package mycalendar.modele.utilisateur;

import java.util.Date;
import java.util.Observable;

public class NotifiCalendrier extends Notif {

    public NotifiCalendrier(int id, String email, String type, String message, Date time) {
        super(id, email, type, message, time);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
