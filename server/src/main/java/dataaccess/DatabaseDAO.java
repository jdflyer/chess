package dataaccess;

public interface DatabaseDAO {
    // Creates the database and any tables, if needed
    void configureDatabase() throws DataAccessException;

    void clearDatabase() throws DataAccessException;
}
