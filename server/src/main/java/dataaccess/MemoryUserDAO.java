package dataaccess;

import handler.UsernameAlreadyTakenException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    // This member is static, its lifetime isn't tied to the class
    public static HashMap<String,UserData> userDataMap = new HashMap<>();

    @Override
    public UserData getUser(String username) throws NoUserException {
        UserData data = userDataMap.get(username);
        if (data == null) {
            throw new NoUserException("There is no user with the username "+username);
        }
        return data;
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        return userDataMap.containsKey(username);
    }

    @Override
    public void createUser(UserData userData) throws UsernameAlreadyTakenException {
        if (userDataMap.containsKey(userData.username())) {
            throw new UsernameAlreadyTakenException("The username "+userData.username()+" is already taken!");
        }
        userDataMap.put(userData.username(),userData);
    }
}
