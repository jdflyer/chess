package serverfacade;

import websocket.messages.NotificationMessage;

public interface ServerMessageObserverInterface {
    void notifyMessage(NotificationMessage message);
}
