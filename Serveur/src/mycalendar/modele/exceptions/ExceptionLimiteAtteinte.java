package mycalendar.modele.exceptions;

public class ExceptionLimiteAtteinte extends RuntimeException{
    @Override
    public String getMessage() {
        return super.getMessage() + "\r\tLimite de clients connectés atteinte.";
    }
}
