package dataaccess;

import model.UserData;

public interface UserDAO {
    // Gets a user by username
    UserData getUser(String username) throws NoUserException;

    // Creates a user by the given user definition
    void createUser(UserData userData) throws UsernameAlreadyTakenException;
}
