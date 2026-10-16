package dataaccess;

public class InvalidAuthTokenException extends DataAccessException {
    public InvalidAuthTokenException(String message) {
        super(message);
    }
}
