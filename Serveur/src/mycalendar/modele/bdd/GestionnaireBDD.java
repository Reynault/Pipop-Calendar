package mycalendar.modele.bdd;

import java.sql.*;
import java.util.Properties;


public class GestionnaireBDD{
    private static GestionnaireBDD instance;
    public static Properties proprietes;
    private Connection connect;
    private static String userName = "pipop";
    private static String password ="calendar";
    private static String serverName = "tomgalanx.ovh";
    private static String portNumber = "3306";
    private static String Name = "pipop";

    public GestionnaireBDD() throws SQLException{
        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        String url = "jdbc:mysql://" + serverName + ":";
        url += portNumber + "/" + Name;
        connect = DriverManager.getConnection(url, connectionProps);
    }

    public static synchronized GestionnaireBDD getInstance() throws SQLException{
            if(instance==null)
            instance = new GestionnaireBDD();
        return instance;
    }

    public static Connection getConnection() throws SQLException{
        GestionnaireBDD co= getInstance();
        return co.connect;
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

    public static void main(String[] args) throws SQLException {

        Connection connect = GestionnaireBDD.getInstance().getConnection();

        // creation de la table Personne
        {
            String request = "INSERT INTO themes (nom) VALUES (?);";
            PreparedStatement prep = connect.prepareStatement(request, Statement.RETURN_GENERATED_KEYS);
            prep.setString(1, "Soirées");
            prep.executeUpdate();
            System.out.println("3) ajout themes 'Soirées'");

            // recuperation de la derniere ligne ajoutee (auto increment)
            // recupere le nouvel id
            int autoInc = -1;
            ResultSet rs = prep.getGeneratedKeys();
            if (rs.next()) {
                autoInc = rs.getInt(1);
            }
            System.out.print("  ->  id utilise lors de l'ajout : ");
            System.out.println(autoInc);
            System.out.println();
        }

    }
}
