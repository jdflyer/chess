package handler;

import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.NoUserException;
import io.javalin.http.Context;
import model.UserData;
import service.InvalidPasswordException;
import service.LoginService;
import service.RegisterService;

public class LoginHandler extends Handler {
    public LoginHandler(Context ctx) {
        super(ctx);
    }

    @Override
    public void handle() {
        try {
            var loginData = getRequestData(LoginData.class);
            setResponse(new LoginService().login(loginData.username(),loginData.password()));
        }catch (JsonSyntaxException e) {
            setBadRequestError();
        }catch (NoUserException | InvalidPasswordException e) {
            setUnauthorizedError();
        } catch (DataAccessException e) {
            setInternalServerError(e);
        }
    }
}
