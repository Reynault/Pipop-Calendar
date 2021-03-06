package mycalendar.modele.serveur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.*;
import mycalendar.modele.exceptions.BadRequestExeption;
import mycalendar.modele.exceptions.MessageCodeException;
import mycalendar.modele.utilisateur.GroupeAmi;
import mycalendar.modele.utilisateur.Utilisateur;

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

    private static DateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy HH:mm");

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
     * @param couleur couleur liée à l'événement
     * @param auteur créateur de l'événement
     * @param visible visibilité des événements auprès des autres utilisateurs
     * @return Hashmap indiquant si la requête s'est bien déroulée et si non, l'erreur associé
     */
    public HashMap<String, String> creationEvenement(String nomCalendrier, String nom, String description, String image, String datedeb, String datefin, String lieu, String couleur, String auteur, boolean visible) {
        int calendrierID, eventID = -1;
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "AddEvent");
        try {
            ArrayList<String> d = new ArrayList<>();
            d.add(nom);
            d.add(nomCalendrier);
            if(Verification.checkEmptyData(d)){
                if(Verification.checkMail(auteur)) {
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
                    if ((eventID = this.createEvenement(calendrierID, nom, description, image, datedeb, datefin, lieu, couleur, auteur, visible)) < 0) { // On crée l'événement
                        // Pas possible d'insérer le nouvel événement dans la base : erreur de cohérence ; son code d'erreur associé est 3
                        MessageCodeException.bdd_event_error(res);
                        //res.put("Result", MessageCodeException.C_ERROR_BDD);
                        //res.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
                        return res;
                    }
                }else{
                    MessageCodeException.invalid_email(res);
                    System.out.println(auteur);
                    return res;
                }
            }else{
                MessageCodeException.empty_data(res);
                return res;
            }
        } catch (ParseException | SQLException e) {
            MessageCodeException.date_parse_error(res);
            return res;
        } catch (BadRequestExeption e){
            MessageCodeException.date(res);
            return res;
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
    private int createEvenement(int calendrierID, String nom, String description, String image, String datedeb, String datefin, String lieu, String couleur, String auteur, boolean visible) throws ParseException, SQLException {
        int res = -1;
        // Date de début
        Date dateD = dateFormat.parse(datedeb);
        // Date de fin
        Date dateF = dateFormat.parse(datefin);
        if(Verification.checkDate(dateD, dateF)) {
            Evenement e;
            int id;
            id = Evenement.getHighestID(); // On récupère l'ID de l'événement le plus élevé afin de créer un ID unique
            if (visible) {
                e = new EvenementPublic(id + 1, calendrierID, nom, description, image, dateD, dateF, lieu, couleur, auteur);
            } else {
                e = new EvenementPrive(id + 1, calendrierID, nom, description, image, dateD, dateF, lieu, couleur, auteur);
            }

            if (e.save()) {
                res = e.getId();
            }
        }else{
            throw new BadRequestExeption("Dates non valides");
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
            MessageCodeException.date_parse_error(res);
            return res;
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
    public HashMap<String, String>  modificationEvenement(int idEv, int calendrierID, String nomE, String description, String image, String datedeb, String datefin, String lieu, String couleur, String auteur){
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "ModifyEvent");
        try {
            // Date de début
            Date dateD = dateFormat.parse(datedeb);
            // Date de fin
            Date dateF = dateFormat.parse(datefin);
            if(!Verification.checkDate(dateD, dateF)){
                throw new BadRequestExeption("Date non valide");
            }
            Evenement e = null;
            ArrayList<String> ver = new ArrayList<String>();
            ver.add(nomE);
            if(Verification.checkEmptyData(ver)){
                if(Verification.checkMail(auteur)) {
                    e = Evenement.find(idEv);
                    if (e == null) {
                        // Evénement pas trouvé : il n'existe donc pas d'événement associé avec cet ID ; son code d'erreur est 1
                        MessageCodeException.event_not_found(res);
                        //res.put("Result", MessageCodeException.C_NOT_FOUND);
                        //res.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
                        return res;
                    }
                    if (!e.modify(calendrierID, nomE, description, image, dateD, dateF, lieu, couleur, auteur)) {
                        // Pas de suppression de l'événement dans la BDD : problème de cohérence ; son code d'erreur est 2
                        MessageCodeException.bdd_event_error(res);
                        //res.put("Result", MessageCodeException.C_ERROR_BDD);
                        //res.put("Message", MessageCodeException.M_EVENT_ERROR_BDD);
                        return res;
                    }
                }else{
                    MessageCodeException.invalid_email(res);
                    return res;
                }
            }else{
                MessageCodeException.empty_data(res);
                return res;
            }
        } catch (BadRequestExeption e){
            MessageCodeException.date(res);
            return res;
        } catch(SQLException e1) {
            MessageCodeException.bdd_error(res);
            return res;
        } catch (ParseException e) {
            MessageCodeException.date_parse_error(res);
            return res;
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
            MessageCodeException.bdd_event_error(res);
        } catch (ParseException e){
            MessageCodeException.date_parse_error(res);
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
        if(Verification.checkMail(email)) {
            if (Utilisateur.verifierConnexion(email, mdp)) {
                // Récupération des calendriers de l'utilisateur
                Connection connect = GestionnaireBDD.getInstance().getConnection();
                String request = "SELECT * FROM utilisateur_calendrier WHERE Email = ? ;";
                PreparedStatement prep = connect.prepareStatement(request);
                prep.setString(1, email);
                ResultSet result = prep.executeQuery();
                MessageCodeException.success(res);
                //res.put("Result", MessageCodeException.C_SUCCESS);
                //res.put("Message", MessageCodeException.M_SUCCESS);
            } else {
                // Utilisateur non trouvé
                MessageCodeException.user_not_found(res);
                return res;
                //res.put("Result", MessageCodeException.C_NOT_FOUND);
                //res.put("Message", MessageCodeException.M_USER_NOT_FOUND);
            }
        }else{
            MessageCodeException.invalid_email(res);
            return res;
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
        if(Verification.checkMail(email)) {
            switch (Utilisateur.verifierInscription(email, mdp, prenom, nom)) {
                case 1: {
                    // Inscription réussie
                    MessageCodeException.success(res);
                    //res.put("Result", MessageCodeException.C_SUCCESS);
                    //res.put("Message", MessageCodeException.M_SUCCESS);
                    break;
                }
                case 0: {
                    // Utilisateur déjà existant
                    MessageCodeException.user_already_exist(res);
                    //res.put("Result", MessageCodeException.C_ALREADY_EXIST);
                    //res.put("Message", MessageCodeException.M_USER_ALREADY_EXIST);
                    break;
                }
                case 2: {
                    // Cas dans lequel une des données est trop longue
                    MessageCodeException.size_error(res);
                    //res.put("Result", MessageCodeException.C_SIZE_ERROR);
                    //res.put("Message", MessageCodeException.M_SIZE_ERROR);
                    break;
                }
            }
        }else{
            MessageCodeException.invalid_email(res);
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
            ArrayList<String> verif = new ArrayList<>();
            verif.add(nomCalendrier);
            if(Verification.checkEmptyData(verif)) {
                if(Verification.checkMail(auteur)) {
                    /*if (!this.verifierCalendrier(auteur, nomCalendrier)) { // On vérifie que le calendrier n'existe pas déjà
                        MessageCodeException.calendar_already_exist(res);
                        //res.put("Result", MessageCodeException.C_ALREADY_EXIST);
                        //res.put("Message", MessageCodeException.M_CALENDAR_ALREADY_EXIST);
                        return res;
                    }*/
                    id = this.creerCalendrier(nomCalendrier, description, couleur, theme, auteur);
                    if (id < 0) { // On crée le calendrier
                        MessageCodeException.bdd_calendar_error(res);
                        //res.put("Result", MessageCodeException.C_ERROR_BDD);
                        //res.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
                        return res;
                    }
                }else{
                    MessageCodeException.invalid_email(res);
                    return res;
                }
            }else{
                MessageCodeException.empty_data(res);
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
               ArrayList<Evenement> events;
               events = Evenement.find(idC, email);
               if (events.size() == 0) {
                   // On ne supprime pas d'événements
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
                   System.out.println("On a PAS réussi à suppr le calendrier");
                   MessageCodeException.bdd_calendar_error(res);
                   //res.put("Result", MessageCodeException.C_ERROR_BDD);
                   //res.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
                   return res;
               }
               else {
                   System.out.println("On a réussi à suppr le calendrier");
               }
           }
        } catch (SQLException e1) {
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
    public HashMap<String, String> modificationCalendrier(int id, String nom, String couleur, String theme, String description) throws SQLException {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "ModifyCalendar");
        // Le calendrier n'existe pas
        ArrayList<String> verif = new ArrayList<>();
        verif.add(nom);
        verif.add(couleur);
        verif.add(theme);
        verif.add(description);
        if(Verification.checkEmptyData(verif)) {
            if (!GestionnaireBDD.verifierExistenceCalendrier(id)) {
                MessageCodeException.calendar_not_found(res);
                //res.put("Result", MessageCodeException.C_NOT_FOUND);
                //res.put("Message", MessageCodeException.M_CALENDAR_NOT_FOUND);
                return res;
            }
            // Aucune modification effectuée
            else if (Calendrier.modificationCalendrier(id, nom, couleur, theme, description) == 0) {
                MessageCodeException.no_change(res);
                //res.put("Result", MessageCodeException.C_NO_CHANGE);
                //res.put("Message", MessageCodeException.M_NO_CHANGE);
                return res;
            }
            // Erreur lors de la modification
            else if (Calendrier.modificationCalendrier(id, nom, couleur, theme, description) < 0) {
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
        }else{
            MessageCodeException.empty_data(res);
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
        //Date date = dateFormat.parse("01:01 11/11/2019");
        System.out.println("après avoir récup la date");
        for(Utilisateur u : alu) {
            //TODO Modifier l'id
            //TODO Envoyer la notification
            //Notif n = new NotifiCalendrier(0, u.getEmail(), "Modification", "Un calendrier a été modifié", da);
        }
    }

    /**
     * Ajout d'un utilisateur en ami
     * @param email1 email de l'utilisateur actif
     * @param email2 email de l'utilisateur a ajouter
     */
    public HashMap<String, String> ajoutAmi(String email1, String email2) throws SQLException {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "AddFriend");
        if(!GestionnaireBDD.verifierAjoutAmi(email1, email2)){
            // Les 2 utilisateurs ne sont pas amis, donc on les ajoute
            if(Utilisateur.ajouterAmi(email1, email2) == 1) {
                // Les utilisateurs sont devenus amis
                res.put("Result", MessageCodeException.C_SUCCESS);
                res.put("Message", MessageCodeException.M_SUCCESS);
            }
            else {
                // Les utilisateurs n'ont pas pu devenir amis
                res.put("Result", MessageCodeException.C_ERROR_BDD);
                res.put("Message", MessageCodeException.M_FRIEND_ERROR_BDD);
            }
        }
        else {
            // Les utilisateurs sont déjà amis
            res.put("Result", MessageCodeException.C_ALREADY_EXIST);
            res.put("Message", MessageCodeException.M_FRIEND_ALREADY_EXIST);
        }
        System.out.println("ajoutAmi de AppliServ : " + res);
        return res;
    }

    /**
     * Creation d'un groupe d'amis
     * @param amis liste des amis a ajouter
     * @param nomGroupe nom du groupe
     * @return HashMap correspondant au resultat de la requête
     * @throws SQLException
     */
    //TODO Création d'un groupe d'amis
    public HashMap<String, Object> creerNouveauGroupeAmis(ArrayList<String> amis, String nomGroupe) throws SQLException {
        HashMap<String, Object> res = new HashMap<>();
        res.put("Request", "CreateFriendsGroup");
        ArrayList<GroupeAmi> groupe = GroupeAmi.find(nomGroupe);

        if(groupe.size() != 0){
            res.put("Result", MessageCodeException.C_ALREADY_EXIST);
            res.put("Message", MessageCodeException.M_GROUP_ALREADY_EXIST);
        }
        else{
            // Groupe inexistant, il peut être créé
            int id = GroupeAmi.getHighestID(); // On récupère l'ID de l'événement le plus élevé afin de créer un ID unique
            // Création des associations groupe - membres
            GroupeAmi groupeAmi = new GroupeAmi(id+1, amis);
            //GroupeAmi groupeAmi = new GroupeAmi(amis, nomGroupe);
            groupeAmi.save();
        }
        return res;
    }

    /**
     * Getter sur une liste d'utilisateurs
     * @param nom nom des utilisateurs a rechercher
     * @param prenom prenom des utilisateurs a rechercher
     * @return HashMap correspondant au resultat de la requête
     *          ainsi qu'a la liste des utilisateurs
     */
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
        HashMap<String, Object> evenements = new HashMap<>();
        calendriers.put("Request", "LoadEvents");
        try {
            int calendrierID = Calendrier.getCalendrierID(auteur, nomCalendrier);
            ArrayList<Evenement> donnees = Evenement.find(calendrierID, auteur);
            if (donnees.size() > 0) {
                HashMap<String, String> events;
                for (int j = 0 ; j < donnees.size() ; j++){
                    Evenement u = donnees.get(j);
                    events = new HashMap<>();
                    events.put("EventID", ""+u.getId());
                    events.put("EventName", u.getNomE());
                    events.put("Description", u.getDescription());
                    events.put("Picture", u.getImage());
                    events.put("Date", u.getDatedeb().toString());
                    events.put("DateFin", u.getDatefin().toString());
                    events.put("EventLocation", u.getLieu());
                    events.put("EventColor", u.getCouleur());
                    events.put("EventAuthor", u.getAuteur());
                    evenements.put("" + j, events);
                }
                calendriers.put("Result", MessageCodeException.C_SUCCESS);
                calendriers.put("Message", MessageCodeException.M_SUCCESS);
                calendriers.put("Data", evenements);
            }
            else {
                calendriers.put("Result", MessageCodeException.C_NOT_FOUND);
                calendriers.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
            }
        } catch (SQLException e) {
            calendriers.put("Result", MessageCodeException.C_ERROR_BDD);
            calendriers.put("Message", MessageCodeException.M_BDD_ERROR);
            e.printStackTrace();
        }catch (ParseException e){
            calendriers.put("Result", MessageCodeException.C_DATE_PARSE);
            calendriers.put("Message", MessageCodeException.M_DATE_PARSE_ERROR);
        }
        return calendriers;
    }

    public HashMap<String, String> modifAdminCalend(String nomCalendrier, String email, String emailNouveau) {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "TransfertCalendarOwnership");
        try {
            int idCalendrier = Calendrier.getCalendrierID(email, nomCalendrier);
            Calendrier c = Calendrier.find(idCalendrier);
            if (c == null) {
                res.put("Result", MessageCodeException.C_NOT_FOUND);
                res.put("Message", MessageCodeException.M_CALENDAR_NOT_FOUND);
            }
            else {
                if (Utilisateur.find(emailNouveau) == null) {
                    res.put("Result", MessageCodeException.C_NOT_FOUND);
                    res.put("Message", MessageCodeException.M_USER_NOT_FOUND);
                }
                else {
                    if (c.modifAdmin(emailNouveau, email) == 0) {
                        res.put("Result", MessageCodeException.C_SUCCESS);
                        res.put("Message", MessageCodeException.M_SUCCESS);
                    } else {
                        res.put("Result", MessageCodeException.C_DB_CONSISTENCY_ERROR);
                        res.put("Message", MessageCodeException.M_DB_CONSISTENCY_ERROR);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public HashMap<String, String> transfererPropriete(String memberName, String eventOwner, String eventName) {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "TransfertEventOwnership");
        try {
            Utilisateur u = Utilisateur.find(memberName);
            if (u == null) {
                res.put("Result", MessageCodeException.C_NOT_FOUND);
                res.put("Message", MessageCodeException.M_USER_NOT_FOUND);
            }
            else {
                Evenement e = Evenement.find(eventOwner, eventName);
                if (e == null) {
                    res.put("Result", MessageCodeException.C_NOT_FOUND);
                    res.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
                }
                else {
                    if (!e.inEvent(u)) {
                        res.put("Result", MessageCodeException.C_NOT_FOUND);
                        res.put("Message", MessageCodeException.M_USER_NOT_IN_EVENT);
                    }
                    else {
                        ArrayList<Utilisateur> ul = new ArrayList<>();
                        ul.add(u);
                        this.envoiNotifications(ul);
                        int requestResult = e.transfererPropriete(u);
                        if (requestResult == -1) {
                            res.put("Result", MessageCodeException.C_DB_CONSISTENCY_ERROR);
                            res.put("Message", MessageCodeException.M_DB_CONSISTENCY_ERROR);
                        } else {
                            res.put("Result", MessageCodeException.C_SUCCESS);
                            res.put("Message", MessageCodeException.M_SUCCESS);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            res.put("Result", MessageCodeException.C_ERROR_BDD);
            res.put("Message", MessageCodeException.M_BDD_ERROR);
            e.printStackTrace();
        } catch (ParseException e) {
            res.put("Result", MessageCodeException.C_DATE_PARSE);
            res.put("Message", MessageCodeException.M_DATE_PARSE_ERROR);
            e.printStackTrace();
        }
        return res;
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public ArrayList<GroupeAmi> rechercherGroupe(String nomG) throws SQLException {
        return GroupeAmi.find(nomG);
    }

	/**
	 * Cette méthode vérifie quels utilisateurs d'un groupe n'appartiennent pas à un événement et les invite.
	 * @param idG L'identifiant du groupe
	 * @param idE L'identifiant de l'événement
	 * @throws SQLException
	 */
	public void verifInvitAmiEvenement(int idG, int idE) throws SQLException {
		Connection connect = GestionnaireBDD.getInstance().getConnection();
		String request = "SELECT Email FROM groupes_amis WHERE idG=? EXCEPT SELECT Email From utilisateur_evenement WHERE ide=?;";
		PreparedStatement prep = connect.prepareStatement(request);
		prep.setInt(1, idG);
		prep.setInt(2, idE);
		ResultSet result = prep.executeQuery();
		while(result.next()){
			//Invite l'utilisateur à l'événement
            Utilisateur.invitUtilisateurEvenement(result.getString(1), idE);
		}
	}

    public HashMap<String, String> supprimerGroupeAmis(String auteur, int id_Groupe) {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "DeletFriendGroup");
        try {
            //requete pour delete le groupe
            if (GroupeAmi.delete(id_Groupe)){
                MessageCodeException.group_not_found(res);
            }else{
                MessageCodeException.success(res);
            }
        } catch (SQLException e) {
            MessageCodeException.bdd_error(res);
            e.printStackTrace();
        }
        return res;
    }

    public HashMap<String, String> supprimerAmis(String user, String amis) {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "DeletFriendGroup");
        try {
            //requete pour delete le groupe
            if (Utilisateur.deleteAmis(user, amis)){
                MessageCodeException.amis_not_found(res);
            }else{
                MessageCodeException.success(res);
            }
        } catch (SQLException e) {
            MessageCodeException.bdd_error(res);
            e.printStackTrace();
        }
        return res;
	}

    /**
     * Méthode qui permet de modifier un compte utilisateur
     * @param email l'email de l'utilisateur
     * @param nom le nouveau nom
     * @param prenom le nouveau prénom
     * @param mdp le nouveau mot de passe
     * @return Une hashmap contenant les données à envoyer au client
     */
    public HashMap<String, String> modifierCompte(String email, String nom, String prenom, String mdp){
        // Init des variables : Liste des données et hashmap de retour
        HashMap<String, String> res = new HashMap<>();
        ArrayList<String> donnees = new ArrayList<>();
        // Test si les valeurs sont vides
        donnees.add(email);
        donnees.add(nom);
        donnees.add(prenom);
        donnees.add(mdp);
        try {
            if (Verification.checkEmptyData(donnees)) {
                // Test de la validité du mail de l'utilisateur
                if (Verification.checkMail(email)) {
                    // Récup de l'utilisateur
                    Utilisateur u = Utilisateur.find(email);
                    if(u != null){
                        // Application des nouvelles valeurs
                        u.setNom(nom);
                        u.setPrenom(prenom);
                        u.setPassword(mdp);
                        u.save();
                        // Partie de retourne des valeurs en fonction des param
                    }else{
                        MessageCodeException.user_not_found(res);
                        return res;
                    }
                } else {
                    MessageCodeException.invalid_email(res);
                    return res;
                }
            } else {
                MessageCodeException.empty_data(res);
                return res;
            }
            MessageCodeException.success(res);
        }catch (SQLException e){
            // En cas d'erreur bdd, affichage de l'erreur dans le message rendu
            MessageCodeException.bdd_error(res);
            e.printStackTrace();
        }
        return res;
    }

    public HashMap<String, String> modifierGroupe(String email, int idG, String nomGroupe, ArrayList<String> users){
        HashMap<String, String> res = new HashMap<>();
        ArrayList<String> verif = new ArrayList<>();
        verif.add(nomGroupe);
        try {
            if (Verification.checkEmptyData(verif)) {
                if (Verification.checkFriends(email, users)) {
                    // Si les données ne sont pas vides, et si les utilisateurs existent
                    // On peut modifier les données du groupe d'amis

                    // Récupération du groupe
                   GroupeAmi g =  GroupeAmi.find(idG);
                   if(g != null){
                       // Sauvegarde du nom du groupe
                       g.setNom_groupe(nomGroupe);
                       if(!g.save_nom()){
                           throw new SQLException();
                       }
                       // Sauvegarde des utilisateurs
                       if(!g.save_users(users)){
                           throw new SQLException();
                       }
                   }else{
                       MessageCodeException.group_not_found(res);
                       return res;
                   }
                }else{
                    MessageCodeException.user_not_found(res);
                    return res;
                }
            } else {
                MessageCodeException.empty_data(res);
                return res;
            }
            MessageCodeException.success(res);
        }catch (SQLException e){
            MessageCodeException.bdd_error(res);
            e.printStackTrace();
        }
        return res;
    }
}