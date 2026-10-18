package service;

import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dataaccess.MemoryDatabaseDAO;

public class DatabaseService {
    DatabaseDAO databaseDAO;

    public DatabaseService() {
        this.databaseDAO = new MemoryDatabaseDAO();
    }

    public void clearDatabase() throws DataAccessException {
        databaseDAO.clearDatabase();
    }
}
