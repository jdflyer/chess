package websocket.messages;

import java.util.Objects;

public class ServerErrorMessage extends ServerMessage {
    String errorMessage;

    public ServerErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ServerErrorMessage that = (ServerErrorMessage) o;
        return Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), errorMessage);
    }
}
