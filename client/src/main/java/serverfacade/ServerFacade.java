package serverfacade;

import model.AuthData;
import model.UserData;

public class ServerFacade implements ServerFacadeInterface {
    public AuthData register(UserData userData) {
        return new AuthData("","");
    }

    public AuthData login(String username, String password) {
        return new AuthData("","");
    }

    public void logout() {

    }
}
