package mycalendar.modele;

import mycalendar.modele.exceptions.ExceptionLimiteAtteinte;
import mycalendar.modele.serveur.ApplicationServeur;
import mycalendar.modele.serveur.GestionnaireClient;

import java.io.IOException;

public class Principale {
    public static void main(String[] args){
        try {
            ApplicationServeur.getInstance().launchServer();
        }catch (ExceptionLimiteAtteinte e) {
            System.out.printf(e.getMessage());
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }catch(IOException e){
            System.out.printf(e.getMessage());
        }finally {
            GestionnaireClient.getInstance().deleteThread();
        }

    }
}
