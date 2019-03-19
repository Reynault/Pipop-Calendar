package mycalendar.modele;

import mycalendar.modele.serveur.ApplicationServeur;

import java.io.IOException;

public class Principale {
    public static void main(String[] args){
        try {
            ApplicationServeur.getInstance().launchServer();
        }catch(IOException e) {
            System.out.println("Erreur In/Out côté serveur :");
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println("Exception côté serveur :");
            System.out.println(e.getMessage());
        }finally {
            main(args);
        }
    }
}
