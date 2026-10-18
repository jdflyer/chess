package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.UserData;
import service.RegisterResult;
import service.RegisterService;

public class RegisterHandler extends Handler {

    public RegisterHandler(Context ctx) {
        super(ctx);
    }

    @Override
    public void handle() {
        try {
            UserData registerData = new Gson().fromJson(ctx.body(), UserData.class);
            RegisterResult result = new RegisterService().register(registerData.username(),registerData.password(),registerData.email());
            setResponse(result);
        }catch (JsonSyntaxException e) {
            setBadRequestError();
        }catch (DataAccessException e) {
            setInternalServerError();
        }catch (UsernameAlreadyTakenException e) {
            setResponseError(403, "already taken");
        }
    }
}
