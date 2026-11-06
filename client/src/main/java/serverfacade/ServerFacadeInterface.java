package serverfacade;

import model.AuthData;
import model.UserData;

public interface ServerFacadeInterface {
    public AuthData register(UserData userData);
    public AuthData login(String username, String password);
    public void logout();
}
