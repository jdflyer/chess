package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.sqldao.SQLDatabaseDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SQLDatabaseDAOTests {
    @BeforeEach
    public void setup() {
        try {
            new SQLDatabaseDAO().clearDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void databaseClearSuccessful() {
        try {
            // Creates a table, checks if it exists
            DatabaseManager.getConnection().prepareStatement("CREATE TABLE test( id INT );").executeUpdate();
            DatabaseManager.getConnection().prepareStatement("INSERT INTO test VALUES (1);").executeUpdate();
            new SQLDatabaseDAO().clearDatabase();
            try {
                var result = DatabaseManager.getConnection().prepareStatement("SELECT ID FROM test").executeQuery();
                throw new Exception("Should've errored here: the table doesn't exist");
            } catch (SQLException e) {
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
