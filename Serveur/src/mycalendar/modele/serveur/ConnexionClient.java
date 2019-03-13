package mycalendar.modele.serveur;

import javafx.beans.binding.BooleanBinding;
import mycalendar.modele.bdd.GestionnaireBDD;
import mycalendar.modele.calendrier.Calendrier;
import mycalendar.modele.exceptions.BadRequestExeption;
import mycalendar.modele.exceptions.NoRequestException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
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
            // Création de la connexion
            GestionnaireBDD.getInstance().createConnection();

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
            StringBuilder json = new StringBuilder();
            String[] lignes;

            // Récupération de la version de http
            String[] tab = requete.split(" ");
            String httpVersion = tab[tab.length-1];
            // Récupération de la méthode
            String method = tab[0];

            if (method.equals("GET")){
                // Si c'est un get, on récupère les paramètres dans le header
                json.append(requete.substring(4,requete.length()-8).
                        replaceAll("%22", "\"").
                        replace("/?{", "{"));
            }else if(method.equals("POST")){
                // Lecture du body
                ArrayList<Byte> data = new ArrayList<Byte>();
                // tant qu'il y en a
                while(bos.ready()){
                    // on lie les caractères
                    data.add((byte)bos.read());
                }
                // ensuite, on les place dans un tableau
                byte[] cbo = new byte[data.size()];
                for(int i = 0 ; i < data.size(); i++){
                    cbo[i] = data.get(i);
                }
                // puis on les transforme en chaîne de caractères
                requete = new String(cbo, Charset.defaultCharset());
                lignes = requete.split("\n");
                boolean trouve = false;
                for(int i = 0; i < lignes.length; i++){
                    if(lignes[i].replace(" ","").isEmpty()){
                        trouve = true;
                    }
                    if(trouve){
                        json.append(lignes[i]);
                    }
                }
                if(!trouve){
                    throw new BadRequestExeption("Body non trouvé");
                }
                System.out.printf("json :"+json.toString());
            }


            System.out.println("DONNEE RECUE : " + json.toString());

            // Décodage de la chaîne JSON
            HashMap<String, String> donnees = ParseurJson.getInstance().decode(json.toString());

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
                case "LoadCalendar":
                {
                    result = ApplicationServeur.getInstance().loadCalendars(
                            donnees.get("Email")
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
            GestionnaireBDD.getInstance().closeConnection();
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
