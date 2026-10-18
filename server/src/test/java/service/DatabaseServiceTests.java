package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseServiceTests {
    @BeforeEach
    public void setup() {
        try {
            new DatabaseService().clearDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void databaseClearSuccessful() {
        try {
            // Enters into user and auth tables
            new RegisterService().register("myUser","password","email@example.com");
            new DatabaseService().clearDatabase();
            Assertions.assertEquals(0, new RegisterService().getAllUsers().size());
            Assertions.assertEquals(0, new SessionService().getAllSessions().size());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
