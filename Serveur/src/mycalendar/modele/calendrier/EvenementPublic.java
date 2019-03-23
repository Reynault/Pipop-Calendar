package mycalendar.modele.calendrier;

import java.util.Date;

public class EvenementPublic extends Evenement {

    /**
     *
     * @param id
     * @param calendrierID
     * @param nom
     * @param description
     * @param image
     * @param datedeb
     * @param datefin
     * @param lieu
     * @param  couleur
     * @param auteur
     */
    public EvenementPublic(int id, int calendrierID, String nom, String description, String image, Date datedeb, Date datefin, String lieu,String couleur, String auteur) {
        super(id, calendrierID, nom, description, image, datedeb, datefin, lieu, couleur, auteur);
        this.visibilite = true;
    }

}
