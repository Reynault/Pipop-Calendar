package mycalendar.modele.clientTest;

import mycalendar.modele.serveur.ApplicationServeur;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class  ClientTest {
    private static void lancerClient(String requete, boolean post) throws IOException {
        // Création socket
        int port = 3307;
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
        System.out.println("DONNEE RECUE :"+line.toString());
        bos.close();
        pred.close();
    }

    public static void main(String[] args) {
        try{
            /*lancerClient("{\"Request\":\"DeleteEvent\"," +
                    "\"Email\":\"test@test.com\","+
                    "\"Mdp\":\"ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff\","+
                    "\"ID\":\"73\"" +
                    "}", true);*/
            //String email, int idC, boolean b
            /*lancerClient("{\"Request\":\"DeleteCalendar\"," +
                    "\"Email\":\"test@test\","+
                    "\"Mdp\":\"ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff\","+
                    "\"IdCalendar\":\"8\","+
                    "\"SuppCal\":\"true\"" +
                    "}", true);*/
            lancerClient("{\"Request\":\"AddEvent\"," +
                    "\"CalendarName\":\"TOUTENKLAKOS\","+
                    "\"EventName\":\"77\","+
                    "\"EventDescription\":\"77\","+
                    "\"EventDate\":\"26/03/2019 20:00\"," +
                    "\"EventDateFin\":\"26/03/2019 22:00\"," +
                    "\"EventLocation\":\"77\"," +
                    "\"EventVisibility\":\"false\"," +
                    "\"Email\":\"test@test.com\"," +
                    "\"EventColor\":\"deeppurple\"," +
                    "\"Mdp\":\"ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff\""+
                    "}", true);
            /*
            String calendarName = donnees.get("CalendarName");
                            String eventName = donnees.get("EventName");
                            String eventDescription = donnees.get("EventDescription");
                            String image = "";
                            String eventDateDeb = donnees.get("EventDate");
                            String eventDateFin = donnees.get("EventDateFin");
                            String eventLocation = donnees.get("EventLocation");
                            String eventColor = donnees.get("EventColor");
             */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
