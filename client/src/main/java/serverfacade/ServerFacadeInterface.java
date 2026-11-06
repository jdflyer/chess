package serverfacade;

import model.AuthData;
import model.UserData;

public interface ServerFacadeInterface {
    public AuthData register(UserData userData) throws BadRequestException, UsernameTakenException;
    public AuthData login(String username, String password);
    public void logout();
}
