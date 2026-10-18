package dataaccess.memorydao;

import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;

public class MemoryDatabaseDAO implements DatabaseDAO {
    public void clearDatabase() throws DataAccessException {
        // Clear all of our static maps, acts as if we were clearing tables
        MemoryAuthDAO.authDataMap.clear();
        MemoryUserDAO.userDataMap.clear();
        MemoryGameDAO.gameDataMap.clear();
        MemoryGameDAO.nextGameId = 1;
    }
}
