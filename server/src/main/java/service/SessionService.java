package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.UUID;

public class SessionService {
    UserDAO userDao;
    AuthDAO authDao;

    public static AuthData createNewAuthData(String username) {
        return new AuthData(UUID.randomUUID().toString(),username);
    }

    public SessionService() {
        this.userDao = new MemoryUserDAO();
        this.authDao = new MemoryAuthDAO();
    }

    public AuthData getAuthData(String authToken) throws InvalidAuthTokenException {
        return authDao.getSession(authToken);
    }

    public boolean isValidAuth(String authToken) {
        try {
            AuthData data = getAuthData(authToken);
            return data.authToken().equals(authToken);
        } catch (InvalidAuthTokenException e) {
            return false;
        }
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

    public void logout(String authToken) throws InvalidAuthTokenException {
        AuthData data = authDao.getSession(authToken);
        authDao.deleteSession(data);
    }
}
