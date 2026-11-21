package websocket.commands;

import chess.ChessMove;

import java.util.Objects;

public class UserMoveCommand extends UserGameCommand {
    ChessMove move;

    public UserMoveCommand(String authToken, Integer gameId, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameId);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    public void setMove(ChessMove move) {
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UserMoveCommand that = (UserMoveCommand) o;
        return Objects.equals(move, that.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), move);
    }
}
