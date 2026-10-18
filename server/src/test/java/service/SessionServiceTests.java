package service;

import dataaccess.DataAccessException;
import dataaccess.InvalidAuthTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SessionServiceTests {
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
            LoginResult loginData = new SessionService().login("myUser","password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void invalidPasswordLogin() {
        try {
            LoginResult data = new RegisterService().register("myUser","password","email@example.com");
            LoginResult loginData = new SessionService().login("myUser","incorrect");
            throw new Exception("InvalidPasswordException should've been thrown!");
        }catch (InvalidPasswordException e) {
            return;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void logoutTest() {
        try {
            LoginResult data = new RegisterService().register("myUser","password","email@example.com");
            new SessionService().logout(data.authToken());
            Assertions.assertEquals(0,new SessionService().getAllSessions().size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void invalidLogoutTest() {
        try {
            LoginResult data = new RegisterService().register("myUser","password","email@example.com");
            new SessionService().logout("0");
            throw new Exception("Should've thrown InvalidAuthTokenException");
        }catch (InvalidAuthTokenException e) {
            return;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
