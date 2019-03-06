package mycalendar.modele.serveur;

import mycalendar.modele.exceptions.ExceptionLimiteAtteinte;

import java.util.ArrayList;

public class GestionnaireClient {
    
    private static GestionnaireClient instance = new GestionnaireClient();

    private ArrayList<Thread> threads;
    public static int LIMITE_CLIENT = 100;

    private GestionnaireClient(){
        threads = new ArrayList<Thread>();
    }

    public Thread creerThread(ConnexionClient connexionClient){
        Thread thread = new Thread(connexionClient);
        if(threads.size() < LIMITE_CLIENT) {
            threads.add(thread);
        }else{
            throw new ExceptionLimiteAtteinte();
        }
        return thread;
    }

    public void deleteThread(){
        // Fermeture des threads existants
        for(Thread thread : threads){
            thread.interrupt();
        }
    }

    public static GestionnaireClient getInstance() {
        return instance;
    }
}