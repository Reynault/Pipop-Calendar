package mycalendar.modele.clientTest;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class  ClientTest {
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

            String request = "GET {\"Request\":\"AddEvent\",\"CalendarName\":\"cal_cock\",\"EventName\":\"eb_cock_super\",\"EventDescription\":\"I love cocks\",\"EventPicture\":\"no\",\"EventDate\":\"10:30 24/06/2069\",\"EventLocation\":\"cock_city\",\"EventAuthor\":\"pootis@spenser.tf\",\"EventVisibility\":\"true\"}";
            // String request = "GET {\"Request\":\"DeleteEvent\",\"ID\":\"1\"}";
            System.out.println("DONNEE ENVOYEE : " + request);
            pred.println(request);
            //pred.println("{\"Request\":\"DeleteEvent\",\"ID\":\"6\"}");
            String line = bos.readLine();
            String response = bos.readLine();
            System.out.println("DONNEE RECUES :"+response);
            bos.close();
            pred.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
