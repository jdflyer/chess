package serverfacade;

import model.AuthData;
import model.LoginData;
import model.UserData;

public interface ServerFacadeInterface {
    public AuthData register(UserData userData) throws BadRequestException, UsernameTakenException;
    public AuthData login(LoginData loginData) throws InvalidUsernamePasswordException, BadRequestException;

    // Always succeeds, the server should handle stale sessions
    public void logout();
}
