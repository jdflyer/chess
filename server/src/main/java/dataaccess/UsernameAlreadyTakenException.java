package dataaccess;

public class UsernameAlreadyTakenException extends DataAccessException {
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
