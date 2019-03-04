package mycalendar.modele.bdd;

public class GestionnaireBDD {

    private GestionnaireBDD instance;

    private GestionnaireBDD(){

    }

    public GestionnaireBDD getInstance(){
        return instance;
    }
}
