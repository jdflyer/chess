package serverfacade;

import websocket.messages.NotificationMessage;

import java.util.ArrayDeque;
import java.util.Queue;

public class ServerMessageObserver implements ServerMessageObserverInterface{
    Queue<String> notificationsToParse;

    ServerMessageObserver() {
        notificationsToParse = new ArrayDeque<>();
    }

    public void notifyMessage(NotificationMessage message) {
        notificationsToParse.add(message.getMessage());
    }

    // Returns null when queue is empty
    public String observeNext() {
        return notificationsToParse.poll();
    }
}
