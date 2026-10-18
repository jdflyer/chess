package handler;

import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.UserData;
import service.RegisterService;

public class RegisterHandler extends Handler {

    public RegisterHandler(Context ctx) {
        super(ctx);
    }

    public void handleRegister() {
        try {
            var registerData = getRequestData(UserData.class);
            setResponse(new RegisterService().register(registerData.username(),registerData.password(),registerData.email()));
        }catch (JsonSyntaxException e) {
            setBadRequestError();
        }catch (DataAccessException e) {
            setInternalServerError(e);
        }catch (UsernameAlreadyTakenException e) {
            setResponseError(403, "already taken");
        }
    }
}
