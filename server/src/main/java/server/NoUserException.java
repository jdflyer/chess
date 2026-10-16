package server;

public class NoUserException extends RuntimeException {
    public NoUserException(String message) {
        super(message);
    }
}
