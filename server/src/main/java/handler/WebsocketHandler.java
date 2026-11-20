package handler;

import io.javalin.websocket.WsConfig;

public class WebsocketHandler {
    WsConfig wsConfig;

    public WebsocketHandler(WsConfig wsConfig) {
        this.wsConfig = wsConfig;
    }

    public void handleWebsocketconfig() {
        wsConfig.onConnect(ctx -> {
            ctx.enableAutomaticPings();
            System.out.println("Websocket connected from "+ctx.session.getRemote().toString());
        });
        wsConfig.onMessage(ctx -> {
            ctx.send("Pong: "+ctx.message());
        });
        wsConfig.onClose(ctx -> {
            System.out.println("Websocket closed from "+ctx.session.getRemote().toString());
        });
    }
}
