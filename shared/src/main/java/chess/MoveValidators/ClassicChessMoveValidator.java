package chess.MoveValidators;

import chess.*;

import java.util.Collection;
import java.util.List;

public class ClassicChessMoveValidator implements ChessMoveValidator {
    @Override
    public boolean isValid(ChessPiece piece, ChessMove move, ChessBoard board) {
        return getValidMoves(move.getStartPosition(), board).contains(move);
    }

    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        return List.of();
    }

    protected int getForwardDirection(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return 1;
        }else {
            return -1;
        }
    }

}
