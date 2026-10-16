package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    HashMap<String, AuthData> authDataMap;

    @Override
    public void createSession(AuthData authData) {
        authDataMap.put(authData.authToken(),authData);
    }

    @Override
    public AuthData getSession(String authToken) throws InvalidAuthTokenException {
        AuthData data = authDataMap.get(authToken);
        if (data == null) {
            throw new InvalidAuthTokenException("The auth token "+authToken+" does not exist!");
        }
        return data;
    }

    @Override
    public void deleteSession(AuthData authData) throws InvalidAuthTokenException {
        if (!authDataMap.containsKey(authData.authToken())) {
            throw new InvalidAuthTokenException("The auth token "+authData.authToken()+" does not exist!");
        }
        authDataMap.remove(authData.authToken());
    }
}
