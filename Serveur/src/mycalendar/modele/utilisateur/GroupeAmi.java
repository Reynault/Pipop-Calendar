package mycalendar.modele.utilisateur;

import mycalendar.modele.bdd.GestionnaireBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GroupeAmi {

	private String email, nom_groupe;

	public GroupeAmi(String em, String nomG){
		this.email = em;
		this.nom_groupe = nomG;
	}

	public static ArrayList<GroupeAmi> find(String nomG) throws SQLException {
		ArrayList<GroupeAmi> groupes = new ArrayList<>();
		Connection connect = GestionnaireBDD.getInstance().getConnection();
		String request = "SELECT * FROM groupes_amis WHERE nom_groupe = ?";
		PreparedStatement prep = connect.prepareStatement(request);
		prep.setString(1, nomG);
		ResultSet result = prep.executeQuery();
		while(result.next()){
			groupes.add(new GroupeAmi(result.getString(2), result.getString(3)));
		}
		return groupes;
	}

	public static Boolean delete(int id_Groupe) throws SQLException{
	    Connection connection = GestionnaireBDD.getInstance().getConnection();
	    String request = "DELETE FROM groupes_amis WHERE nom_groupe = ?";
	    PreparedStatement preparedStatement = connection.prepareStatement(request);
	    preparedStatement.setInt(1, id_Groupe);
	    if (preparedStatement.executeUpdate()  == 0){
	        return false;
        }
	    return true;
    }
}
