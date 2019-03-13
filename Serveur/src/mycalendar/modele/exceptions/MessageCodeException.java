package mycalendar.modele.exceptions;

public class MessageCodeException {
    /**
     * Message de retour de la part du serveur
     */
    public static String M_CALENDAR_NOT_FOUND = "Calendar not found.";
    public static String M_CALENDAR_ALREADY_EXIST = "Calendar already exist.";
    public static String M_CALENDAR_ERROR_BDD = "Could not insert / delete or modifie event from database.";

    public static String M_EVENT_ALREADY_EXIST = "Event already exists.";
    public static String M_EVENT_ERROR_BDD = "Could not insert / delete or modifie event into database.";
    public static String M_EVENT_NOT_FOUND = "Event not found.";

    public static String M_USER_NOT_FOUND = "User not found.";
    public static String M_USER_ALREADY_EXIST = "User already exists.";

    public static String M_DATE_ERROR = "Date already passed.";

    public static String M_SUCCESS = "Success.";

    public static String M_NO_CHANGE = "No change were done.";

    public static String M_SIZE_ERROR = "Data size error.";


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

}
