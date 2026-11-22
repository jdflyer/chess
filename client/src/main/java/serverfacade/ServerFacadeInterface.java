package serverfacade;

import chess.ChessMove;
import model.*;

public interface ServerFacadeInterface {
    public AuthData register(UserData userData) throws BadRequestException, UsernameTakenException;
    public AuthData login(LoginData loginData) throws InvalidUsernamePasswordException, BadRequestException;

    // Always succeeds, the server should handle stale sessions
    public void logout();

    public GameList listAllGames() throws BadRequestException, InvalidSessionException;
    public GameCreateResponse createGame(GameCreateParams gameCreateParams) throws BadRequestException, InvalidSessionException;
    public void joinGame(GameJoinParams joinParams) throws BadRequestException, InvalidSessionException;

    public void connectToGame(int gameId, ServerMessageObserverInterface observer) throws BadRequestException;
    public void makeMove(ChessMove move) throws BadRequestException;
    public void leaveGame() throws BadRequestException;
    public void resignGame() throws BadRequestException;
}
