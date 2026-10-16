package dataaccess;

public class NoUserException extends DataAccessException {
    public NoUserException(String message) {
        super(message);
    }
}
