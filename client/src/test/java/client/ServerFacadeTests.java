package client;

import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.*;
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

    @Test
    public void createGameTest() {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        ClientMain.getSingleton().authData = auth;
        GameCreateResponse r = facade.createGame(new GameCreateParams("game"));
        assert(r != null);
    }

    @Test
    public void createGameNoAuthTest() {
        try {
            GameCreateResponse r = facade.createGame(new GameCreateParams("game"));
            throw new RuntimeException("Should've thrown InvalidSessionException");
        }catch (InvalidSessionException e) {
            return;
        }
    }

    @Test
    public void createGameBadRequestTest() {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        ClientMain.getSingleton().authData = auth;
        try {
            GameCreateResponse r = facade.createGame(new GameCreateParams(null));
            throw new RuntimeException("Should've thrown BadRequestException");
        }catch (BadRequestException e) {
            return;
        }
    }

    @Test
    public void listGamesTest() {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        ClientMain.getSingleton().authData = auth;
        GameCreateResponse r = facade.createGame(new GameCreateParams("game"));
        GameCreateResponse r2 = facade.createGame(new GameCreateParams("game2"));
        GameList list = facade.listAllGames();
        Assertions.assertEquals(2,list.games().length);
    }

    @Test
    public void listGamesNoAuthTest() {
        try {
            facade.listAllGames();
            throw new RuntimeException("Should've thrown InvalidSessionException");
        }catch (InvalidSessionException e) {
            return;
        }
    }

    @Test
    public void joinGameTest() {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        ClientMain.getSingleton().authData = auth;
        GameCreateResponse r = facade.createGame(new GameCreateParams("game"));
        facade.joinGame(new GameJoinParams("WHITE",r.gameID()));
    }

    @Test
    public void joinGameNoAuthTest() {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        ClientMain.getSingleton().authData = auth;
        GameCreateResponse r = facade.createGame(new GameCreateParams("game"));
        ClientMain.getSingleton().authData = null;
        try {
            facade.joinGame(new GameJoinParams("WHITE",r.gameID()));
            throw new RuntimeException("Should've thrown InvalidSessionException");
        }catch (InvalidSessionException e) {
            return;
        }
    }

    @Test
    public void joinGameBadRequestTest() {
        AuthData auth = facade.register(new UserData("test","test","test@example.com"));
        ClientMain.getSingleton().authData = auth;
        try {
            facade.joinGame(new GameJoinParams(null,1));
            throw new RuntimeException("Should've thrown BadRequestException");
        }catch (BadRequestException e) {
            return;
        }
    }
}
