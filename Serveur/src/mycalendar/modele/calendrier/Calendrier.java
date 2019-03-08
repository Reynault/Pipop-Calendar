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
    public static int getCalendrierID(String nomUtilisateur, String nomCalendrier) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        {
            String request = "SELECT idc FROM utilisateur_calendrier WHERE Email=?;";
            PreparedStatement prep = connect.prepareStatement(request);
            prep.setString(1, nomUtilisateur);
            prep.execute();
            ResultSet rs = prep.getResultSet();
            while (rs.next()) {
                request = "SELECT idc FROM Calendrier WHERE idc=? AND nomC=?;";
                prep = connect.prepareStatement(request);
                prep.setInt(1, rs.getInt("idc"));
                prep.setString(2, nomCalendrier);
                prep.execute();
                ResultSet rst = prep.getResultSet();
                if (rst.next()) {
                    return rst.getInt("idc");
                }
            }
        }
        return -1;
    }

}
