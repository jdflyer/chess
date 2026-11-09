package client;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ServerFacade;
import serverfacade.UsernameTakenException;
import service.DatabaseService;


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

}
