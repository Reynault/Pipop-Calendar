package mycalendar.modele.utilisateur;

import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.Evenement;
import mycalendar.modele.calendrier.EvenementPrive;
import mycalendar.modele.calendrier.EvenementPublic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

public class NotifiEvenement extends Notif {
    protected int idE;

    public NotifiEvenement(int id, String email, String type, String message, Date time, int idE) {
        super(id, email, type, message, time);
        this.idE=idE;
    }

    public boolean save() throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "INSERT INTO Notification VALUES (?, ?, ?, ?, ?, ?);";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setInt(1, this.idN);
            prep.setString(2, this.type);
            prep.setString(3, this.messageN);
            java.sql.Timestamp t = new java.sql.Timestamp(this.TimeN.getTime());
            prep.setTimestamp(4, t);
            prep.setInt(5, this.idE);
            prep.setString(6, this.Email);

            if (prep.executeUpdate() == 0) { // Pas de nouvelles lignes insérées lors de l'exécution de la requête, il y a donc un problème
                return false;
            }

        }
        return true;
    }

    public static ArrayList<NotifiEvenement> find(String Email) throws SQLException, ParseException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        ArrayList<NotifiEvenement> notifs = new ArrayList<>();
        {
            String request = "SELECT * FROM Notification WHERE Email=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setString(1,Email);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            while (rs.next()) {
                    notifs.add(new NotifiEvenement(rs.getInt("idN"), rs.getString("Email"), rs.getString("type"),
                            rs.getString("messageN"), rs.getDate("timeN"), rs.getInt("idE")));
            }
        }
        return notifs;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
