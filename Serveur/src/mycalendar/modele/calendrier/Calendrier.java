package mycalendar.modele.calendrier;

import mycalendar.modele.bdd.GestionnaireBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Calendrier {

    /**
     * Récupère l'ID d'un calendrier associé à son nom
     * @param nomCalendrier nom du calendrier
     * @return l'ID du calendrier récupéré (-1 si le calendrier n'existe pas)
     * @throws SQLException
     */
    public static int getCalendrierID(String nomCalendrier) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "SELECT idc FROM Calendrier WHERE nomC=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setString(1, nomCalendrier);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            if (rs.next()) {
                return rs.getInt("idc");
            }
        }
        return -1;
    }

}
