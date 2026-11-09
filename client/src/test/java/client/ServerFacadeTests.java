package client;

import dataaccess.DataAccessException;
import model.AuthData;
import model.LoginData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.BadRequestException;
import serverfacade.InvalidUsernamePasswordException;
import serverfacade.ServerFacade;
import serverfacade.UsernameTakenException;
import service.DatabaseService;
import service.SessionService;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        facade = new ServerFacade("localhost",port);
    }

    @BeforeEach
    void setupTest() throws Exception {
        new DatabaseService().clearDatabase();
        ClientMain.getSingleton().authData = null;
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerTest() {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        Assertions.assertEquals("test",auth.username());
    }

    @Test
    public void registerTestUsernameTaken() throws Exception {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        try {
            auth = facade.register(new UserData("test","test","test@example.com"));
            throw new Exception("Should've thrown UsernameTakenException!");
        }catch (UsernameTakenException e) {
            return;
        }
    }

    @Test
    public void registerBadUserData() throws Exception {
        try {
            AuthData auth = facade.register(new UserData(null,null,null));
            throw new Exception("Should've thrown BadRequestException!");
        }catch (BadRequestException e) {
            return;
        }
    }

    @Test
    public void loginTest() {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        AuthData auth2 = facade.login(new LoginData("test","test"));
        Assertions.assertEquals("test",auth.username());
        Assertions.assertEquals("test",auth2.username());
    }

    @Test
    public void loginTestWrongUsername() throws Exception {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        try {
            AuthData auth2 = facade.login(new LoginData("test2","test"));
            throw new Exception("Should've thrown InvalidUsernamePasswordException!");
        }catch (InvalidUsernamePasswordException e) {
            return;
        }
    }

    @Test
    public void loginTestWrongPassword() throws Exception {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        try {
            AuthData auth2 = facade.login(new LoginData("test","password"));
            throw new Exception("Should've thrown InvalidUsernamePasswordException!");
        }catch (InvalidUsernamePasswordException e) {
            return;
        }
    }

    @Test
    public void loginTestBadRequest() throws Exception {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        try {
            AuthData auth2 = facade.login(new LoginData(null,null));
            throw new Exception("Should've thrown BadRequestException!");
        }catch (BadRequestException e) {
            return;
        }
    }

    @Test
    public void logoutTest() throws DataAccessException {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        ClientMain.getSingleton().authData = auth;
        facade.logout();
        Assertions.assertEquals(0,new SessionService().getAllSessions().size());
    }
}
