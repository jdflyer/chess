package service;

import dataaccess.*;
import handler.UsernameAlreadyTakenException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class RegisterService {
    UserDAO userDao;
    AuthDAO authDao;

    public RegisterService() {
        this.userDao = new MemoryUserDAO();
        this.authDao = new MemoryAuthDAO();
    }

    public RegisterResult register(String username, String password, String email) throws UsernameAlreadyTakenException, DataAccessException {
        if (userDao.userExists(username)) {
            throw new UsernameAlreadyTakenException("Username "+username+" is already taken!");
        }

        // Todo: Use a real password hash algorithm, not Java's hash
        UserData data = new UserData(username, Integer.toString(password.hashCode()), email);
        userDao.createUser(data);
        String authToken = UUID.randomUUID().toString();
        authDao.createSession(new AuthData(authToken,username));
        return new RegisterResult(username,authToken);
    }
}
