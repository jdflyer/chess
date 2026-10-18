package server;

public class ServerConfig {
    public enum DatabaseType {
        MEMORY,
        SQL
    };

    public static DatabaseType databaseType = DatabaseType.MEMORY;
}
