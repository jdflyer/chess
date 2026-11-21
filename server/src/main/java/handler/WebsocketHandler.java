package handler;

import com.google.gson.Gson;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;

public class WebsocketHandler {
    WsConfig wsConfig;

    public WebsocketHandler(WsConfig wsConfig) {
        this.wsConfig = wsConfig;
    }

    String createErrorMessage(String errorString) {
        ServerErrorMessage message = new ServerErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: "+errorString);
        return new Gson().toJson(message);
    }

    void sendError(WsMessageContext ctx, String errorString) {
        ctx.send(createErrorMessage(createErrorMessage(errorString)));
    }

    void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = Handler.deserialize(UserGameCommand.class, ctx.message());

        }catch (Exception e) {
            sendError(ctx, "Error parsing message on server!");
        }
    }

    public void handleWebsocketconfig() {
        wsConfig.onConnect(ctx -> {
            ctx.enableAutomaticPings();
            System.out.println("Websocket connected from "+ctx.session.getRemote().toString());
        });
        wsConfig.onMessage(ctx -> {
            handleMessage(ctx);
        });
        wsConfig.onClose(ctx -> {
            System.out.println("Websocket closed from "+ctx.session.getRemote().toString());
        });
    }
}
