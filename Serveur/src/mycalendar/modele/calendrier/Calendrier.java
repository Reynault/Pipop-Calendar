package mycalendar.modele.calendrier;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.utilisateur.Utilisateur;

import java.util.ArrayList;
import java.sql.*;
import java.util.Properties;

import mycalendar.modele.bdd.GestionnaireBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Calendrier {

    private int idC;
    private String nomC;
    private ArrayList<Evenement> evenements;
    private String couleur;
    private StringBuilder description;
    private String theme;

    /**
     * Constructeur d'un calendrier
     * @param nom nom du calendrier
     * @param desc description du calendrier
     * @param coul couleur du calendrier
     * @param themes themes du calendrier
     */
    public Calendrier(int idCalendar, String nom, String coul, String desc, String themes) throws SQLException{
        this.idC = idCalendar;
        this.nomC = nom;
        this.evenements = new ArrayList<>();
        this.couleur = coul;
        this.description = new StringBuilder(desc);
        this.theme = themes;
    }


    /**
     * Consultation d'un calendrier
     * @param id id du calendrier
     * @throws SQLException
     */
    public void consulterCalendrier(int id) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String SQLPrep = "SELECT * FROM Calendrier WHERE id=?;";
        PreparedStatement prep = connect.prepareStatement(SQLPrep);
        prep.setInt(1, id);
        prep.execute();
    }


    /**
     * Modification d'un calendrier
     * @param id id du calendrier
     * @param nom nouveau nom du calendrier
     * @param couleur nouvelle couleur du calendrier
     * @return nombre de lignes modifiees (0 si echec)
     * @throws SQLException
     */
    public static int modificationCalendrier(int id, String nom, String couleur) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        System.out.println("ModificationCalendrier avant");
        String SQLprep = "update Calendrier set nomC=?, couleur=? where idc=?;";
        System.out.println("ModificationCalendrier après");
        PreparedStatement prep = connect.prepareStatement(SQLprep);
        prep.setString(1, nom);
        prep.setString(2, couleur);
        prep.setInt(3, id);
        return prep.executeUpdate();  // Le nombre de lignes modifiées
    }


    /**
     * Getter sur l'ID le plus eleve des calendriers
     * @return id le plus eleve des calendriers
     * throws SQLException
     */
    public static int getHighestID() throws SQLException{
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "SELECT MAX(idc) AS max FROM Calendrier;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            if (rs.next()) {
                return rs.getInt("max");
            }
            return -1;
        }
    }


    /**
     * Getter sur les evenements lies au calendrier
     * @return liste des evenements
     */
    public ArrayList<Evenement> getEvenements() {
        return this.evenements;
    }


    /**
     * Getter sur le nom du calendrier
     * @return nom du calendrier
     */
    public String getNomCalendrier(){
        return this.nomC;
    }


    /**
     * Getter sur l'appartenance d'un evenement au calendrier
     * @return true si l'evenement appartient au calendrier
     */
    public boolean contient(Evenement e) {
        boolean res=false;
        for(Evenement ev : evenements){
            if(ev==e){
                res =true;
            }
        }
        return res;
    }


    /**
     * Récupère l'ID d'un calendrier associé à son nom
     * @param nomCalendrier nom du calendrier
     * @return l'ID du calendrier récupéré (-1 si le calendrier n'existe pas)
     * @throws SQLException
     */
    public static int getCalendrierID(String nomUtilisateur, String nomCalendrier) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "SELECT idc FROM Calendrier WHERE nomC=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setString(1, nomUtilisateur);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            while (rs.next()) {
                request = "SELECT idc FROM Calendrier WHERE nomC=?;";
                prep = connect.prepareStatement(request);
                prep.setString(1, nomCalendrier);
                prep.execute();
                ResultSet rst = prep.getResultSet();
                if (rst.next()) {
                    return rst.getInt("idc");
                }
            }
        }
        return -1;
    }


    /**
     * Suppression d'un evenement du calendrier
     * @param e evenement a supprimer
     * @throws SQLException
     */
    public void deleteEvent(Evenement e) throws SQLException{
        for(Evenement ev : evenements){
            if(ev==e){
                ev.delete();
            }
        }
    }


    /**
     * Recherche d'un calendrier par son id
     * @param idC id du calendrier
     * @return calendrier correspondant
     * @throws SQLException
     */
    public static Calendrier find(int idC) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "SELECT * FROM Calendrier WHERE idC=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setInt(1, idC);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            if (rs.next()) {
                return new Calendrier(rs.getInt("idC"),rs.getString("nomC"), rs.getString("description"), rs.getString("couleur"), rs.getString("theme"));
            }
            return null;
        }
    }


    /**
     * Recherche des calendriers contenant un evenement
     * @param e evenement lie au calendrier
     * @param idEv id de l'evenement
     * @return calendriers lies a l'evenement
     * @throws SQLException
     */
    public static ArrayList<Calendrier> find (Evenement e, int idEv) throws  SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        ArrayList<Calendrier> calendars = new ArrayList<>();
        {
            String request = "SELECT * FROM Calendrier WHERE idC=(SELECT idC FROM Evenement WHERE idE = ?);";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setInt(1, idEv);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            Calendrier c;
            if (rs.next()) {
                c = new Calendrier(rs.getInt("idC"),rs.getString("nomC"), rs.getString("description"), rs.getString("couleur"), rs.getString("theme"));
                if(c.contient(e)){
                    calendars.add(c);
                }
            }
            return calendars;
        }

    }


    /**
     * Methode de sauvegarde d'un evenement dans la BDD
     * @return true si la sauvegarde s'est bien passee, false sinon
     * @throws SQLException
     */
    public boolean save() throws SQLException{
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "INSERT INTO Calendrier (nomC, description, couleur, theme) VALUES (?,?,?,?);";
        PreparedStatement prep = connect.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);
        prep.setString(1, nomC);
        prep.setString(2, description.toString());
        prep.setString(3, couleur);
        prep.setString(4, theme);
        prep.executeUpdate();
       // System.out.println(" ajout calendrier ");
        if (prep.executeUpdate() == 0) { // Pas de nouvelles lignes insérées lors de l'exécution de la requête, il y a donc un problème
            return false;
        }
        return true;
    }


    /**
     * Methode de suppression d'un evenement dans la BDD
     * @return true si la suppression s'est bien passee, false sinon
     * @throws SQLException
     */

    public boolean delete() throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "DELETE FROM Calendrier WHERE idC=? AND nomC=? AND description=? AND couleur=? AND theme=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setInt(1, this.idC);
            prep.setString(3, this.nomC);
            prep.setString(4, this.description.toString());
            prep.setString(3, this.couleur);
            prep.setString(4, this.theme);
            if (prep.executeUpdate() == 0) { // Pas de nouvelles lignes insérées lors de l'exécution de la requête, il y a donc un problème
                return false;
            }
        }
        return true;
    }

}
