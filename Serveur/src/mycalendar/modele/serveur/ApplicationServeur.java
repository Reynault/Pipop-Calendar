package mycalendar.modele.serveur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.Calendrier;
import mycalendar.modele.calendrier.Evenement;
import mycalendar.modele.calendrier.EvenementPrive;
import mycalendar.modele.calendrier.EvenementPublic;
import mycalendar.modele.utilisateur.Utilisateur;

import javax.xml.transform.Result;
import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.utilisateur.Utilisateur;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class ApplicationServeur implements Observer {

    public static int PORT_NUMBER = 3306;

    private ServerSocket listener;
    private Socket socket;

    private static ApplicationServeur instance = new ApplicationServeur();

    private ApplicationServeur(){

    }

    public static ApplicationServeur getInstance(){
        return instance;
    }

    public void launchServer() throws IOException, InterruptedException{
        Thread thread;
        // LIGNE A MODIFIER POUR METTRE SUR LE SERVEUR
        InetAddress inet = InetAddress.getLocalHost();
        listener = new ServerSocket(PORT_NUMBER, GestionnaireClient.LIMITE_CLIENT, inet);
        System.out.println("The server is running...");
        // Le serveur attend continuellement un client
        while (true) {
            // On accepte d'un client
            socket = listener.accept();
            System.out.println("On accepte le client.");
            // Création du thread lié au client en cours
            thread = GestionnaireClient.getInstance().creerThread(
                    new ConnexionClient(socket));
            // Lancement du thread
            thread.start();
            System.out.println("Je continue d'attendre des clients.");
        }
    }

    /**
     * Méthode serveur de création d'un évenement. C4est celle-ci appelée par le client
     * @param nomCalendrier nom du calendrier auquel l'événement est associé
     * @param nom nom de l'événement
     * @param description description de l'événement
     * @param image image visuelle de l'événement
     * @param date date à laquelle l'événement va se dérouler
     * @param lieu lieu à lequel l'événement va se dérouler
     * @param auteur créateur de l'événement
     * @param visible visibilité des événements auprès des autres utilisateurs
     * @return code d'erreur (0 si tout s'est bien passé, > 0 si une erreur s'est produite)
     */
    public int creationEvenement(String nomCalendrier, String nom, String description, String image, String date, String lieu, String auteur, boolean visible) {
        int calendrierID;
        try {
            calendrierID = Calendrier.getCalendrierID(nomCalendrier); // IL nous faut l'ID du calendrier pour la suite, pas son nom
            // nomCalendrier spécifié inexistant : son code d'erreur est 2
            if (calendrierID == -1) {
                return 2;
            }
            if (!this.verifierEvenement(auteur, calendrierID, nom)) { // On vérifie que l'événement n'existe pas déjà
                // Données invalides : l'événement existe déjà ; son code d'erreur associé est 1
                return 1;
            }
            if (!this.createEvenement(calendrierID, nom, description, image, date, lieu, auteur, visible)) { // On crée l'événement
                // Pas possible d'insérer le nouvel événement dans la base : erreur de cohérence ; son code d'erreur associé est 3
                return 3;
            }
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Méthode de vérification de l'existence d'un événement
     * @param email créateur de l'événement à vérifier
     * @param calendrierID ID du calendrier à vérifier
     * @param nom nom de l'événement à vérifier
     * @return true si l'événement n'existe pas, false sinon
     * @throws SQLException
     */
    private boolean verifierEvenement(String email, int calendrierID, String nom) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "SELECT * FROM Evenement WHERE nomE=? AND auteur=? AND idc=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setString(1, nom);
            prep.setString(2, email);
            prep.setInt(3, calendrierID);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            return !rs.next();
        }
    }

    /**
     * Méthode de création d'un événement
     * @param calendrierID ID du calendrier associé à l'événement
     * @param nom nom de l'événement
     * @param description description de l'événement
     * @param image image de l'événement
     * @param date date de l'événement
     * @param lieu lieu de l'événement
     * @param auteur créateur de l'événement
     * @param visible visibilité public de l'événement
     * @return 1 si la création s'est bien passé, 0 sinon
     * @throws ParseException
     */
    private boolean createEvenement(int calendrierID, String nom, String description, String image, String date, String lieu, String auteur, boolean visible) throws ParseException, SQLException {
        // On parse la date afin d'en créer un objet utilisable
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date dateP = df.parse(date);
        Evenement e;
        int id;
        id = Evenement.getHighestID(); // On récupère l'ID de l'événement le plus élevé afin de créer un ID unique
        if (visible) {
            e = new EvenementPublic(id + 1, calendrierID, nom, description, image, dateP, lieu, auteur);
        }
        else {
            e = new EvenementPrive(id + 1, calendrierID, nom, description, image, dateP, lieu, auteur);
        }
        return e.save();
    }

    /**
     * Méthode de suppression d'un événement
     * @param idEv l'ID de l'événement à supprimer
     * @return 1 si l'ID spécifié n'a pas d'événements associé, 2 si erreur de cohérence dans la BDD, 0 lors d'une suppression avec succès
     */
    public int suppressionEvenement(int idEv) {
        try {
            Evenement e = null;
            e = Evenement.find(idEv);
            if (e == null) {
                // Evénement pas trouvé : il n'existe donc pas d'événement associé avec cet ID ; son code d'erreur est 1
                return 1;
            }
            ArrayList<Utilisateur> alUsrs = e.findInvites(); // Récupération de la liste des participants de l'événement
            this.envoiNotifications(alUsrs); // Notification des utilisateurs
            if (!e.delete()) {
                // Pas de suppression de l'événement dans la BDD : problème de cohérence ; son code d'erreur est 2
                return 2;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return 0;
    }

    //TODO coder cette méthode quand les notifications seront faites
    public int envoiNotifications(ArrayList<Utilisateur> utilisateurs) {

        return 0;
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    /**
     * Lorsqu'un utilisateur souhaite se connecter, cette fonction teste s'il est présent dans la base de données.
     * S'il est déjà présent alors la connexion sera possible.
     * @param email L'email de l'utilisateur
     * @param mdp Le mot de passe de l'utilisateur
     * @return 0 si la connexion a échoué, 1 si c'est réussi
     * @throws SQLException
     */
    public HashMap<String, String> authentification(String email, String mdp) throws SQLException {
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("Request","SignIn");
        // Vérification de la connexion
        if(Utilisateur.verifierConnexion(email, mdp)){
            // Récupération des calendriers de l'utilisateur

        }else{
            // Utilisateur non trouvé
            res.put("Result","User not found");
        }
        return res;
    }

    /**
     * Méthode inscription qui correspond à une inscription d'un utilisateur dans la base de données
     * @param email email de l'utilisateur
     * @param mdp mot de passe chiffré
     * @param prenom prénom de l'utilisateur
     * @param nom nom de l'utilisateur
     * @return une hashmap qui contient les informations à envoyer au client
     * @throws SQLException
     */
    public HashMap<String, String> inscription(String email, String mdp, String prenom, String nom) throws SQLException{
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("Request","SignIn");
        // Vérification de l'inscription
        switch(Utilisateur.verifierInscription(email, mdp, prenom, nom)){
            case 1:
            {
                // Inscription réussie
                res.put("Result","Success");
                break;
            }
            case 0:
            {
                // Utilisateur déjà existant
                res.put("Result","Username already exist");
                break;
            }
            case 2:
            {
                // Cas dans lequel une des données est trop longue
                res.put("Result","Données trop longues");
                break;
            }
        }
        return res;
    }
}
