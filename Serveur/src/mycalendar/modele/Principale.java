package mycalendar.modele;

import mycalendar.modele.serveur.ApplicationServeur;

import java.io.IOException;

public class Principale {
    public static void main(String[] args){
        try {
            ApplicationServeur.getInstance().launchServer();
        }catch(IOException e){
            System.out.printf(e.getMessage());
        }
    }
}
