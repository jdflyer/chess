package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dataaccess.DatabaseManager;

public class SQLDatabaseDAO implements DatabaseDAO {
    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.createTables();
    }

    public void clearDatabase() throws DataAccessException {
        DatabaseManager.deleteDatabase();
        configureDatabase();
    }
}
