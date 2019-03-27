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
    private String email;

    /**
     * Constructeur d'un calendrier
     * @param nom nom du calendrier
     * @param desc description du calendrier
     * @param coul couleur du calendrier
     * @param themes themes du calendrier
     */
    public Calendrier(int idCalendar, String nom, String coul, String desc, String themes, String auteur) throws SQLException{
        this.idC = idCalendar;
        this.nomC = nom;
        this.evenements = new ArrayList<>();
        this.couleur = coul;
        this.description = new StringBuilder(desc);
        this.theme = themes;
        this.email = auteur;
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
    public static int modificationCalendrier(int id, String nom, String couleur, String theme, String description) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String SQLprep = "update Calendrier set nomC=?, couleur=?, theme=?, description=? where idc=?;";
        PreparedStatement prep = connect.prepareStatement(SQLprep);
        prep.setString(1, nom);
        prep.setString(2, couleur);
        prep.setString(3, theme);
        prep.setString(4, description);
        prep.setInt(5, id);
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
            String request = "SELECT idc FROM utilisateur_calendrier WHERE email=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setString(1, nomUtilisateur);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            while (rs.next()) {
                request = "SELECT idc FROM Calendrier WHERE idc=? AND nomC=?;";
                prep = connect.prepareStatement(request);
                prep.setInt(1, rs.getInt("idc"));
                prep.setString((int)2, nomCalendrier);
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
     * Getter sur les membres d'un calendrier
     * @param id id du calendrier
     * @return liste des utilisateurs
     * @throws SQLException
     */
    public static ArrayList<Utilisateur> findInvites(int id) throws SQLException {
        ArrayList<Utilisateur> alu = new ArrayList<>();
        String email, nom, mdp, prenom;
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        // Sélection des utilisateurs possédant le calendrier
        System.out.println("findInvites avant");
        String SQLPrep = "SELECT email FROM utilisateur_calendrier WHERE idc=?;";
        System.out.println("findInvites après");
        PreparedStatement prep = connect.prepareStatement(SQLPrep);
        prep.setInt(1, id);
        prep.execute();
        ResultSet rs = prep.getResultSet();
        // s'il y a un resultat
        while(rs.next()){
            email = rs.getString("Email");
            nom = rs.getString("nom");
            mdp = rs.getString("mdp");
            prenom = rs.getString("prenom");
            alu.add(new Utilisateur(email, nom, mdp, prenom));    // Création d'Utilisateur à ajouter à la liste
        }
        return alu;
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
                request = "SELECT * FROM utilisateur_calendrier WHERE idc=?;";
                prep = connect.prepareStatement(request);
                prep.setInt(1, rs.getInt("idc"));
                prep.execute();
                ResultSet rst = prep.getResultSet();
                      if(rst.next()){
                            return new Calendrier(rs.getInt("idC"),rs.getString("nomC"), rs.getString("couleur"), rs.getString("description"), rs.getString("theme"),rst.getString("Email"));
                                      }
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
                request = "SELECT * FROM utilisateur_calendrier WHERE idc=?;";
                prep = connect.prepareStatement(request);
                prep.setInt(1, rs.getInt("idc"));
                prep.execute();
                ResultSet rst = prep.getResultSet();
                c = new Calendrier(rs.getInt("idC"),rs.getString("nomC"), rs.getString("description"), rs.getString("couleur"), rs.getString("theme"),rst.getString("Email"));
                
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
        // On commence par vérifier si le thème existe
        String request = "SELECT * FROM themes WHERE nom = ?";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, theme);
        ResultSet res = prep.executeQuery();

        if(!res.next()){
            // Insertion du thème dans la base
            request = "INSERT INTO themes VALUES(?);";
            prep = connect.prepareStatement(request);
            prep.setString(1, theme);
            prep.executeUpdate();
        }

        request = "INSERT INTO Calendrier (idC, nomC, description, couleur, theme) VALUES (?,?,?,?,?);";
        prep = connect.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);
        prep.setInt(1, idC);
        prep.setString(2, nomC);
        prep.setString(3, description.toString());
        prep.setString(4, couleur);
        prep.setString(5, theme);
        // System.out.println(" ajout calendrier ");
        if (prep.executeUpdate() == 0) { // Pas de nouvelles lignes insérées lors de l'exécution de la requête, il y a donc un problème
            return false;
        }
        //ajout dans la table utilisateur_calendrier
        request = "INSERT INTO utilisateur_calendrier (Email, idc) VALUES (?,?);";
        prep = connect.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);
        prep.setString(1, email);
        prep.setInt(2, idC);
        prep.executeUpdate();
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
            String request = "DELETE FROM Calendrier WHERE idc=? AND nomC=? AND description=? AND couleur=? AND theme=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setInt(1, this.idC);
            prep.setString(2, this.nomC);
            // Inversés car prob de constructeur... on évite de tout toucher
            prep.setString(4, this.description.toString());
            prep.setString(3, this.couleur);
            prep.setString(5, this.theme);
            if (prep.executeUpdate() == 0) { // Pas de nouvelles lignes insérées lors de l'exécution de la requête, il y a donc un problème
                return false;
            }
        }
        return true;
    }

    public static ArrayList<String> getThemes() throws SQLException {
        ArrayList<String> themes = new ArrayList<>();
        Connection connect = GestionnaireBDD.getConnection();
        String requete = "SELECT * FROM themes";
        PreparedStatement statement = connect.prepareStatement(requete);
        ResultSet res = statement.executeQuery();
        while(res.next()){
            themes.add(res.getString("nom"));
        }
        return themes;
    }

    public int modifAdmin(String emailNouveau, String email) throws SQLException {
        this.email = emailNouveau;
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "UPDATE utilisateur_calendrier SET Email=? WHERE Email=? AND idc=?;";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setString(1, this.email);
        prep.setString(2, email);
        prep.setInt(3, this.idC);
        if (prep.executeUpdate() == 1) {
            return 0;
        }
        return -1;
    }

    public int getIdC() {
        return idC;
    }

    public StringBuilder getDescription() {
        return description;
    }

    public String getCouleur() {
        return couleur;
    }

    public String getTheme() {
        return theme;
    }
}
