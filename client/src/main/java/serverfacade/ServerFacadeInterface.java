package serverfacade;

import model.*;

import java.util.Collection;

public interface ServerFacadeInterface {
    public AuthData register(UserData userData) throws BadRequestException, UsernameTakenException;
    public AuthData login(LoginData loginData) throws InvalidUsernamePasswordException, BadRequestException;

    // Always succeeds, the server should handle stale sessions
    public void logout();

    public GameList listAllGames() throws BadRequestException, InvalidSessionException;
    public GameCreateResponse createGame(GameCreateParams gameCreateParams) throws BadRequestException, InvalidSessionException;
}
