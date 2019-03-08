package mycalendar.modele.serveur;

import javafx.beans.binding.BooleanBinding;
import mycalendar.modele.exceptions.BadRequestExeption;
import mycalendar.modele.exceptions.NoRequestException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class ConnexionClient implements Runnable{

    private Socket socket;

    public ConnexionClient(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            System.out.println("Un client s'est connecté.");

            System.out.println(socket.getInetAddress().toString());

            System.out.println("Récupération de la donnée sous la forme d'une hashmap avec" +
                    " le parseur json");

            BufferedReader bos = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            PrintWriter pred = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream()
                            )
                    ),true
            );

            String ligne;
            ligne = bos.readLine();

            System.out.println("Ligne de donnée envoyée par le client: " + ligne);

            HashMap<String, String> donnees =  ParseurJson.getInstance().decode(ligne);

            System.out.println("Traite les données...");

            // Verification de la presence d'une requete dans le message envoyer par le client
            if (!donnees.containsKey("Request")){
                bos.close();
                socket.close();
                throw new NoRequestException();
            }

            // Redirection vers la bonne requete en fonction de la demande du client
            HashMap<String, String> result = null;
            switch (donnees.get("Request")){
                // Authentification
                case "SignIn":{
                    result = ApplicationServeur.getInstance().authentification(
                            donnees.get("Email"),
                            donnees.get("Mdp")
                    );
                    break;
                }
                case "AddEvent":{
                    String calendarName = donnees.get("CalendarName");
                    String eventName = donnees.get("EventName");
                    String eventDescription = donnees.get("EventDescription");
                    String eventPicture = donnees.get("EventPicture");
                    String eventDate = donnees.get("EventDate");
                    String eventLocation = donnees.get("EventLocation");
                    String eventAuthor = donnees.get("EventAuthor");
                    boolean eventVisibility = Boolean.parseBoolean(donnees.get("EventVisibility"));
                    ApplicationServeur.getInstance().creationEvenement(calendarName, eventName, eventDescription, eventPicture, eventDate, eventLocation, eventAuthor, eventVisibility);
                    break;
                }
                case "DeleteEvent":{
                    int idEv = Integer.parseInt(donnees.get("ID"));
                    ApplicationServeur.getInstance().suppressionEvenement(idEv);
                // Inscription
                case "SignUp":{
                    result = ApplicationServeur.getInstance().inscription(
                            donnees.get("Email"),
                            donnees.get("Mdp"),
                            donnees.get("Prenom"),
                            donnees.get("Nom")
                    );
                    break;
                }
                //cas creation d'evenement
                case "CreateEvent":{
                    break;
                }
                default:{
                    // La request ne correspond pas a une demande possible faite au serveur
                    bos.close();
                    socket.close();
                    throw new BadRequestExeption(donnees.get("Request"));
                }
            }

            // Réponse vers le client
            pred.println(ParseurJson.getInstance().encode(result));

            bos.close();
            socket.close();

            System.out.println("On ferme le client.");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
