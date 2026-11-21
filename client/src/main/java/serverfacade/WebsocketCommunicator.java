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

    ServerMessageObserverInterface observer;

    public WebsocketCommunicator(String address, int port, ServerMessageObserverInterface observer) {
        this.address = address;
        this.port = port;
        this.observer = observer;
    }

    public void connect() throws Exception {
        URI uri = new URI("ws://" + address + ":" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this,uri);
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
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
