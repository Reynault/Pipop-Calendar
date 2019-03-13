package mycalendar.modele.utilisateur;

import java.util.Date;

public class NotifiEvenement extends Notif {

    public NotifiEvenement(int id, String email, String type, String message, Date time) {
        super(id, email, type, message, time);
    }
}
