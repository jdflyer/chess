package dataaccess.sqldao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dataaccess.DatabaseManager;

public class SQLDatabaseDAO implements DatabaseDAO {
    public void clearDatabase() throws DataAccessException {
        DatabaseManager.deleteDatabase();
        DatabaseManager.createDatabase();
    }
}
