package mycalendar.modele.serveur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.utilisateur.Utilisateur;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
