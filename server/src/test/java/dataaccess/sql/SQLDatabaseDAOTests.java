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
            // Tables exist already, add an entry and check if it clears
            DatabaseManager.getConnection().prepareStatement("""
                    INSERT INTO users (username,password_hash,email) VALUES ('username','password','email@example.com');
                    """).executeUpdate();
            new SQLDatabaseDAO().clearDatabase();
            var result = DatabaseManager.getConnection().prepareStatement("SELECT COUNT(*) FROM users").executeQuery();
            if (result.next()) {
                Assertions.assertEquals(0,result.getInt(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
