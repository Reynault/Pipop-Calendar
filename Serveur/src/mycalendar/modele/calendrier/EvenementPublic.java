package mycalendar.modele.calendrier;

import java.util.GregorianCalendar;

public class EvenementPublic extends Evenement {

    public EvenementPublic(int id, int calendrierID, String nom, String description, String image, GregorianCalendar date, String lieu, String auteur) {
        super(id, calendrierID, nom, description, image, date, lieu, auteur);
        this.visibilite = true;
    }

}
