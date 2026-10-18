package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.DatabaseService;

public class DatabaseHandler extends Handler {

    public DatabaseHandler(Context ctx) {
        super(ctx);
    }

    protected void handleClear() {
        try {
            DatabaseService service = new DatabaseService();
            service.clearDatabase();
            setResponseJson("{}");
        }catch (DataAccessException e) {
            setInternalServerError();
        }
    }

    @Override
    public void handle() {
       handleClear();
    }
}
