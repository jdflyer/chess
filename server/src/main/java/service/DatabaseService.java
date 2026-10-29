package service;

import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dataaccess.memorydao.MemoryDatabaseDAO;
import dataaccess.sqldao.SQLDatabaseDAO;
import server.ServerConfig;

public class DatabaseService {
    DatabaseDAO databaseDAO;

    public DatabaseService() {
        if (ServerConfig.databaseType == ServerConfig.DatabaseType.MEMORY) {
            this.databaseDAO = new MemoryDatabaseDAO();
        }else if (ServerConfig.databaseType == ServerConfig.DatabaseType.SQL) {
            this.databaseDAO = new SQLDatabaseDAO();
        }
    }

    public void clearDatabase() throws DataAccessException {
        databaseDAO.clearDatabase();
    }
}
