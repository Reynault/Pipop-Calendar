package mycalendar.modele.utilisateur;

import java.util.Date;

public class NotifiCalendrier extends Notif {

    public NotifiCalendrier(int id, String email, String type, String message, Date time) {
        super(id, email, type, message, time);
    }
}
