package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.InvalidAuthTokenException;
import io.javalin.http.Context;
import service.SessionService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;

public abstract class Handler {
    Context ctx;

    Handler(Context ctx) {
        this.ctx = ctx;
    }

    protected <T> T getRequestData(Class<T> classOfT) throws JsonSyntaxException {
        T record = new Gson().fromJson(ctx.body(), classOfT);
        if (classOfT.isRecord()) {
            for (RecordComponent component : classOfT.getRecordComponents()) {
                try {
                    if (component.getAccessor().invoke(record) == null) {
                        throw new JsonSyntaxException(component.getName()+" is expected but not included!");
                    }
                }catch (IllegalAccessException | InvocationTargetException e) {
                    continue;
                }
            }
        }
        return record;
    }

    protected void setResponseJson(int statusCode, String json) {
        ctx.status(statusCode);
        ctx.contentType("application/json");
        ctx.result(json);
    }

    protected void setResponseJson(String message) {
        setResponseJson(200,message);
    }

    protected void setResponse(int statusCode, Object recordToSerialize) {
        setResponseJson(statusCode,new Gson().toJson(recordToSerialize));
    }

    protected void setResponse(Object recordToSerialize) {
        setResponseJson(200,new Gson().toJson(recordToSerialize));
    }

    protected void setResponseError( int statusCode, String message) {
        setResponseJson(statusCode, "{ \"message\": \"Error: "+message+"\" }");
    }

    protected void setBadRequestError() {
        setResponseError(400,"bad request");
    }

    protected void setUnauthorizedError() {
        setResponseError(401,"unauthorized");
    }

    protected void setInternalServerError() {
        setResponseError(500,"internal server error");
    }

    protected void setInternalServerError(Exception e) {
        setResponseError(500,"internal server error ("+e.getMessage()+")");
    }

    protected String getAuthToken() throws InvalidAuthHeaderException {
        return ctx.header("authorization");
    }

    protected boolean isValidAuthToken() {
        try {
            return new SessionService().isValidAuth(getAuthToken());
        } catch (InvalidAuthHeaderException e) {
            return false;
        }
    }
}
