package mycalendar.modele.clientTest;

import java.io.*;
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

            BufferedReader bos = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            pred.println("{\"Request\":\"AddEvent\",\"ID\":\"1\",\"CalendarName\":\"cal_cock\",\"EventName\":\"eb_cock_super\",\"EventDescription\":\"I love cocks\",\"EventPicture\":\"no\",\"EventDate\":\"10:30 24/06/2019\",\"EventLocation\":\"cock_city\",\"EventAuthor\":\"cockman\", \"EventVisibility\":\"true\"}");
            //pred.println("{\"Request\":\"DeleteEvent\",\"ID\":\"2\"}");
            String line = bos.readLine();
            System.out.println(line);
            bos.close();
            pred.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
