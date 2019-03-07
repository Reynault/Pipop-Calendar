package mycalendar.modele.exceptions;

public class BadRequestExeption extends RuntimeException{
    private final String message;

    public BadRequestExeption(String request) {
        this.message = request;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\r\tBad request to server: " + message;
    }
}
