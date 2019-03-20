package mycalendar.modele.exceptions;

import java.util.HashMap;

/**
 * Fabrique qui garde l'ensemble des messages et codes d'erreurs
 * utilisés par l'application
 */
public class MessageCodeException {
    /**
     * Message de retour de la part du serveur
     */
    public static String M_CALENDAR_NOT_FOUND = "Calendar not found.";
    public static String M_CALENDAR_ALREADY_EXIST = "Calendar already exist.";
    public static String M_CALENDAR_ERROR_BDD = "Could not insert / delete or modify event from database.";

    public static String M_EVENT_ALREADY_EXIST = "Event already exists.";
    public static String M_EVENT_ERROR_BDD = "Could not insert / delete or modify event into database.";
    public static String M_EVENT_NOT_FOUND = "Event not found.";

    public static String M_USER_NOT_FOUND = "User not found.";
    public static String M_USER_ALREADY_EXIST = "User already exists.";

    public static String M_BDD_ERROR = "Erreur de connexion avec la base de données";

    public static String M_DATE_ERROR = "Date already passed.";

    public static String M_SUCCESS = "Success.";

    public static String M_NO_CHANGE = "No change were done.";

    public static String M_SIZE_ERROR = "Data size error.";

    public static String M_THEME_NOT_FOUND = "Themes non trouve.";

    public static String M_DATE_PARSE_ERROR = "Erreur de date.";

    private static String M_GROUPE_ERROR = "Erreur de groupe";

    public static String M_DB_CONSISTENCY_ERROR = "Database consistency error.";

    public static String M_USER_NOT_IN_EVENT = "User not participating in event.";

    /**
     * Code de retour de la part du serveur
     */
    public static String C_NOT_FOUND = "1";
    public static String C_ALREADY_EXIST = "2";

    public static String C_ERROR_BDD = "3";

    public static String C_DATE_ERROR = "4";

    public static String C_SUCCESS = "0";

    public static String C_SIZE_ERROR = "5";

    public static String C_NO_CHANGE = "6";

    public static String C_DATE_PARSE = "7";

    public static String C_GROUPE_ERROR = "8";

    public static String C_DB_CONSISTENCY_ERROR = "8";

    public static void success(HashMap<String, String> map) {
        map.put("Result", MessageCodeException.C_SUCCESS);
        map.put("Message", MessageCodeException.M_SUCCESS);
    }

    public static void date_parse_error(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_DATE_PARSE);
        map.put("Message", MessageCodeException.M_DATE_PARSE_ERROR);
    }

    public static void theme_not_found(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_NOT_FOUND);
        map.put("Message", MessageCodeException.M_THEME_NOT_FOUND);
    }

    public static void bdd_error(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_ERROR_BDD);
        map.put("Message", MessageCodeException.M_BDD_ERROR);
    }

    public static void user_not_found(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_NOT_FOUND);
        map.put("Message", MessageCodeException.M_USER_NOT_FOUND);
    }

    public static void event_already_exist(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_ALREADY_EXIST);
        map.put("Message", MessageCodeException.M_EVENT_ALREADY_EXIST);
    }

    public static void bdd_calendar_error(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_ERROR_BDD);
        map.put("Message", MessageCodeException.M_CALENDAR_ERROR_BDD);
    }

    public static void no_change(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_NO_CHANGE);
        map.put("Message", MessageCodeException.M_NO_CHANGE);
    }

    public static void calendar_not_found(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_NOT_FOUND);
        map.put("Message", MessageCodeException.M_CALENDAR_NOT_FOUND);
    }

    public static void event_not_found(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_NOT_FOUND);
        map.put("Message", MessageCodeException.M_EVENT_NOT_FOUND);
    }

    public static void calendar_already_exist(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_ALREADY_EXIST);
        map.put("Message", MessageCodeException.M_CALENDAR_ALREADY_EXIST);
    }

    public static void size_error(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_SIZE_ERROR);
        map.put("Message", MessageCodeException.M_SIZE_ERROR);
    }

    public static void user_already_exist(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_ALREADY_EXIST);
        map.put("Message", MessageCodeException.M_USER_ALREADY_EXIST);
    }

    public static void bdd_event_error(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_ALREADY_EXIST);
        map.put("Message", MessageCodeException.M_USER_ALREADY_EXIST);
    }

    public static void date(HashMap<String, String> map){
        map.put("Result", MessageCodeException.C_DATE_ERROR);
        map.put("Message", MessageCodeException.M_DATE_ERROR);
    }

    public static void group_not_foud(HashMap<String, String> map) {
        map.put("Result", MessageCodeException.C_GROUPE_ERROR);
        map.put("Message", MessageCodeException.M_GROUPE_ERROR);
    }
}
