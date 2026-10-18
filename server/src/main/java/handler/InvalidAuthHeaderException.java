package handler;

public class InvalidAuthHeaderException extends RuntimeException {
    public InvalidAuthHeaderException(String message) {
        super(message);
    }
}
