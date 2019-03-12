package mycalendar.modele.serveur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.Calendrier;
import mycalendar.modele.calendrier.Evenement;
import mycalendar.modele.utilisateur.Utilisateur;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
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
        listener = new ServerSocket(PORT_NUMBER, 100, inet);
        System.out.println("LAUNCH SERVER");
        // Le serveur attend continuellement un client
        while (true) {
            // On accepte d'un client
            socket = listener.accept();
            System.out.println("On accepte le client.");
            // Création du thread lié au client en cours
            thread = new Thread(new ConnexionClient(socket));
            // Lancement du thread
            thread.start();
            System.out.println("Je continue d'attendre des clients.");
        }
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
			while(result.next()){

			}
            res.put("Result","0");
        }else{
            // Utilisateur non trouvé
            res.put("Result","1");
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
                res.put("Result","0");
                break;
            }
            case 0:
            {
                // Utilisateur déjà existant
                res.put("Result","1");
                break;
            }
            case 2:
            {
                // Cas dans lequel une des données est trop longue
                res.put("Result","2");
                break;
            }
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
        res.put("Request", "AddCalendar");
        try {
            if (!this.verifierCalendrier(auteur, nomCalendrier)) { // On vérifie que le calendrier n'existe pas déjà
                res.put("Result", "Calendar already exists");
                return res;
            }
            if (!this.creerCalendrier(nomCalendrier, description, couleur, theme)) { // On crée le calendrier

                res.put("Result", "Couldn't insert new calendar into database");
                return res;
            }
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
        res.put("Result", "Success");
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
    private boolean creerCalendrier(String nomCalendrier, String description, String couleur, String theme) throws ParseException, SQLException {
        Calendrier c;
        int id;
        id = Calendrier.getHighestID(); // On récupère l'ID de l'événement le plus élevé afin de créer un ID unique
        c = new Calendrier(id+1,nomCalendrier, couleur, description, theme);
        return c.save();
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
           res.put("Request", "DeleteEvent");
           try {
               //b a true si on souhaire supprimer les evenements liés au calendrier
               if(b) {
                   //on veut recuperer la liste des evenements appartenant au calendrier
                   // dans le but de les supprimer
                   ArrayList<Evenement> events = null;
                   events = Evenement.find(idC, email);
                   if (events.size() == 0) {

                       res.put("Result", "Events not found");
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
                       res.put("Result", "Couldn't delete calendar from database");
                       return res;
                   }
               }
               // b a false quand on veut juste supprimer le calendrier, sans supprimer les evenements
               else{
                   // on cherche le calendrier dans la base et le supprime
                   Calendrier c = Calendrier.find(idC);

                   if (!c.delete()) {
                       res.put("Result", "Couldn't delete calendar from database");
                       return res;
                   }
               }
           } catch (SQLException e1) {
               e1.printStackTrace();
           }
           res.put("Result", "Success");
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
    public HashMap<String, String> modificationCalendrier(int id, String nom, String couleur) throws SQLException, ParseException {
        HashMap<String, String> res = new HashMap<>();
        res.put("Request", "ModifyCalendar");
        // Le calendrier n'existe pas
        if(!GestionnaireBDD.verifierExistenceCalendrier(id)){
            res.put("Result", "Calendar not found");
            return res;
        }
        // Aucune modification effectuée
        else if(Calendrier.modificationCalendrier(id, nom, couleur) == 0) {
            res.put("Result", "Calendar didn't change");
            return res;
        }
        // Erreur lors de la modification
        else if(Calendrier.modificationCalendrier(id, nom, couleur) < 0) {
            res.put("Result", "Error during modification");
        }
        // La calendrier a bien été modifié
        else {
            envoiNotifications(Calendrier.findInvites(id));
            res.put("Result", "Success");
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

    /**
     * Getter sur un calendrier
     * @param id id du calendrier
     * @return calendrier correspondant
     * @throws SQLException
     */
    public Calendrier getCalendrier(int id) throws SQLException {
        return Calendrier.find(id);
    }
 
}
