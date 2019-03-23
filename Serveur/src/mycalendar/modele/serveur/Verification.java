package mycalendar.modele.serveur;

import mycalendar.modele.bdd.GestionnaireBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe vérification qui permet de vérifier les données fournies par le client
 */
public class Verification {
    /**
     * Méthode de vérification de l'email fourni
     * @param mail email
     * @return
     */
    public static boolean checkMail(String mail){
        return mail.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }

    /**
     * Méthode de vérification de l'existence de l'utilisateur
     * @param email email
     * @param password et mot de passe
     * @return un booléen qui indique l'existence de l'utilisateur
     * @throws SQLException
     */
    public static boolean checkPassword(String email, String password) throws SQLException {
        boolean resultat = false;
        // Récupération de la connexion
        Connection connect = GestionnaireBDD.getConnection();
        // Préparation de la requête
        String requete = "SELECT * FROM Utilisateur WHERE Email = ?";
        PreparedStatement statement = connect.prepareStatement(requete);
        // Paramètrage
        statement.setString(1, email);
        // Execution
        ResultSet res = statement.executeQuery();
        while (res.next()){
            // Il y a donc bien un utilisateur qui possède cet email
            // On vérifie donc la correspondance avec le mdp de la bdd
            if(res.getString("mdp").equals(password)){
                resultat = true;
            }
        }
        return resultat;
    }

    /**
     * Méthode qui permet de vérifier l'existence d'un calendrier pour un utilisateur donné
     * @param email email de l'utilisateur
     * @param id id de l'utilisateur
     * @return un booléen
     * @throws SQLException
     */
    public static boolean checkCalendar(String email, int id) throws SQLException {
        boolean resultat = false;
        // Récupération de la connexion
        Connection connect = GestionnaireBDD.getConnection();
        // Préparation de la requête
        String requete = "SELECT * FROM utilisateur_calendrier WHERE Email = ? AND idc = ?";
        PreparedStatement statement = connect.prepareStatement(requete);
        // Paramètrage
        statement.setString(1, email);
        statement.setInt(2, id);
        // Execution
        ResultSet res = statement.executeQuery();
        while (res.next()){
            // Il y a donc un résultat
            resultat = true;
        }
        return resultat;
    }

    /**
     * Méthode qui permet de vérifier l'existence d'un calendrier pour un utilisateur
     * via le nom du calendrier
     * @param email email de l'utilisateur
     * @param name nom du calendrier
     * @return un booléen
     * @throws SQLException
     */
    public static boolean checkCalendarByName(String email, String name) throws SQLException {
        boolean resultat = false;
        // Récupération de la connexion
        Connection connect = GestionnaireBDD.getConnection();
        // Préparation de la requête
        String requete = "SELECT * FROM utilisateur_calendrier WHERE Email = ? AND idc IN (SELECT idc FROM Calendrier WHERE nomC = ?)";
        PreparedStatement statement = connect.prepareStatement(requete);
        // Paramètrage
        statement.setString(1, email);
        statement.setString(2, name);
        // Execution
        ResultSet res = statement.executeQuery();
        while (res.next()){
            // Il y a donc un résultat
            resultat = true;
        }
        return resultat;
    }

    /**
     * Méthode qui permet de vérifier l'existence d'un événement pour un utilisateur via son nom
     * @param email email de l'utilisateur
     * @param nom nom de l'événement
     * @return booléen
     * @throws SQLException
     */
    public static boolean checkEventByName(String email, String nom) throws SQLException {
        boolean resultat = false;
        // Récupération de la connexion
        Connection connect = GestionnaireBDD.getConnection();
        // Préparation de la requête
        String requete = "SELECT * FROM utilisateur_evenement WHERE Email = ? AND ide IN (SELECT ide FROM Evenement WHERE nomE = ?)";
        PreparedStatement statement = connect.prepareStatement(requete);
        // Paramètrage
        statement.setString(1, email);
        statement.setString(2, nom);
        // Execution
        ResultSet res = statement.executeQuery();
        while (res.next()){
            // Il y a donc un résultat
            resultat = true;
        }
        return resultat;
    }

    /**
     * Méthode checkEvent qui permet de vérifier l'existence d'un événement pour un utilisateur
     * @param email email de l'utilisateur
     * @param id id de l'événement
     * @return un booléen
     * @throws SQLException
     */
    public static boolean checkEvent(String email, int id) throws SQLException {
        boolean resultat = false;
        // Récupération de la connexion
        Connection connect = GestionnaireBDD.getConnection();
        // Préparation de la requête
        String requete = "SELECT * FROM utilisateur_evenement WHERE Email = ? AND ide = ?";
        PreparedStatement statement = connect.prepareStatement(requete);
        // Paramètrage
        statement.setString(1, email);
        statement.setInt(2, id);
        // Execution
        ResultSet res = statement.executeQuery();
        while (res.next()){
            // Il y a donc un résultat
            resultat = true;
        }
        return resultat;
    }

    /**
     * Méthode de vérification qui permet de vérifier si les données fournies en paramètres sont
     * vides ou non
     * @param donnees données à vérifier
     * @return un booléen
     */
    public static boolean checkEmptyData(ArrayList<String> donnees){
        for (String s:donnees) {
            if (s.isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     * Méthode de vérification de la cohérence entre la date de début d'un événement et la date
     * de fin
     * @param deb date de début
     * @param fin date de fin
     * @return un booléen qui indique si la fin est bien après le début
     */
    public static boolean checkDate(Date deb, Date fin){
        return fin.after(deb);
    }
}
