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

	public GroupeAmi(int idG, String em, String nomG){
	    this.idG = idG;
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
			groupes.add(new GroupeAmi(result.getInt(1), result.getString(2), result.getString(3)));
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
	 * @param idG id du groupe a supprimer
	 * @return true si le groupe a ete supprime
	 * @throws SQLException
	 */
	public static GroupeAmi find(int idG) throws SQLException{
	    Connection connec = GestionnaireBDD.getConnection();
	    GroupeAmi group = null;
	    String request = "SELECT * FROM groupes_amis WHERE idG = ?";
	    PreparedStatement statement = connec.prepareStatement(request);
	    statement.setInt(1, idG);
	    ResultSet res = statement.executeQuery();
	    if(res.next()){
	        group = new GroupeAmi(res.getInt(1), res.getString(2), res.getString(3));
        }
        return group;
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

    public boolean save_nom() throws SQLException{
	    Connection connection = GestionnaireBDD.getInstance().getConnection();
        String request = "UPDATE groupes_amis SET nom_groupe = ? WHERE idG = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(request);
        preparedStatement.setString(1, nom_groupe);
        preparedStatement.setInt(2, idG);
        if (preparedStatement.executeUpdate()  == 0){
            return false;
        }
	    return true;
    }

    public boolean save_users(ArrayList<String> u) throws SQLException{
        Connection connection = GestionnaireBDD.getInstance().getConnection();
        String request;
        PreparedStatement statement;
        ResultSet rs;

        // Suppression des anciens utilisateurs
        request = "SELECT * FROM amis_groupe WHERE idG = ?";
        statement = connection.prepareStatement(request);
        statement.setInt(1, idG);
        rs = statement.executeQuery();
        // Pour tous les utilisateurs déjà présents
        while(rs.next()){
            boolean trouve = false;
            // On regarde dans la liste si l'utilisateur existe toujours dans la liste
            for(String user : u){
                if(user.equals(rs.getString("Email"))){
                    trouve = true;
                }
            }
            // Si l'utilisateur n'existe plus, on le supprime
            if(!trouve){
                delete_user(rs.getString("Email"));
            }
        }

        // Ajout des nouveaux utilisateurs
        for(String user : u){
            request = "SELECT * FROM amis_groupe WHERE idG = ? AND Email = ?";
            statement = connection.prepareStatement(request);
            statement.setInt(1, idG);
            statement.setString(2, user);
            rs = statement.executeQuery();
            if(!rs.next()){
                // S'il n'y a pas de résultats, cela signifie qu'il n'y a pas cet utilisateur
                request = "INSERT INTO amis_groupe VALUES(?, ?)";
                statement = connection.prepareStatement(request);
                statement.setString(1, user);
                statement.setInt(2, idG);
                if(statement.executeUpdate() == 0){
                    return false;
                }
            }
        }
	    return true;
    }

    /**
     * Méthode de suppression d'un individu dans un groupe
     * @param email l'individu à supprimer
     * @return un booléen qui indique s'il y a eu une erreur
     * @throws SQLException
     */
    public boolean delete_user(String email) throws SQLException{
	    Connection connection = GestionnaireBDD.getConnection();
	    String request = "DELETE FROM amis_groupe WHERE Email = ?";
	    PreparedStatement statement = connection.prepareStatement(request);
	    statement.setString(1, email);
	    if(statement.executeUpdate() == 0) {
            return false;
        }
	    return true;
    }

    public void setNom_groupe(String nom_groupe) {
        this.nom_groupe = nom_groupe;
    }
}
