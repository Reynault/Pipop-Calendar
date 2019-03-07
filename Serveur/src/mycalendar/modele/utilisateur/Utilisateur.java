package mycalendar.modele.utilisateur;

import mycalendar.modele.bdd.GestionnaireBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utilisateur extends Notif{

    private String email, nom, tmp_password, password, prenom;

    public Utilisateur(String mail, String mdp){
        this.email = mail;
        this.password = mdp;
    }

    /**
     * Lorsqu'un utilisateur souhaite se connecter, cette fonction teste s'il est présent dans la base de données.
     * S'il est déjà présent alors la connexion sera possible.
     * @return 0 si la connexion a échoué, 1 si c'est réussi
     * @throws SQLException
     */
    public int verifierConnexion() throws SQLException {
        int connexion = 0;
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "SELECT * FROM Utilisateur WHERE Email = ? AND mdp = ?;";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, email);
        prep.setString(2, password);
        ResultSet result = prep.executeQuery();
        if(result.next()){
            connexion = 1;
        }
        return connexion;
    }

}
