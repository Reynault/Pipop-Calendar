package mycalendar.modele.exceptions;

public class NoRequestException extends RuntimeException{
    @Override
    public String getMessage() {
        return super.getMessage() + "\r\tNo request made to server.";
    }
}