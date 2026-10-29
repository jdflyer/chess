package handler;

import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import dataaccess.InvalidAuthTokenException;
import dataaccess.NoUserException;
import io.javalin.http.Context;
import service.InvalidPasswordException;
import service.SessionService;

public class SessionHandler extends Handler {
    SessionService sessionService;

    public SessionHandler(Context ctx) {
        super(ctx);
        sessionService = new SessionService();
    }

    public void handleLogin() {
        try {
            var loginData = getRequestData(LoginData.class);
            setResponse(sessionService.login(loginData.username(),loginData.password()));
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
            sessionService.logout(getAuthToken());
            setResponseJson("{}");
        }catch (JsonSyntaxException | InvalidAuthHeaderException e) {
            setBadRequestError();
        } catch (InvalidAuthTokenException e) {
            setUnauthorizedError();
        }
    }
}
