package handler;

import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
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

    public void handleCreateGame() {
        if (!isValidAuthToken()) {
            setUnauthorizedError();
            return;
        }

        try {
            int gameId = new GameService().createGame(getRequestData(GameCreateParams.class).gameName());
            setResponse(new GameCreateResponse(gameId));
        }catch (JsonSyntaxException e) {
            setBadRequestError();
        }catch (DataAccessException e) {
            setInternalServerError(e);
        }
    }
}
