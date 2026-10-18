package server;

import handler.DatabaseHandler;
import handler.LoginHandler;
import handler.RegisterHandler;
import io.javalin.*;
import io.javalin.router.Endpoint;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", context -> new DatabaseHandler(context).handle());
        javalin.post("/user", context -> new RegisterHandler(context).handle());
        javalin.post("/session", context -> new LoginHandler(context).handle());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
