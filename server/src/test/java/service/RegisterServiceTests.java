package service;

import dataaccess.DataAccessException;
import handler.UsernameAlreadyTakenException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterServiceTests {
    @BeforeEach
    public void setup() {
        try {
            new DatabaseService().clearDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void normalRegister() {
        try {
            LoginResult data = new RegisterService().register("myUser","password","email@example.com");
            Assertions.assertEquals("myUser",data.username());
            Assertions.assertEquals(1, new RegisterService().getAllUsers().size());
            Assertions.assertEquals(1, new SessionService().getAllSessions().size());
            Assertions.assertEquals(new UserData("myUser",RegisterService.hashPassword("password"),"email@example.com"),
                    new RegisterService().userDao.getUser("myUser"));
            Assertions.assertEquals(new AuthData(data.authToken(),"myUser"),new SessionService().authDao.getSession(data.authToken()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void registerExistingUser() {
        try {
            LoginResult data = new RegisterService().register("myUser","password","email@example.com");
            LoginResult data2 = new RegisterService().register("myUser","password","email@example.com");
            throw new Exception("Creating a second user did not trigger an exception");
        }catch (UsernameAlreadyTakenException e) {
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
