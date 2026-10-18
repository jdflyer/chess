package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import handler.UsernameAlreadyTakenException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;

public class LoginServiceTests {
    @BeforeEach
    public void setup() {
        try {
            new DatabaseService().clearDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void normalLogin() {
        try {
            LoginResult data = new RegisterService().register("myUser","password","email@example.com");
            LoginResult loginData = new LoginService().login("myUser","password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void invalidPasswordLogin() {
        try {
            LoginResult data = new RegisterService().register("myUser","password","email@example.com");
            LoginResult loginData = new LoginService().login("myUser","incorrect");
            throw new Exception("InvalidPasswordException should've been thrown!");
        }catch (InvalidPasswordException e) {
            return;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
