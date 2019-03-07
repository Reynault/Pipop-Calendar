package mycalendar.modele.clientTest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientTest {
    public static void main(String[] args) {
        // Création socket
        int port = 3306;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            Socket socket = new Socket(ip, port);
            PrintWriter pred = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream()
                            )
                    ),true
            );

            pred.print("{\"description\":\"quarter\"}");
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
