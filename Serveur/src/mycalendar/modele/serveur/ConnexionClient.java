package mycalendar.modele.serveur;

import javafx.beans.binding.BooleanBinding;
import mycalendar.modele.exceptions.BadRequestExeption;
import mycalendar.modele.exceptions.NoRequestException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ConnexionClient implements Runnable{

    private Socket socket;

    public ConnexionClient(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            System.out.println("NOUVEAU CLIENT");
            // Permet de lire les données
            BufferedReader bos = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
            // Permet de répondre au client
            PrintWriter pred = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream()
                            )
                    ), true
            );

            // Formatage de la requête
            String requete = bos.readLine();

            String httpVersion = requete.split(" ")[2];

            requete = requete.split(" ")[1].
                    replaceAll("%22", "\"").
                    replace("/?{", "{");

            System.out.println("DONNEE RECUE : " + requete);
            // Décodage de la chaîne JSON
            HashMap<String, String> donnees = ParseurJson.getInstance().decode(requete);

            // Verification de la presence d'une requete dans le message envoyer par le client
            if (!donnees.containsKey("Request")) {
                bos.close();
                socket.close();
                throw new NoRequestException();
            }

            // Redirection vers la bonne requete en fonction de la demande du client
            HashMap<String, String> result = null;
            switch (donnees.get("Request")) {
                // Authentification
                case "SignIn": {
                    result = ApplicationServeur.getInstance().authentification(
                            donnees.get("Email"),
                            donnees.get("Mdp")
                    );
                    break;
                }
                case "AddEvent": {
                    String calendarName = donnees.get("CalendarName");
                    String eventName = donnees.get("EventName");
                    String eventDescription = donnees.get("EventDescription");
                    String eventPicture = donnees.get("EventPicture");
                    String eventDate = donnees.get("EventDate");
                    String eventLocation = donnees.get("EventLocation");
                    String eventAuthor = donnees.get("EventAuthor");
                    boolean eventVisibility = Boolean.parseBoolean(donnees.get("EventVisibility"));
                    result = ApplicationServeur.getInstance().creationEvenement(calendarName, eventName, eventDescription, eventPicture, eventDate, eventLocation, eventAuthor, eventVisibility);
                    break;
                }
                case "DeleteEvent": {
                    int idEv = Integer.parseInt(donnees.get("ID"));
                    result = ApplicationServeur.getInstance().suppressionEvenement(idEv);
                    break;
                }
                // Inscription
                case "SignUp": {
                    result = ApplicationServeur.getInstance().inscription(
                            donnees.get("Email"),
                            donnees.get("Mdp"),
                            donnees.get("Prenom"),
                                donnees.get("Nom")
                        );
                        break;
                }
                //cas creation d'evenement
                case "CreateEvent": {
                    break;
                }
                default: {
                    // La request ne correspond pas a une demande possible faite au serveur
                    bos.close();
                    socket.close();
                    throw new BadRequestExeption(donnees.get("Request"));
                }

            }
            // Réponse vers le client

            // Construction
            String reponse = ParseurJson.getInstance().encode(result);

            System.out.println("DONNEE ENVOYEE : " + reponse);

            StringBuilder httpResponse = new StringBuilder();
            httpResponse.append(httpVersion+" 200 OK\n");
            httpResponse.append("Connection: Closed\n");
            httpResponse.append("Content-Type: json\n\n");
            httpResponse.append(reponse);

            // Envoi
            pred.println(httpResponse.toString());

            // Fermeture des objets
            pred.close();
            bos.close();
            socket.close();

            System.out.println("FIN CLIENT");
        }catch (Exception e){
            System.out.println("ERREUR CLIENT : \r\t");
            System.out.println(e.getMessage());
        }
    }
}
