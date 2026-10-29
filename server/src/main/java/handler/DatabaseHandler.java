package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.DatabaseService;

public class DatabaseHandler extends Handler {
    DatabaseService databaseService;

    public DatabaseHandler(Context ctx) {
        super(ctx);
        databaseService = new DatabaseService();
    }

    public void handleClear() {
        try {
            databaseService.clearDatabase();
            setResponseJson("{}");
        }catch (DataAccessException e) {
            setInternalServerError();
        }
    }
}
