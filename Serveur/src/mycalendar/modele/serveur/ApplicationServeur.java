package mycalendar.modele.serveur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.Evenement;
import mycalendar.modele.calendrier.EvenementPrive;
import mycalendar.modele.calendrier.EvenementPublic;

import javax.xml.transform.Result;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
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
     * @param id ID de l'événement
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
    public int creationEvenement(int id, String nomCalendrier, String nom, String description, String image, String date, String lieu, String auteur, boolean visible) {
        int calendrierID = 0;
        try {
            calendrierID = this.getCalendrierID(nomCalendrier);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // nomCalendrier spécifié inexistant : son code d'erreur est 2
        if (calendrierID == -1) {
            return 2;
        }
        try {
            if (!this.verifierEvenement(auteur, calendrierID, nom)) {
                // Données invalides : l'événement existe déjà ; son code d'erreur associé est 1
                return 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            // Pas possible d'insérer le nouvel événement dans la base : erreur de cohérence ; son code d'erreur associé est 3
            if (!this.createEvenement(id, calendrierID, nom, description, image, date, lieu, auteur, visible)) {
                return 3;
            }
        } catch (ParseException e) {
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
            if (rs.next()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Récupère l'ID d'un calendrier associé à son nom
     * @param nomCalendrier nom du calendrier
     * @return l'ID du calendrier récupéré (-1 si le calendrier n'existe pas)
     * @throws SQLException
     */
    private int getCalendrierID(String nomCalendrier) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "SELECT idc FROM Calendrier WHERE nomC=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setString(1, nomCalendrier);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            if (rs.next()) {
                return rs.getInt("idc");
            }
        }
        return -1;
    }

    /**
     * Méthode de création d'un événement
     * @param id ID de l'événement
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
    private boolean createEvenement(int id, int calendrierID, String nom, String description, String image, String date, String lieu, String auteur, boolean visible) throws ParseException {
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date dateP = df.parse(date);
        Evenement e;
        if (visible) {
            e = new EvenementPublic(id, calendrierID, nom, description, image, dateP, lieu, auteur);
        }
        else {
            e = new EvenementPrive(id, calendrierID, nom, description, image, dateP, lieu, auteur);
        }
        try {
            if (!e.save()) {
              return false;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return true;
    }

    public int suppressionEvenement(int idEv) {
        Evenement e = null;
        try {
            e = Evenement.find(idEv);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        if (e == null) {
            // Evenement pas trouvé : il n'existe donc pas d'événement associé avec cet ID ; son code d'erreur est 1
            return 1;
        }
        try {
            if (!e.delete()) {
                // Pas de suppression de l'événement dans la BDD : problème de cohérence ; son code d'erreur est 2
                return 2;
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return 0;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
