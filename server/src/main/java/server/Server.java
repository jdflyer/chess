package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import handler.DatabaseHandler;
import handler.GameHandler;
import handler.SessionHandler;
import handler.RegisterHandler;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        if (ServerConfig.databaseType == ServerConfig.DatabaseType.SQL) {
            try{
                // Call the function every time, it will only create a database if it doesn't exist
                DatabaseManager.createDatabase();
            } catch(DataAccessException e) {
                throw new RuntimeException("Could not create a database! Is mysql running and connected?");
            }
        }

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", context -> new DatabaseHandler(context).handleClear());
        javalin.post("/user", context -> new RegisterHandler(context).handleRegister());
        javalin.post("/session", context -> new SessionHandler(context).handleLogin());
        javalin.delete("/session", context -> new SessionHandler(context).handleLogout());
        javalin.get("/game", context -> new GameHandler(context).handleListGames());
        javalin.post("/game", context -> new GameHandler(context).handleCreateGame());
        javalin.put("/game", context -> new GameHandler(context).handleJoinGame());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
