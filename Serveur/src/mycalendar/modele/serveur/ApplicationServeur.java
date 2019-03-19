package mycalendar.modele.serveur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.Calendrier;
import mycalendar.modele.calendrier.Evenement;
import mycalendar.modele.calendrier.EvenementPrive;
import mycalendar.modele.calendrier.EvenementPublic;
import mycalendar.modele.exceptions.MessageCodeException;
import mycalendar.modele.utilisateur.Utilisateur;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Application Serveur qui met en attente le serveur des requêtes des clients
 */
public class ApplicationServeur implements Observer {

    // Numero de port
    public static int PORT_NUMBER = 3307;

    public static int NB_BACKLOG = 100;

    public static String URL = "127.0.0.1";

    // Listener du serveur
    private ServerSocket listener;

    // Socket du client en cours
    private Socket socket;

    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");

    // Instance unique
    private static ApplicationServeur instance = new ApplicationServeur();

    private ApplicationServeur(){
        // Constructeur privé
    }

    /**
     * Getteur du singleton
     * @return l'instance unique
     */
    public static ApplicationServeur getInstance(){
        return instance;
    }

    /**
     * Méthode qui permet d'attendre les clients
     * @throws IOException
     */
    public void launchServer() throws IOException{
        Thread thread;
        // Adresse IP
        InetAddress inet = InetAddress.getByName(URL);
        // Mise en place du serveur
        listener = new ServerSocket(PORT_NUMBER, NB_BACKLOG, inet);
        System.out.println("LAUNCH SERVER");
        // Le serveur attend continuellement un client
        while (true) {
            // On accepte d'un client
            socket = listener.accept();
            // Création du thread lié au client en cours
            thread = new Thread(new ConnexionClient(socket));
            // Lancement du thread
            thread.start();
        }
}

    /**
     * Méthode serveur de création d'un évenement. C'est celle-ci qui est appelée par le client
     * @param nomCalendrier nom du calendrier auquel l'événement est associé
     * @param nom nom de l'événement
     * @param description description de l'événement
     * @param image image de l'événement
     * @param datedeb date de début
     * @param datefin date de fin
     * @param lieu lieu à lequel l'événement va se dérouler
     * @param auteur créateur de l'événement
     * @param visible visibilité des événements auprès des autres utilisateurs
     * @return Hashmap indiquant si la requête s'est bien déroulée et si non, l'erreur associé
     */
    public HashMap<String, String> creationEvenement(String nomCalendrier, String nom, String description, String image, String datedeb, String datefin, String lieu, String auteur, boolean visible) {
        int calendrierID, eventID = -1;
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "AddEvent");
        try {
            calendrierID = Calendrier.getCalendrierID(auteur, nomCalendrier); // Il nous faut l'ID du calendrier pour la suite, pas son nom
            // nomCalendrier spécifié inexistant : son code d'erreur est 2
            if (calendrierID == -1) {
                MessageCodeException.calendar_not_found(res);
                //res.put("Result", MessageCodeException.C_NOT_FOUND);
                //res.put("Message", MessageCodeException.M_CALENDAR_NOT_FOUND);
                return res;
            }
            if (!this.verifierEvenement(auteur, calendrierID, nom)) { // On vérifie que l'événement n'existe pas déjà
                // Données invalides : l'événement existe déjà ; son code d'erreur associé est 1
                MessageCodeException.event_already_exist(res);
                //res.put("Result", MessageCodeException.C_ALREADY_EXIST);
                //res.put("Message", MessageCodeException.M_CALENDAR_ALREADY_EXIST);
                return res;
            }
            if ( (eventID = this.createEvenement(calendrierID, nom, description, image, datedeb, datefin, lieu, auteur, visible)) < 0) { // On crée l'événement
                // Pas possible d'insérer le nouvel événement dans la base : erreur de cohérence ; son code d'erreur associé est 3
                MessageCodeException.bdd_event_error(res);
                //res.put("Result", MessageCodeException.C_ERROR_BDD);
                //res.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
                return res;
            }
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        MessageCodeException.success(res);
        //res.put("Result", MessageCodeException.C_SUCCESS);
        //res.put("Message", MessageCodeException.M_SUCCESS);
        res.put("ID",""+eventID);
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
     * @param image, image de l'événement
     * @param datedeb date de début de l'événement
     * @param datefin date de fin de l'événement
     * @param lieu lieu de l'événement
     * @param auteur créateur de l'événement
     * @param visible visibilité public de l'événement
     * @return 1 si la création s'est bien passé, 0 sinon
     * @throws ParseException
     */
    private int createEvenement(int calendrierID, String nom, String description, String image, String datedeb, String datefin, String lieu, String auteur, boolean visible) throws ParseException, SQLException {
        int res = -1;
        // Date de début
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date dateD = dateFormat.parse(datedeb);
        // Date de fin
        Date dateF = dateFormat.parse(datefin);
        Evenement e;
        int id;
        id = Evenement.getHighestID(); // On récupère l'ID de l'événement le plus élevé afin de créer un ID unique
        if (visible) {
            e = new EvenementPublic(id + 1, calendrierID, nom, description, image, dateD, dateF, lieu, auteur);
        }
        else {
            e = new EvenementPrive(id + 1, calendrierID, nom, description, image, dateD, dateF, lieu, auteur);
        }

        if(e.save()){
            res = e.getId();
        }

        return res;
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
                MessageCodeException.event_not_found(res);
                //res.put("Result", MessageCodeException.C_NOT_FOUND);
                //res.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
                return res;
            }
            ArrayList<Utilisateur> alUsrs = e.findInvites(); // Récupération de la liste des participants de l'événement
            this.envoiNotifications(alUsrs); // Notification des utilisateurs
            if (!e.delete()) {
                // Pas de suppression de l'événement dans la BDD : problème de cohérence ; son code d'erreur est 2
                MessageCodeException.bdd_event_error(res);
                //res.put("Result", MessageCodeException.C_ERROR_BDD);
                //res.put("Message", MessageCodeException.M_EVENT_ERROR_BDD);
                return res;
            }
        } catch (SQLException | ParseException e1) {
            e1.printStackTrace();
        }
        MessageCodeException.success(res);
        //res.put("Result", MessageCodeException.C_SUCCESS);
        //res.put("Message", MessageCodeException.M_SUCCESS);
        return res;
    }

    /**
     * Méthode serveur de modification d'un événement appelée par le client
     * @param idEv l'ID de l'événement à modifier
     * @param calendrierID l'ID du calendrier de l'événement modifier
     * @param description la description de l'événement modifier
     * @param image l'image de l'événement modifier
     * @param datedeb la date de l'événement modifier
     * @param datefin date de fin
     * @param lieu le lieu de l'événement modifier
     * @param auteur l'auteur de l'événement modifier
     * @return Hashmap indiquant si la requête s'est bien déroulée et si non, l'erreur associé
     */
    public HashMap<String, String>  modificationEvenement(int idEv, int calendrierID, String nomE, String description, String image, String datedeb, String datefin, String lieu, String auteur){
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "ModifyEvent");
        try {
            // Date de début
            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date dateD = dateFormat.parse(datedeb);
            // Date de fin
            Date dateF = dateFormat.parse(datefin);
            Evenement e = null;
            e = Evenement.find(idEv);
            if (e == null) {
                // Evénement pas trouvé : il n'existe donc pas d'événement associé avec cet ID ; son code d'erreur est 1
                MessageCodeException.event_not_found(res);
                //res.put("Result", MessageCodeException.C_NOT_FOUND);
                //res.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
                return res;
            }
            if (dateD.before(Calendar.getInstance().getTime())){
                // Date déjà passée
                MessageCodeException.date(res);
                //res.put("Result", MessageCodeException.C_DATE_ERROR);
                //res.put("Message", MessageCodeException.M_DATE_ERROR);
                return res;
            }
            if (!e.modify(calendrierID, nomE, description, image, dateD, dateF, lieu, auteur)) {
                // Pas de suppression de l'événement dans la BDD : problème de cohérence ; son code d'erreur est 2
                MessageCodeException.bdd_event_error(res);
                //res.put("Result", MessageCodeException.C_ERROR_BDD);
                //res.put("Message", MessageCodeException.M_EVENT_ERROR_BDD);
                return res;
            }
        }catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        MessageCodeException.success(res);
        //res.put("Result", MessageCodeException.C_SUCCESS);
        //res.put("Message", MessageCodeException.M_SUCCESS);
        return res;
    }


    /**
     * Méthode serveur de consultation d'un évenement. C'est celle-ci qui est appelée par le client
     * @param idEV ID de l'événement
     * @return Hashmap indiquant si la requête s'est bien déroulée et les données de l'événement demandé et si non, l'erreur associé
     */
    public HashMap<String, String> consultationEvenement(String idEV) {
        int calendrierID;
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "ConsultEvent");
        try {
            //calendrierID = Calendrier.getCalendrierID(auteur, nomCalendrier); // IL nous faut l'ID du calendrier pour la suite, pas son nom
            // nomCalendrier spécifié inexistant : son code d'erreur est 2
            Evenement e = Evenement.find(Integer.parseInt(idEV));
            if (e == null) {
                MessageCodeException.event_not_found(res);
                //res.put("Result", MessageCodeException.C_NOT_FOUND);
                //res.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
                return res;
            }
            else{
                MessageCodeException.success(res);
                //res.put("Result", MessageCodeException.C_SUCCESS);
                //res.put("Message", MessageCodeException.M_SUCCESS);
                res.putAll(e.consult());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
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
			Connection connect = GestionnaireBDD.getInstance().getConnection();
			String request = "SELECT * FROM utilisateur_calendrier WHERE Email = ? ;";
			PreparedStatement prep = connect.prepareStatement(request);
			prep.setString(1, email);
			ResultSet result = prep.executeQuery();
			MessageCodeException.success(res);
            //res.put("Result", MessageCodeException.C_SUCCESS);
            //res.put("Message", MessageCodeException.M_SUCCESS);
        }else{
            // Utilisateur non trouvé
            MessageCodeException.user_not_found(res);
            //res.put("Result", MessageCodeException.C_NOT_FOUND);
            //res.put("Message", MessageCodeException.M_USER_NOT_FOUND);
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
                MessageCodeException.success(res);
                //res.put("Result", MessageCodeException.C_SUCCESS);
                //res.put("Message", MessageCodeException.M_SUCCESS);
                break;
            }
            case 0:
            {
                // Utilisateur déjà existant
                MessageCodeException.user_already_exist(res);
                //res.put("Result", MessageCodeException.C_ALREADY_EXIST);
                //res.put("Message", MessageCodeException.M_USER_ALREADY_EXIST);
                break;
            }
            case 2:
            {
                // Cas dans lequel une des données est trop longue
                MessageCodeException.size_error(res);
                //res.put("Result", MessageCodeException.C_SIZE_ERROR);
                //res.put("Message", MessageCodeException.M_SIZE_ERROR);
                break;
            }
        }
        return res;
    }

    /**
     * Méthode qui permet de charger la liste des calendriers de l'utilisateur
     * @param email email de l'utilisateur
     * @return Hashmap qui contient les données
     * @throws SQLException
     */
    public HashMap<String, Object> loadCalendars(String email){
        HashMap<String, Object> res = new HashMap<>();
        HashMap<String, Object> res1 = new HashMap<>();
        // Récupération des calendriers
        try {
            ArrayList<Calendrier> calendriers = Utilisateur.findCalendriers(email);
            if (calendriers.size() == 0) {
                res.put("Result", MessageCodeException.C_NOT_FOUND);
                res.put("Message", MessageCodeException.M_CALENDAR_NOT_FOUND);
            } else {
                res.put("Result", MessageCodeException.C_SUCCESS);
                res.put("Message", MessageCodeException.M_SUCCESS);
                HashMap<String, String> calendars;
                Calendrier c;
                // Pour chaque calendrier, on l'ajoute dans la hashmap
                for (int i = 0; i < calendriers.size(); i++) {
                    calendars = new HashMap<>();
                    c = calendriers.get(i);
                    calendars.put("ID", "" + c.getIdC());
                    calendars.put("Nom", c.getNomCalendrier());
                    calendars.put("Couleur", c.getCouleur());
                    res1.put("" + i, calendars);
                }
                res.put("Data", res1);
            }
        }catch (SQLException e){
            res.put("Result", MessageCodeException.C_ERROR_BDD);
            res.put("Message", MessageCodeException.M_BDD_ERROR);
        }
        return res;
    }

    /**
     * Méthode qui permet de charger un calendrier
     * @param idCalendrier
     * @return
     * @throws SQLException
     */
    public HashMap<String, String> consultCalendar(int idCalendrier) throws SQLException{
        HashMap<String, String> res = new HashMap<String, String>();
        Calendrier c = Calendrier.find(idCalendrier);
        if( c == null){
            MessageCodeException.calendar_not_found(res);
            //res.put("RESULT", MessageCodeException.C_NOT_FOUND);
            //res.put("MESSAGE", MessageCodeException.M_CALENDAR_NOT_FOUND);
        }else{
            MessageCodeException.success(res);
            //res.put("RESULT", MessageCodeException.C_SUCCESS);
            //res.put("MESSAGE", MessageCodeException.M_SUCCESS);
            res.put("Nom", c.getNomCalendrier());
            res.put("Description", c.getDescription().toString());
            res.put("Couleur", c.getCouleur());
            res.put("Theme", c.getTheme());
        }
        return res;
    }

    /**
     * Méthode serveur de création d'un calendrier. C'est celle-ci qui est appelée par le client
     * @param nomCalendrier nom du calendrier
     * @param description description du calendrier
     * @param couleur couleur du calendrier
     * @param theme theme du calendrier
     * @return Hashmap indiquant si la requête s'est bien déroulée et si non, l'erreur associé
     */
    public HashMap<String, String> creationCalendrier(String nomCalendrier, String description, String couleur, String theme, String auteur) {
        HashMap<String, String> res = new HashMap<>();
        int id = -1;
        res.put("Request", "CreateCalendar");
        try {
            if (!this.verifierCalendrier(auteur, nomCalendrier)) { // On vérifie que le calendrier n'existe pas déjà
                MessageCodeException.calendar_already_exist(res);
                //res.put("Result", MessageCodeException.C_ALREADY_EXIST);
                //res.put("Message", MessageCodeException.M_CALENDAR_ALREADY_EXIST);
                return res;
            }
            id = this.creerCalendrier(nomCalendrier, description, couleur, theme, auteur);
            if ( id < 0) { // On crée le calendrier
                MessageCodeException.bdd_calendar_error(res);
                //res.put("Result", MessageCodeException.C_ERROR_BDD);
                //res.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
                return res;
            }
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        MessageCodeException.success(res);
        //res.put("Result", MessageCodeException.C_SUCCESS);
        //res.put("Message", MessageCodeException.M_SUCCESS);
        res.put("ID", ""+id);
        return res;
    }

    /**
     * Méthode de vérification de l'existence d'un calendrier
     * @param email créateur du calendrier à vérifier
     * @param nomCalendrier nom du calendrier à vérifier
     * @return true si l'événement n'existe pas, false sinon
     * @throws SQLException
     */
    private boolean verifierCalendrier(String email, String nomCalendrier) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "SELECT * FROM Calendrier WHERE nomC=?";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setString(1, nomCalendrier);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            return !rs.next();
        }
    }

    /**
     * Méthode de création d'un calendrier
     * @param nomCalendrier nom du calendrire
     * @param description description du calendrier
     * @param couleur couleur du calendrier
     * @param theme theme du calendrier
     * @return 1 si la création s'est bien passé, 0 sinon
     * @throws ParseException, SQLException
     */
    private int creerCalendrier(String nomCalendrier, String description, String couleur, String theme, String auteur) throws ParseException, SQLException {
        Calendrier c;
        int id;
        int res = -1;
        id = Calendrier.getHighestID(); // On récupère l'ID de l'événement le plus élevé afin de créer un ID unique
        c = new Calendrier(id+1,nomCalendrier, couleur, description, theme, auteur);
        if(c.save()){
            res = id;
        }
        return res;
    }

    /**
     * Méthode serveur de suppression d'un calendrier appelée par le client
     * @param email email de l'auteur
     * @param idC l'ID du calendrier à supprimer
     * @param b booléen concernant la suppression ou non des événements
     * @return Hashmap indiquant si la requête s'est bien déroulée et si non, l'erreur associé
     */
    public HashMap<String, String>  suppressionCalendrier(String email, int idC, boolean b) {


        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "DeleteCalendar");
        try {
           //b a true si on souhaire supprimer les evenements liés au calendrier
           if(b) {
               //on veut recuperer la liste des evenements appartenant au calendrier
               // dans le but de les supprimer
               ArrayList<Evenement> events = null;
               events = Evenement.find(idC, email);
               if (events.size() == 0) {
                   MessageCodeException.event_not_found(res);
                   //res.put("Result", MessageCodeException.C_NOT_FOUND);
                   //res.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
                   return res;
               } else {
                   // pour chaque evenement , on recherche les calendriers associés
                   for (Evenement e : events) {
                       if (e.getAdmin()) {
                           ArrayList<Calendrier> calendars = getCalendars(e);
                           // pour chaque calendrier trouvé , on supprime l'événement
                           for (Calendrier c : calendars) {
                               c.deleteEvent(e);
                           }
                       } else {
                           e.delete();
                       }
                   }
               }
               // on cherche le calendrier dans la base et le supprime
               Calendrier c = Calendrier.find(idC);

               if (!c.delete()) {
                   MessageCodeException.bdd_calendar_error(res);
                   //res.put("Result", MessageCodeException.C_ERROR_BDD);
                   //res.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
                   return res;
               }
           }
               // b a false quand on veut juste supprimer le calendrier, sans supprimer les evenements
               else{
                   // on cherche le calendrier dans la base et le supprime
                   Calendrier c = Calendrier.find(idC);

                   if (!c.delete()) {
                       MessageCodeException.bdd_calendar_error(res);
                       //res.put("Result", MessageCodeException.C_ERROR_BDD);
                       //res.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
                       return res;
                   }
               }
           } catch (SQLException e1) {
               e1.printStackTrace();
           }
        MessageCodeException.success(res);
        //res.put("Result", MessageCodeException.C_SUCCESS);
        //res.put("Message", MessageCodeException.M_SUCCESS);
        return res;
    }

    /**
     * Methode qui recherche les calendriers liés à un événement
     * @param e evenement en question
     * @return liste des calendriers liés à l'événement
     * @throws SQLException
     */
    private ArrayList<Calendrier> getCalendars(Evenement e) throws SQLException {
        ArrayList<Calendrier> cal = Calendrier.find(e, e.getId());
        return cal;
    }


    /**
     * Modification d'un calendrier
     * @param id id du calendrier
     * @param nom nouveau nom du calendrier
     * @param couleur nouvelle couleur du calendrier
     * @return Hashmap indiquant si la requête s'est bien déroulée ou sinon l'erreur associée
     * @throws SQLException
     */
    public HashMap<String, String> modificationCalendrier(int id, String nom, String couleur) throws SQLException {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "ModifyCalendar");
        // Le calendrier n'existe pas
        if(!GestionnaireBDD.verifierExistenceCalendrier(id)){
            MessageCodeException.calendar_not_found(res);
            //res.put("Result", MessageCodeException.C_NOT_FOUND);
            //res.put("Message", MessageCodeException.M_CALENDAR_NOT_FOUND);
            return res;
        }
        // Aucune modification effectuée
        else if(Calendrier.modificationCalendrier(id, nom, couleur) == 0) {
            MessageCodeException.no_change(res);
            //res.put("Result", MessageCodeException.C_NO_CHANGE);
            //res.put("Message", MessageCodeException.M_NO_CHANGE);
            return res;
        }
        // Erreur lors de la modification
        else if(Calendrier.modificationCalendrier(id, nom, couleur) < 0) {
            MessageCodeException.bdd_calendar_error(res);
            //res.put("Result", MessageCodeException.C_ERROR_BDD);
            //res.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
        }
        // La calendrier a bien été modifié
        else {
            //envoiNotifications(Calendrier.findInvites(id));
            MessageCodeException.success(res);
            //res.put("Result", MessageCodeException.C_SUCCESS);
            //res.put("Message", MessageCodeException.M_SUCCESS);
        }
        return res;
    }

    /**
     * Envoi d'une notification a tous les utilisateurs du calendrier
     * @param alu liste des utilisateurs
     */
    private void envoiNotifications(ArrayList<Utilisateur> alu) throws ParseException {
        System.out.println("envoie notif avant");
        //String date = LocalDate.now().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));  // HH MM JJ MM AAAA
        DateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date date = df.parse("01:01 11/11/2019");
        System.out.println("après avoir récup la date");
        for(Utilisateur u : alu) {
            //TODO Modifier l'id
            //TODO Envoyer la notification
            //Notif n = new NotifiCalendrier(0, u.getEmail(), "Modification", "Un calendrier a été modifié", da);
        }
    }

    public HashMap<String, Object> getUtilisateurs(String nom, String prenom) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("Request", "GetUsers");
        try {
            ArrayList<Utilisateur> ul = Utilisateur.find(nom, prenom);
            if (ul.size() == 0){
                res.put("Result", MessageCodeException.C_NOT_FOUND);
                res.put("Message", MessageCodeException.M_USER_NOT_FOUND);
            }else{
                HashMap<String, String> users = new HashMap<>();
                for(int j = 0; j < ul.size(); j++){
                    Utilisateur u = ul.get(j);
                    users.put("Email", u.getEmail());
                    users.put("Nom", u.getNom());
                    users.put("Prenom", u.getPrenom());
                    res.put(""+j, users);
                }
                res.put("Result", MessageCodeException.C_SUCCESS);
                res.put("Message", MessageCodeException.M_SUCCESS);
            }
        } catch (SQLException e) {
            res.put("Result", MessageCodeException.C_ERROR_BDD);
            res.put("Message", MessageCodeException.M_BDD_ERROR);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Getter sur un calendrier
     * @param id id du calendrier
     * @return calendrier correspondant
     * @throws SQLException
     */
    public Calendrier getCalendrier(int id) throws SQLException {
        return Calendrier.find(id);
    }


    public HashMap<String, Object> getThemes() {
        HashMap<String, Object> themes = new HashMap<>();
        HashMap<String, String> donnees = new HashMap<>();
        try {
            ArrayList<String> res = Calendrier.getThemes();
            if(res.size() > 0){
                themes.put("Result", MessageCodeException.C_SUCCESS);
                themes.put("Message", MessageCodeException.M_SUCCESS);
                for (int i = 0 ; i < res.size() ; i ++){
                    donnees.put(""+i,res.get(i));
                }
                themes.put("Data", donnees);
            }else{
                themes.put("Result", MessageCodeException.C_NOT_FOUND);
                themes.put("Message", MessageCodeException.M_THEME_NOT_FOUND);
            }
        }catch (SQLException e){
            themes.put("Result", MessageCodeException.C_ERROR_BDD);
            themes.put("Message", MessageCodeException.M_BDD_ERROR);
        }
        return themes;
    }

    public HashMap<String, Object> loadEvents(String auteur, String nomCalendrier) {
        HashMap<String, Object> calendriers = new HashMap<>();
        calendriers.put("Request", "LoadEvents");
        try {
            int calendrierID = Calendrier.getCalendrierID(auteur, nomCalendrier);
            ArrayList<Evenement> donnees = Evenement.find(calendrierID, auteur);
            if (donnees.size() > 0) {
                HashMap<String, String> events = new HashMap<>();
                for (int j = 0 ; j < donnees.size() ; j++){
                    Evenement u = donnees.get(j);
                    events.put("EventName", u.getNomE());
                    events.put("Description", u.getDescription());
                    events.put("Picture", u.getImage());
                    events.put("Date", u.getDatedeb().toString());
                    events.put("DateFin", u.getDatefin().toString());
                    events.put("EventLocation", u.getLieu());
                    events.put("EventAuthor", u.getAuteur());
                    calendriers.put("" + j, events);
                }
                calendriers.put("Result", MessageCodeException.C_SUCCESS);
                calendriers.put("Message", MessageCodeException.M_SUCCESS);
            }
            else {
                calendriers.put("Result", MessageCodeException.C_NOT_FOUND);
                calendriers.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
            }
        } catch (SQLException e) {
            calendriers.put("Result", MessageCodeException.C_ERROR_BDD);
            calendriers.put("Message", MessageCodeException.M_BDD_ERROR);
            e.printStackTrace();
        }
        return calendriers;
    }

}
