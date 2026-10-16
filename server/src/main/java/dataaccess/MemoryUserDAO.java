package dataaccess;

import handler.UsernameAlreadyTakenException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    HashMap<String,UserData> userDataMap;

    @Override
    public UserData getUser(String username) throws NoUserException {
        UserData data = userDataMap.get(username);
        if (data == null) {
            throw new NoUserException("There is no user with the username "+username);
        }
        return data;
    }

    @Override
    public void createUser(UserData userData) throws UsernameAlreadyTakenException {
        if (userDataMap.containsKey(userData.username())) {
            throw new UsernameAlreadyTakenException("The username "+userData.username()+" is already taken!");
        }
        userDataMap.put(userData.username(),userData);
    }
}
