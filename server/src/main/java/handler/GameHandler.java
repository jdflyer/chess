package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;

public class GameHandler extends Handler {

    public GameHandler(Context ctx) {
        super(ctx);
    }

    public void handleListGames() {
        if (!isValidAuthToken()) {
            setUnauthorizedError();
            return;
        }

        try {
            setResponse(new GameService().listAllGames());
        }catch (DataAccessException e) {
            setInternalServerError(e);
        }
    }
}
