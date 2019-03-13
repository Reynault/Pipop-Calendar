package mycalendar.modele.bdd;

import java.sql.*;
import java.util.Properties;


public class GestionnaireBDD{
    private static GestionnaireBDD instance;
    public static Properties proprietes;
    private static Connection connect;
    private static String userName = "pipop";
    private static String password ="calendar";
    private static String serverName = "tomgalanx.ovh";
    private static String portNumber = "3306";
    private static String Name = "pipop";
    private static String url;
    private static Properties connectionProps;

    public GestionnaireBDD() throws SQLException{
        connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        url = "jdbc:mysql://" + serverName + ":";
        url += portNumber + "/" + Name;
    }

    public static synchronized GestionnaireBDD getInstance() throws SQLException{
            if(instance==null)
            instance = new GestionnaireBDD();
        return instance;
    }

    public static Connection getConnection() throws SQLException{
        GestionnaireBDD co= getInstance();
        return connect;
    }

    public void createConnection() throws SQLException{
        connect = DriverManager.getConnection(url, connectionProps);
    }

    public void closeConnection() throws SQLException{
        connect.close();
    }

    public void setNomDB(String nomDb){
        GestionnaireBDD.Name =nomDb;
        if(GestionnaireBDD.instance != null){
            try {
                GestionnaireBDD.instance.connect.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        GestionnaireBDD.instance = null;

    }

    public static String getName(){
        return Name;
    }
    
     /**
     * Vérification de l'existence d'un calendrier
     * @param idc id du calendrier
     * @return true si le calendrier existe
     * @throws SQLException
     */
    public static boolean verifierExistenceCalendrier(int idc) throws SQLException {
        Connection connect = GestionnaireBDD.getInstance().getConnection();
        String request = "SELECT * FROM Calendrier WHERE idc=?;";
        PreparedStatement prep = connect.prepareStatement(request);
        prep.setInt(1, idc);
        prep.execute();
        ResultSet rs = prep.getResultSet();
        return rs.next();   // true si un résultat a été trouvé, false sinon
    }
}
