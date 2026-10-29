package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memorydao.MemoryAuthDAO;
import dataaccess.memorydao.MemoryGameDAO;
import dataaccess.memorydao.MemoryUserDAO;
import dataaccess.sqldao.SQLAuthDAO;
import dataaccess.sqldao.SQLGameDAO;
import dataaccess.sqldao.SQLUserDAO;

public class ServerConfig {
    public enum DatabaseType {
        MEMORY,
        SQL
    };

    public static DatabaseType databaseType = DatabaseType.SQL;

    public static UserDAO getUserDao() {
        switch (databaseType) {
            case MEMORY -> {
                return new MemoryUserDAO();
            }
            case SQL -> {
                return new SQLUserDAO();
            }
        }
        return null;
    }

    public static AuthDAO getAuthDao() {
        switch (databaseType) {
            case MEMORY -> {
                return new MemoryAuthDAO();
            }
            case SQL -> {
                return new SQLAuthDAO();
            }
        }
        return null;
    }

    public static GameDAO getGameDao() {
        switch (databaseType) {
            case MEMORY -> {
                return new MemoryGameDAO();
            }
            case SQL -> {
                return new SQLGameDAO();
            }
        }
        return null;
    }
}
