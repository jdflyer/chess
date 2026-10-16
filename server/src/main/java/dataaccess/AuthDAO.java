package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createSession(AuthData authData);

    AuthData getSession(String authToken) throws InvalidAuthTokenException;

    void deleteSession(AuthData authData);
}
