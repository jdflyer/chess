package serverfacade;

import websocket.messages.ServerMessage;

public interface ServerMessageObserverInterface {
    void notifyMessage(String message);
}
