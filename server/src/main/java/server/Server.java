package server;

import handler.DatabaseHandler;
import handler.GameHandler;
import handler.SessionHandler;
import handler.RegisterHandler;
import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", context -> new DatabaseHandler(context).handleClear());
        javalin.post("/user", context -> new RegisterHandler(context).handleRegister());
        javalin.post("/session", context -> new SessionHandler(context).handleLogin());
        javalin.delete("/session", context -> new SessionHandler(context).handleLogout());
        javalin.get("/game", context -> new GameHandler(context).handleListGames());
        javalin.post("/game", context -> new GameHandler(context).handleCreateGame());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
