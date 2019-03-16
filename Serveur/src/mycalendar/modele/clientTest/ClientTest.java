package mycalendar.modele.clientTest;

import mycalendar.modele.serveur.ApplicationServeur;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class  ClientTest {
    private static void lancerClient(String requete, boolean post) throws IOException {
        // Création socket
        int port = 55555;
        InetAddress ip = InetAddress.getByName(ApplicationServeur.URL);

        Socket socket = new Socket(ip, port);
        PrintWriter pred = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()
                        )
                ),true
        );

        BufferedReader bos = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));

        String request = "";
        if(post){
            request = "POST / HTTP/1.1\n\r\n"+requete;
        }else{
            request = "GET "+requete+" HTTP/1.1";
        }
        System.out.println("DONNEE ENVOYEE : " + request);
        pred.println(request);

        StringBuilder line = new StringBuilder();
        line.append("\n\n");
        String l = bos.readLine();
        while(l != null){
            line.append(l+"\n");
            l = bos.readLine();
        }
        System.out.println("DONNEE RECUES :"+line.toString());
        bos.close();
        pred.close();
    }

    public static void main(String[] args) {
        try{
            lancerClient("{\"Request\":\"GetUsers\",\"FirstName\":\"devmaster\",\"LastName\":\"devmaster\"}", false);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
