package handler;

import com.google.gson.Gson;
import io.javalin.http.Context;

public abstract class Handler {
    Context ctx;

    Handler(Context ctx) {
        this.ctx = ctx;
    }

    protected void setResponseJson(int statusCode, String message) {
        ctx.status(statusCode);
        ctx.contentType("application/json");
        ctx.result("{ \"message\": \"Error: "+message+"\" }");
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

    abstract void handle();
}
