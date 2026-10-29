package dataaccess.sqldao;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SQLAuthDAO implements AuthDAO {
    @Override
    public void createSession(AuthData authData) throws DataAccessException {
        try {
            var statement = DatabaseManager.getConnection().prepareStatement("""
                    INSERT INTO sessions (username,auth_token) VALUES (?,?);
                    """);
            statement.setString(1,authData.username());
            statement.setString(2,authData.authToken());
            statement.executeUpdate();
        } catch(SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }

    @Override
    public AuthData getSession(String authToken) throws InvalidAuthTokenException,DataAccessException {
        try {
            var statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM users where auth_token=?");
            statement.setString(1, authToken);
            var rs = statement.executeQuery();
            if (!rs.next()) {
                throw new InvalidAuthTokenException("The auth token "+authToken+" does not exist!");
            }
            return new AuthData(rs.getString("username"),rs.getString("auth_token"));
        }catch (SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }

    @Override
    public Collection<AuthData> getAllSessions() throws DataAccessException {
        try {
            ArrayList<AuthData> sessions = new ArrayList<>();
            var rs = DatabaseManager.getConnection().prepareStatement("SELECT * FROM sessions;").executeQuery();
            while (rs.next()) {
                sessions.add(new AuthData(rs.getString("username"),rs.getString("auth_token")));
            }
            return sessions;
        }catch (SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }

    protected boolean sessionExists(String authToken) throws DataAccessException {
        try {
            getSession(authToken);
            return true;
        }catch (InvalidAuthTokenException e) {
            return false;
        }
    }

    @Override
    public void deleteSession(AuthData authData) throws InvalidAuthTokenException, DataAccessException {
        if (!sessionExists(authData.authToken())) {
            throw new InvalidAuthTokenException("The auth token "+authData.authToken()+" does not exist!");
        }
        try {
            var statement = DatabaseManager.getConnection().prepareStatement("""
                    DELETE FROM sessions WHERE auth_token=?;
                    """);
            statement.setString(1,authData.authToken());
            statement.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }
}
