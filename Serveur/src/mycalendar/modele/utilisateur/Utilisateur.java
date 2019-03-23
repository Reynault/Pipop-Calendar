package mycalendar.modele.utilisateur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.Calendrier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe Utilisateur qui représente un utilisateur dans la base de données
 */

public class Utilisateur {

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

    /**
     * Ajout d'un utilisateur en ami
     * @param email1 email de l'utilisateur actif
     * @param email2 email de l'utilisateur a ajouter
     * @return 1 si l'ami a ete ajoute. 0 sinon
     * @throws SQLException
     */
    public static int ajouterAmi(String email1, String email2) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "INSERT INTO Amis VALUES(?,?);";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, email1);
        prep.setString(2, email2);
        int result = prep.executeUpdate();
        connect.close();
        return result; // 1 si le tuple a été ajouté
    }

    /**
     * Recherche des calendriers appartenant a l'utilisateur
     * @param email email de l'utilisateur
     * @return liste des calendriers de l'utilisateur
     * @throws SQLException
     */
    public static ArrayList<Calendrier> findCalendriers(String email) throws SQLException {
        ArrayList<Calendrier> calendriers = new ArrayList<>();
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "SELECT * FROM `Calendrier` WHERE `idc` IN (SELECT `idc` FROM `utilisateur_calendrier` WHERE `Email` = ? );";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, email);
        ResultSet result = prep.executeQuery();
        while(result.next()){
            calendriers.add(new Calendrier(result.getInt(1), result.getString(2), result.getString(4),
                    result.getString(3), result.getString(5), email));
        }
        return calendriers;
    }

    /**
     * Méthode save qui permet de sauvegarder un utilisateur
     * dans la base de données
     * @return un entier indiquant le message de retour
     */
    public boolean save() throws SQLException{
        // Connexion à la bdd
        Connection connect = GestionnaireBDD.getConnection();
        // Préparation de la requete
        String request = "UPDATE Utilisateur SET nom = ?, tmp_mdp = ?, mdp = ?, prenom = ? WHERE email = ?";
        PreparedStatement statement = connect.prepareStatement(request);
        // Paramètrage
        statement.setString(1, nom);
        statement.setString(2, tmp_password);
        statement.setString(3, password);
        statement.setString(4, prenom);
        statement.setString(5, email);
        // Execution
        if (statement.executeUpdate() == 0) { // Pas de nouvelles lignes insérées lors de l'exécution de la requête, il y a donc un problème
            return false;
        }
        return true;
    }

    public static Utilisateur find(String nom) throws SQLException {
        Utilisateur u = null;
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "SELECT * FROM Utilisateur WHERE Email=?;";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, nom);
        ResultSet result = prep.executeQuery();
        if (result.next()) {
            u = new Utilisateur(result.getString("Email"), result.getString("nom"), result.getString("mdp"), result.getString("prenom"));
        }
        return u;
    }

    /**
     * Recherche des utilisateurs par nom et prenom
     * @param nom nom de l'utilisateur
     * @param prenom prenom de l'utilisateur
     * @return liste des utilisateurs
     * @throws SQLException
     */
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

    /**
     * Getter sur le nom
     * @return nom de l'utilisateur
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Getter sur le prenom
     * @return prenom de l'utilisateur
     */
    public String getPrenom() {
        return this.prenom;
    }

    public static Boolean deleteAmis(String user, String amis) throws SQLException {
        Connection connection = GestionnaireBDD.getInstance().getConnection();
        String request = "DELETE FROM Amis WHERE Email1=? AND Email2=?";
        PreparedStatement preparedStatement = connection.prepareStatement(request);
        preparedStatement.setString(1, user);
        preparedStatement.setString(2, amis);
        if (preparedStatement.executeUpdate()  == 0){
            return false;
        }
        return true;

    }

    public static void invitUtilisateurEvenement(String email, int idEvent) throws SQLException {
    	Connection connection = GestionnaireBDD.getInstance().getConnection();
    	String request = "INSERT INTO utilisateur_evenement VALUES(?,?);";
    	PreparedStatement preparedStatement = connection.prepareStatement(request);
    	preparedStatement.setString(1, email);
    	preparedStatement.setInt(2, idEvent);
	    preparedStatement.executeUpdate();
	    connection.close();
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setTmp_password(String tmp_password) {
        this.tmp_password = tmp_password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
