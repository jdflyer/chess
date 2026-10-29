package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import dataaccess.sqldao.SQLDatabaseDAO;
import dataaccess.sqldao.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SQLUserDAOTests {
    @BeforeEach
    public void setup() {
        try {
            new SQLDatabaseDAO().clearDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createUser() {
        try {
            var dao = new SQLUserDAO();
            dao.createUser(new UserData("user","password","example@example.com"));
            Assertions.assertEquals(1,dao.getAllUsers().size());
            Assertions.assertTrue(dao.userExists("user"));
            var user = dao.getUser("user");
            Assertions.assertEquals("user",user.username());
            Assertions.assertEquals("password",user.password());
            Assertions.assertEquals("example@example.com",user.email());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void userExists() {
        try {
            var dao = new SQLUserDAO();
            dao.createUser(new UserData("user","password","example@example.com"));
            Assertions.assertEquals(1,dao.getAllUsers().size());
            Assertions.assertTrue(dao.userExists("user"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
