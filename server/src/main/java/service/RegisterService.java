package service;

import dataaccess.*;
import dataaccess.memorydao.MemoryAuthDAO;
import dataaccess.memorydao.MemoryUserDAO;
import handler.UsernameAlreadyTakenException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.ServerConfig;

import java.util.Collection;

public class RegisterService {
    UserDAO userDao;
    AuthDAO authDao;

    public static String hashPassword(String originalPassword) {
        return BCrypt.hashpw(originalPassword,BCrypt.gensalt());
    }
    public static boolean isPasswordMatch(String originalPassword, String hashed) {
        return BCrypt.checkpw(originalPassword, hashed);
    }

    public RegisterService() {
        if (ServerConfig.databaseType == ServerConfig.DatabaseType.MEMORY) {
            this.userDao = new MemoryUserDAO();
            this.authDao = new MemoryAuthDAO();
        }
    }

    public LoginResult register(String username, String password, String email) throws UsernameAlreadyTakenException, DataAccessException {
        if (userDao.userExists(username)) {
            throw new UsernameAlreadyTakenException("Username "+username+" is already taken!");
        }

        UserData data = new UserData(username, hashPassword(password), email);
        userDao.createUser(data);
        AuthData authData = SessionService.createNewAuthData(username);
        authDao.createSession(authData);
        return new LoginResult(username,authData.authToken());
    }

    public Collection<UserData> getAllUsers() throws DataAccessException {
        return userDao.getAllUsers();
    }
}
