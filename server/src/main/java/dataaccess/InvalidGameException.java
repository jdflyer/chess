package dataaccess;

public class InvalidGameException extends DataAccessException {
    public InvalidGameException(String message) {
        super(message);
    }
}
