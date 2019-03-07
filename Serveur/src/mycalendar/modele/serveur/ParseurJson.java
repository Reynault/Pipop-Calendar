package mycalendar.modele.serveur;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Classe Parseur JSON qui sert de façade sur la librairie GSON qui permet de coder et de décoder
 * un dictionnaire en JSON
 */
public class ParseurJson {

    private Gson gson;

    private static ParseurJson instance = new ParseurJson();

    /**
     * Constructeur de la classe Parseur Json qui initialise
     * l'objet Gson qui encode et décode
     */
    private ParseurJson(){
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();
    }

    public static ParseurJson getInstance(){
        return instance;
    }

    /**
     * Méthode qui permet de décoder une chaîne en json et
     * de la mettre dans une hashmap
     * @param json La chaîne en json à décoder
     * @return la hashmap résultante
     */
    public HashMap<String, String> decode(String json){
        HashMap<String, String> hash = gson.fromJson(json, HashMap.class);
        return hash;
    }

    /**
     * Méthode qui permet d'encoder en JSON
     * @param param La hashmap à encoder
     * @return le json résultant
     */
    public String encode(HashMap<String,String> param){
        String json = gson.toJson(param);
        return json;
    }
}
