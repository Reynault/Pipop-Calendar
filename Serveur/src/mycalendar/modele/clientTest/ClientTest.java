package mycalendar.modele.clientTest;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTest {
    public static void main(String[] args) {
        // Création socket
        int port = 3306;
        try {
            InetAddress ip = InetAddress.getByName("localhost");
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

            String request = "GET {\"Request\":\"SignIn\"}";
            System.out.println("DONNEE ENVOYEE :"+request);
            pred.println(request);
            String response = bos.readLine();
            System.out.println("DONNEE RECUES :"+response);
            bos.close();
            pred.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
