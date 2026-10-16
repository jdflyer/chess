package handler;

import dataaccess.AuthDAO;
import dataaccess.NoUserException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class RegisterHandler {
    UserDAO userDao;
    AuthDAO authDao;

    RegisterHandler() {
        this.userDao = new MemoryUserDAO();
        this.authDao = new MemoryAuthDAO();
    }

    RegisterResult register(String username, String password, String email) throws UsernameAlreadyTakenException {
        try {
            UserData data = userDao.getUser(username);
        } catch (NoUserException e) {
            // This is where we want this function to normally go
            // Todo: Use a real password hash algorithm, not Java's hash
            UserData data = new UserData(username, Integer.toString(password.hashCode()), email);
            userDao.createUser(data);
            String authToken = UUID.randomUUID().toString();
            authDao.createSession(new AuthData(authToken,username));
            return new RegisterResult(username,authToken);
        }
        throw new UsernameAlreadyTakenException("Username "+username+" is already taken!");
    }
}
