package service;

import dataaccess.*;
import handler.UsernameAlreadyTakenException;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class LoginService {
    UserDAO userDao;
    AuthDAO authDao;

    public static AuthData createNewAuthData(String username) {
        return new AuthData(UUID.randomUUID().toString(),username);
    }

    public LoginService() {
        this.userDao = new MemoryUserDAO();
        this.authDao = new MemoryAuthDAO();
    }

    public LoginResult login(String username, String password) throws InvalidPasswordException, NoUserException, DataAccessException {
        UserData user = userDao.getUser(username);
        if (!user.password().equals(RegisterService.hashPassword(password))) {
            throw new InvalidPasswordException("The password hashes did not match");
        }
        AuthData authData = createNewAuthData(username);
        authDao.createSession(authData);
        return new LoginResult(username,authData.authToken());
    }

    public Collection<AuthData> getAllSessions() throws DataAccessException {
        return authDao.getAllSessions();
    }
}
