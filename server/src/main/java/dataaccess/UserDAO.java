package dataaccess;

import handler.UsernameAlreadyTakenException;
import model.UserData;

import java.util.Collection;

public interface UserDAO {
    // Gets a user by username
    UserData getUser(String username) throws NoUserException;

    Collection<UserData> getAllUsers() throws DataAccessException;

    // Checks if a user exists by username
    boolean userExists(String username) throws DataAccessException;

    // Creates a user by the given user definition
    void createUser(UserData userData) throws UsernameAlreadyTakenException;
}
