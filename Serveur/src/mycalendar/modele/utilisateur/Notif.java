package mycalendar.modele.utilisateur;

import mycalendar.modele.bdd.GestionnaireBDD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Observer;

public abstract class Notif implements Observer {

    protected int idN;
    protected String Email;
    protected String type;
    protected String messageN;
    protected Date TimeN;

    public Notif(int id, String email, String type, String message, Date time) {
        this.idN = id;
        this.Email =email;
        this.type = type;
        this.messageN = message;
        this.TimeN=time;
    }



}
