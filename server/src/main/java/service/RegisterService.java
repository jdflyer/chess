package service;

import dataaccess.*;
import handler.UsernameAlreadyTakenException;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.UUID;

public class RegisterService {
    UserDAO userDao;
    AuthDAO authDao;

    public static String hashPassword(String originalPassword) {
        // todo: Use an actual password hashing algorithm, this is just here to show the method
        return Integer.toString(originalPassword.hashCode());
    }

    public RegisterService() {
        this.userDao = new MemoryUserDAO();
        this.authDao = new MemoryAuthDAO();
    }

    public LoginResult register(String username, String password, String email) throws UsernameAlreadyTakenException, DataAccessException {
        if (userDao.userExists(username)) {
            throw new UsernameAlreadyTakenException("Username "+username+" is already taken!");
        }

        UserData data = new UserData(username, hashPassword(password), email);
        userDao.createUser(data);
        AuthData authData = LoginService.createNewAuthData(username);
        authDao.createSession(authData);
        return new LoginResult(username,authData.authToken());
    }

    public Collection<UserData> getAllUsers() throws DataAccessException {
        return userDao.getAllUsers();
    }
}
