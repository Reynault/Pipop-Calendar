package mycalendar.modele.serveur;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ConnexionClient implements Runnable{

    private Socket socket;

    public ConnexionClient(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            System.out.println("Un client s'est connecté.");

            System.out.printf(socket.getInetAddress().toString());

            System.out.println("Récupération de la donnée sous la forme d'une hashmap avec" +
                    " le parseur json");

            BufferedReader bos = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            String ligne;
            ligne = bos.readLine();

            System.out.println("Ligne envoyée par le client.");

            System.out.printf("Traite les données avec un switch (En fonction du type de requête, méthode différente" +
                    " de ApplicationServeur");

            socket.close();

            System.out.println("On ferme le client.");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
