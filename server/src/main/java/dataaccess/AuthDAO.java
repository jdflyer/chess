package dataaccess;

import model.AuthData;

import java.util.Collection;

public interface AuthDAO {
    void createSession(AuthData authData) throws DataAccessException;

    AuthData getSession(String authToken) throws InvalidAuthTokenException;
    Collection<AuthData> getAllSessions() throws DataAccessException;

    void deleteSession(AuthData authData) throws InvalidAuthTokenException;
}
