package mycalendar.modele.utilisateur;

import mycalendar.modele.bdd.GestionnaireBDD;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class GroupeAmi {

	private int idG;
	private String email;
	private String nom_groupe;
	private ArrayList<String> amis;

	/**
	 * Constructeur
	 * @param em email du createur
	 * @param nomG nom du groupe
	 */
	public GroupeAmi(String em, String nomG){
		this.email = em;
		this.nom_groupe = nomG;
	}

	/**
	 * Constructeur
	 * @param amis amis qui vont appartenir au groupe
	 * @param nomG nom du groupe
	 */
	public GroupeAmi(ArrayList<String> amis, String nomG) {
		this.amis = amis;
		this.nom_groupe = nomG;
	}

	/**
	 * Recherche des groupes par nom
	 * @param nomG nom du groupe
	 * @return les groupes associes au nom
	 * @throws SQLException
	 */
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


	/**
	 * Methode de sauvegarde d'un groupe d'amis dans la BDD
	 * @return true si la sauvegarde s'est bien passee
	 * @throws SQLException
	 */
	public boolean save() throws SQLException{
		Connection connect = GestionnaireBDD.getInstance().getConnection();
		// On commence par vérifier si le thème existe
		String request;
		PreparedStatement prep;
		// Création du groupe pour l'utilisateur
		request = "INSERT INTO groupe_amis (idG, Email, nom_groupe) VALUES (?,?, ?);";
		prep = connect.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);
		prep.setInt(1, idG);
		prep.setString(2, email);
		prep.setString(2, nom_groupe);
		if (prep.executeUpdate() == 0) { // Pas de nouvelles lignes insérées lors de l'exécution de la requête, il y a donc un problème
			return false;
		}
		// Association des utilisateurs (amis) au groupe
		for(String ami : amis) {
			request = "INSERT INTO amis_groupe (Email, idG) VALUES (?,?);";
			prep = connect.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);
			prep.setString(1, ami);
			prep.setInt(2, idG);
			prep.executeUpdate();
		}
		return true;
	}

	/**
	 * Suppression d'un groupe d'amis
	 * @param id_Groupe id du groupe a supprimer
	 * @return true si le groupe a ete supprime
	 * @throws SQLException
	 */
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
