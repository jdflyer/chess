package serverfacade;


import client.ClientMain;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;

import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
    String address;
    int port;

    public Session session;

    int gameId; // The game ID to use in all requests

    public WebsocketCommunicator(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void connect() throws Exception {
        URI uri = new URI("ws://" + address + ":" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this,uri);
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println("Message");
            }
        });
    }

    public void send(String message) throws Exception {
        session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
