package handler;

import chess.ChessGame;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.InvalidAuthTokenException;
import dataaccess.InvalidGameException;
import model.GameCreateParams;
import model.GameCreateResponse;
import model.GameJoinParams;
import handler.gamehandler.InvalidJoinParamsException;
import io.javalin.http.Context;
import service.gameservice.GameJoinTeamTakenException;
import service.gameservice.GameService;
import service.SessionService;

public class GameHandler extends Handler {
    GameService gameService;
    SessionService sessionService;

    public GameHandler(Context ctx) {
        super(ctx);
        gameService = new GameService();
        sessionService = new SessionService();
    }

    public void handleListGames() {
        try {
            if (!isValidAuthToken()) {
                setUnauthorizedError();
                return;
            }
            setResponse(gameService.listAllGames());
        }catch (DataAccessException e) {
            setInternalServerError(e);
        }
    }

    public void handleCreateGame() {
        try {
            if (!isValidAuthToken()) {
                setUnauthorizedError();
                return;
            }
            int gameId = gameService.createGame(getRequestData(GameCreateParams.class).gameName());
            setResponse(new GameCreateResponse(gameId));
        }catch (JsonSyntaxException e) {
            setBadRequestError();
        }catch (DataAccessException e) {
            setInternalServerError(e);
        }
    }

    public void handleJoinGame() {
        try {
            if (!isValidAuthToken()) {
                setUnauthorizedError();
                return;
            }
            var requestData = getRequestData(GameJoinParams.class);

            ChessGame.TeamColor color;
            if (requestData.playerColor().equals("WHITE")){
                color = ChessGame.TeamColor.WHITE;
            }else if (requestData.playerColor().equals("BLACK")) {
                color = ChessGame.TeamColor.BLACK;
            }else {
                throw new InvalidJoinParamsException("Unknown team");
            }

            String username = sessionService.getAuthData(getAuthToken()).username();
            gameService.joinGame(requestData.gameID(),color,username);
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
