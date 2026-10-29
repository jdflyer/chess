package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.InvalidAuthTokenException;
import dataaccess.UserDAO;
import dataaccess.sqldao.SQLAuthDAO;
import dataaccess.sqldao.SQLDatabaseDAO;
import dataaccess.sqldao.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SQLAuthDAOTests {
    @BeforeEach
    public void setup() {
        try {
            new SQLDatabaseDAO().clearDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createSession() {
        try {
            var dao = new SQLAuthDAO();
            dao.createSession(new AuthData("my_auth_token","user"));
            Assertions.assertEquals(1,dao.getAllSessions().size());
            var session = dao.getSession("my_auth_token");
            Assertions.assertEquals("user",session.username());
            Assertions.assertEquals("my_auth_token",session.authToken());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deleteSession() {
        try {
            var dao = new SQLAuthDAO();
            dao.createSession(new AuthData("my_auth_token","user"));
            Assertions.assertEquals(1,dao.getAllSessions().size());
            dao.deleteSession(new AuthData("my_auth_token","user"));
            Assertions.assertEquals(0,dao.getAllSessions().size());
            try {
                dao.getSession("my_auth_token");
                throw new RuntimeException("Should've hit an InvalidAuthTokenException here");
            }catch (InvalidAuthTokenException e) {
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
