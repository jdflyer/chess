package chess.MoveValidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessKnightMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        var piece = board.getPiece(position);
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        final ChessPosition[] testPairs = {new ChessPosition(2,1), new ChessPosition(2,-1), new ChessPosition(1,2), new ChessPosition(1,-2),
                new ChessPosition(-2,1), new ChessPosition(-2,-1), new ChessPosition(-1,2), new ChessPosition(-1,-2)};

        for (ChessPosition offset : testPairs) {
            ChessPosition testPos = position.getOffset(offset.getRow(),offset.getColumn());

            if (testPos.isValid() && canTake(piece,board.getPiece(testPos))) {
                validMoves.add(new ChessMove(position,testPos));
            }
        }

        return validMoves;
    }

}
