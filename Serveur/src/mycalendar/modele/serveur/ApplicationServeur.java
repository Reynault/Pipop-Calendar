package mycalendar.modele.serveur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.utilisateur.Utilisateur;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
}
