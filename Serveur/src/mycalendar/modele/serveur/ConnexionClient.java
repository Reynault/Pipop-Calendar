package mycalendar.modele.serveur;

import java.io.IOException;
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
            socket.close();
            System.out.println("On ferme le client.");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
