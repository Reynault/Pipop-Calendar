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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

    public void launchServer() throws IOException{
        Thread thread;
        // Adresse IP
        InetAddress inet = InetAddress.getByName("localhost");
        // Mise en place du serveur
        listener = new ServerSocket(PORT_NUMBER, GestionnaireClient.LIMITE_CLIENT, inet);
        System.out.println("LAUNCH SERVER");
        // Le serveur attend continuellement un client
        while (true) {
            // On accepte d'un client
            socket = listener.accept();
            // Création du thread lié au client en cours
            thread = GestionnaireClient.getInstance().creerThread(
                    new ConnexionClient(socket));
            // Lancement du thread
            thread.start();
        }
    }

    /**
     * Méthode serveur de création d'un évenement. C'est celle-ci qui est appelée par le client
     * @param nomCalendrier nom du calendrier auquel l'événement est associé
     * @param nom nom de l'événement
     * @param description description de l'événement
     * @param image image visuelle de l'événement
     * @param date date à laquelle l'événement va se dérouler
     * @param lieu lieu à lequel l'événement va se dérouler
     * @param auteur créateur de l'événement
     * @param visible visibilité des événements auprès des autres utilisateurs
     * @return Hashmap indiquant si la requête s'est bien déroulée et si non, l'erreur associé
     */
    public HashMap<String, String> creationEvenement(String nomCalendrier, String nom, String description, String image, String date, String lieu, String auteur, boolean visible) {
        int calendrierID;
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "AddEvent");
        try {
            calendrierID = Calendrier.getCalendrierID(nomCalendrier); // IL nous faut l'ID du calendrier pour la suite, pas son nom
            // nomCalendrier spécifié inexistant : son code d'erreur est 2
            if (calendrierID == -1) {
                res.put("Result", "Calendar doesn't exist");
                return res;
            }
            if (!this.verifierEvenement(auteur, calendrierID, nom)) { // On vérifie que l'événement n'existe pas déjà
                // Données invalides : l'événement existe déjà ; son code d'erreur associé est 1
                res.put("Result", "Event already exists");
                return res;
            }
            if (!this.createEvenement(calendrierID, nom, description, image, date, lieu, auteur, visible)) { // On crée l'événement
                // Pas possible d'insérer le nouvel événement dans la base : erreur de cohérence ; son code d'erreur associé est 3
                res.put("Result", "Couldn't insert new event into database");
                return res;
            }
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        res.put("Result", "Success");
        return res;
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
     * Méthode serveur de suppression d'un événement appelée par le client
     * @param idEv l'ID de l'événement à supprimer
     * @return Hashmap indiquant si la requête s'est bien déroulée et si non, l'erreur associé
     */
    public HashMap<String, String>  suppressionEvenement(int idEv) {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "DeleteEvent");
        try {
            Evenement e = null;
            e = Evenement.find(idEv);
            if (e == null) {
                // Evénement pas trouvé : il n'existe donc pas d'événement associé avec cet ID ; son code d'erreur est 1
                res.put("Result","Event not found");
                return res;
            }
            ArrayList<Utilisateur> alUsrs = e.findInvites(); // Récupération de la liste des participants de l'événement
            this.envoiNotifications(alUsrs); // Notification des utilisateurs
            if (!e.delete()) {
                // Pas de suppression de l'événement dans la BDD : problème de cohérence ; son code d'erreur est 2
                res.put("Result","Couldn't delete event from database");
                return res;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        res.put("Result","Success");
        return res;
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
                res.put("Result","Username already exists");
                break;
            }
            case 2:
            {
                // Cas dans lequel une des données est trop longue
                res.put("Result","Data length too big");
                break;
            }
        }
        return res;
    }
}
