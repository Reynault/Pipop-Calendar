package mycalendar.modele.utilisateur;
import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.Calendrier;
import mycalendar.modele.calendrier.Evenement;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Classe Utilisateur qui représente un utilisateur dans la base de données
 */

public class Utilisateur{

    private String email, nom, tmp_password, password, prenom;

    /**
     * Constructeur à quatre paramètres
     * @param email l'email
     * @param nom le nom
     * @param password le mot de passe
     * @param prenom et le prénom
     */
    public Utilisateur(String email, String nom, String password, String prenom) {
        this.email = email;
        this.nom = nom;
        this.password = password;
        this.prenom = prenom;
    }

    /**
     * Lorsqu'un utilisateur souhaite se connecter, cette fonction teste s'il est présent dans la base de données.
     * S'il est déjà présent alors la connexion sera possible.
     * @return 0 si la connexion a échoué, 1 si c'est réussi
     * @throws SQLException
     */
    public static boolean verifierConnexion(String email, String password) throws SQLException {
        boolean connexion = false;
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "SELECT * FROM Utilisateur WHERE Email = ? AND mdp = ?;";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, email);
        prep.setString(2, password);
        ResultSet result = prep.executeQuery();
        if(result.next()){
            connexion = true;
        }
        return connexion;
    }

    /**
     * Méthode qui permet de vérifier les données fournies par l'inscription
     * d'un utilisateur
     * @param email email de l'utilisateur
     * @param mdp mot de passe de l'utilisateur
     * @param prenom prénom de l'utilisateur
     * @param nom nom de l'utilisateur
     * @return un entier qui indique le message de retour
     * @throws SQLException
     */
    public static int verifierInscription(String email, String mdp, String prenom, String nom)
            throws SQLException{
        // Récup de la connexion
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        Utilisateur user;
        int retour = 1;

        // On commence par vérifier si un utilisateur existe déjà
        String request = "SELECT * FROM Utilisateur WHERE Email = ?;";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, email);
        ResultSet result = prep.executeQuery();
        // Si l'utilisateur existe déjà
        if(result.next()){
            return 0;
        }else{
            // Sinon

            // On teste les données

            // On commence par regarder la taille des données par rapport à la bdd
            if(email.length() > 32 || nom.length() > 32
                    || prenom.length() > 32 || mdp.length() > 2000){
                // Si les données sont trop longues, on quitte
                return 2;
            }

            // Création de l'utilisateur
            user = new Utilisateur(email, mdp, prenom, nom);
            request = "INSERT INTO Utilisateur VALUES(?,?,?,?,?)";
            prep = connect.prepareStatement(request);
            prep.setString(1,email);
            prep.setString(2,nom);
            prep.setString(3,"");
            prep.setString(4,mdp);
            prep.setString(5,nom);
            // Execution de la mise à jour
            retour = prep.executeUpdate();
        }
        return retour;
    }

    public static ArrayList<Calendrier> findCalendriers(String email) throws SQLException {
        ArrayList<Calendrier> calendriers = new ArrayList<>();
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "SELECT * FROM `Calendrier` WHERE `idc` IN (SELECT `idc` FROM `utilisateur_calendrier` WHERE `Email` = ? );";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, email);
        ResultSet result = prep.executeQuery();
        while(result.next()){
            calendriers.add(new Calendrier(result.getInt(1), result.getString(2), result.getString(3),
                    result.getString(4), result.getString(5), email));
        }
        return calendriers;
    }

    /**
     * Méthode save qui permet de sauvegarder un utilisateur
     * dans la base de données
     * @return un entier indiquant le message de retour
     */
    public int save() throws SQLException{
        int res = 1;
        return res;
    }

    public static ArrayList<Utilisateur> find(String nom, String prenom) throws SQLException {
        ArrayList<Utilisateur> ul = new ArrayList<>();
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        PreparedStatement prep;
        String request;
        if (nom.equals("")) {
            request = "SELECT * FROM Utilisateur WHERE prenom=?;";
            prep = connect.prepareStatement(request);
            prep.setString(1, prenom);
        }
        else if (prenom.equals("")) {
            request = "SELECT * FROM Utilisateur WHERE nom=?;";
            prep = connect.prepareStatement(request);
            prep.setString(1, nom);
        }
        else {
            request = "SELECT * FROM Utilisateur WHERE nom=? AND prenom=?;";
            prep = connect.prepareStatement(request);
            prep.setString(1, nom);
            prep.setString(2, prenom);
        }
        prep.execute();
        ResultSet rs = prep.getResultSet();
        while (rs.next()) {
            ul.add(new Utilisateur(rs.getString("Email"), rs.getString("nom"), rs.getString("mdp"), rs.getString("prenom")));
        }
        return ul;
    }

    /**
     * Getter sur l'email
     * @return email de l'utilisateur
     */
    public String getEmail() {
        return email;
    }

    public String getNom() {
        return this.nom;
    }

    public String getPrenom() {
        return this.prenom;
    }
}
