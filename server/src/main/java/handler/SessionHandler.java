package handler;

import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.InvalidAuthTokenException;
import dataaccess.NoUserException;
import io.javalin.http.Context;
import service.InvalidPasswordException;
import service.SessionService;

public class SessionHandler extends Handler {
    public SessionHandler(Context ctx) {
        super(ctx);
    }

    public void handleLogin() {
        try {
            var loginData = getRequestData(LoginData.class);
            setResponse(new SessionService().login(loginData.username(),loginData.password()));
        }catch (JsonSyntaxException e) {
            setBadRequestError();
        }catch (NoUserException | InvalidPasswordException e) {
            setUnauthorizedError();
        } catch (DataAccessException e) {
            setInternalServerError(e);
        }
    }

    public void handleLogout() {

        try {
            new SessionService().logout(getAuthToken());
            setResponseJson("{}");
        }catch (JsonSyntaxException | InvalidAuthHeaderException e) {
            setBadRequestError();
        } catch (InvalidAuthTokenException e) {
            setUnauthorizedError();
        }
    }
}
