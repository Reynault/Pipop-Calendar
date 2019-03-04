package mycalendar.modele.serveur;

import java.util.Observable;
import java.util.Observer;

public class ApplicationServeur implements Observer {

    private ApplicationServeur instance = new ApplicationServeur();

    private ApplicationServeur(){

    }

    public ApplicationServeur getInstance(){
        return instance;
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
