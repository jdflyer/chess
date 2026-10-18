package handler.gamehandler;

import chess.ChessGame;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.InvalidAuthTokenException;
import dataaccess.InvalidGameException;
import handler.Handler;
import io.javalin.http.Context;
import service.gameservice.GameJoinTeamTakenException;
import service.gameservice.GameService;
import service.SessionService;

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

    public void handleJoinGame() {
        if (!isValidAuthToken()) {
            setUnauthorizedError();
            return;
        }

        try {
            var requestData = getRequestData(GameJoinParams.class);

            ChessGame.TeamColor color;
            if (requestData.playerColor().equals("WHITE")){
                color = ChessGame.TeamColor.WHITE;
            }else if (requestData.playerColor().equals("BLACK")) {
                color = ChessGame.TeamColor.BLACK;
            }else {
                throw new InvalidJoinParamsException("Unknown team");
            }

            String username = new SessionService().getAuthData(getAuthToken()).username();
            new GameService().joinGame(requestData.gameID(),color,username);
            setResponseJson("{}");
        }catch (GameJoinTeamTakenException e) {
            setResponseError(403,"already taken");
        }catch (JsonSyntaxException | InvalidJoinParamsException | InvalidGameException e) {
            setBadRequestError();
        }catch (InvalidAuthTokenException e) {
            setUnauthorizedError();
        } catch (DataAccessException e) {
            setInternalServerError(e);
        }
    }
}
