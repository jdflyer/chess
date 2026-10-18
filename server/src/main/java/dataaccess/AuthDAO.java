package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createSession(AuthData authData) throws DataAccessException;

    AuthData getSession(String authToken) throws InvalidAuthTokenException;

    void deleteSession(AuthData authData) throws InvalidAuthTokenException;
}
