package mycalendar.modele.calendrier;

import mycalendar.modele.bdd.GestionnaireBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Observable;

public abstract class Evenement extends Observable {

    private int idEv;
    private int calendrierID;
    private String nomE;
    private String description;
    private String image;
    private Date date;
    private String lieu;
    private String auteur;

    protected boolean visibilite;

    private ArrayList<Message> messages;

    /**
     * Constructeur d'un événement
     * @param id
     * @param calID
     * @param nom
     * @param description
     * @param image
     * @param date
     * @param lieu
     * @param auteur
     */
    public Evenement(int id, int calID, String nom, String description, String image, Date date, String lieu, String auteur) {
        this.idEv = id;
        this.calendrierID = calID;
        this.nomE = nom;
        this.description = description;
        this.image = image;
        this.date = date;
        this.lieu = lieu;
        this.auteur = auteur;
        this.messages = new ArrayList<>();
    }

    public void prevenirVues() {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Méthode de sauvegarde d'un événement dans la BDD
     * @return true si la sauvegarde s'est bien passé, false sinon
     * @throws SQLException
     */
    public boolean save() throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "INSERT INTO Evenement VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setInt(1, this.idEv);
            prep.setInt(2, this.calendrierID);
            prep.setString(3, this.nomE);
            java.sql.Timestamp t = new java.sql.Timestamp(this.date.getTime());
            prep.setTimestamp(4, t);
            prep.setString(5, this.description);
            prep.setString(6, this.image);
            prep.setString(7, this.lieu);
            prep.setString(8, this.auteur);
            prep.setBoolean(9, this.visibilite);
            if (prep.executeUpdate() == 0) {
                return false;
            }

        }
        return true;
    }


}

