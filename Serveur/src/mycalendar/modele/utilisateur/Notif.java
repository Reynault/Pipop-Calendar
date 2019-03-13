package mycalendar.modele.utilisateur;

import java.util.Date;

public abstract class Notif {

    private int idN;
    private String Email;
    private String type;
    private String messageN;
    private Date TimeN;

    public Notif(int id, String email, String type, String message, Date time) {

    }

}
