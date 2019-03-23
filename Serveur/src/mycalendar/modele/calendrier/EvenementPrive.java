package mycalendar.modele.calendrier;

import java.util.Date;

public class EvenementPrive extends Evenement {

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
     * @param couleur
     * @param auteur
     */
    public EvenementPrive(int id, int calendrierID, String nom, String description, String image, Date datedeb, Date datefin, String lieu, String couleur, String auteur) {
        super(id, calendrierID, nom, description, image, datedeb, datefin, lieu, couleur, auteur);
        this.visibilite = false;
    }

}
