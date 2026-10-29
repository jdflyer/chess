package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.NoUserException;
import dataaccess.UserDAO;
import handler.UsernameAlreadyTakenException;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) throws NoUserException,DataAccessException {
        try {
            var statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM users where username=?");
            statement.setString(1, username);
            var rs = statement.executeQuery();
            if (!rs.next()) {
                throw new NoUserException("There is no user with the username "+username);
            }
            return new UserData(rs.getString("username"),rs.getString("password_hash"),
                    rs.getString("email"));
        }catch (SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }

    @Override
    public Collection<UserData> getAllUsers() throws DataAccessException {
        try {
            ArrayList<UserData> users = new ArrayList<>();
            var rs = DatabaseManager.getConnection().prepareStatement("SELECT * FROM users;").executeQuery();
            while (rs.next()) {
                users.add(new UserData(rs.getString("username"),rs.getString("password_hash"),
                        rs.getString("email")));
            }
            return users;
        }catch (SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        try {
            UserData user = getUser(username);
            return true;
        }catch (NoUserException e) {
            return false;
        }
    }

    @Override
    public void createUser(UserData userData) throws UsernameAlreadyTakenException,DataAccessException {
        if (userExists(userData.username())) {
            throw new UsernameAlreadyTakenException("The username "+userData.username()+" is already taken!");
        }
        try {
            var statement = DatabaseManager.getConnection().prepareStatement("""
                    INSERT INTO users (username,password_hash,email) VALUES (?,?,?);
                    """);
            statement.setString(1,userData.username());
            statement.setString(2,userData.password());
            statement.setString(3,userData.email());
            statement.executeUpdate();
        } catch(SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }
}
