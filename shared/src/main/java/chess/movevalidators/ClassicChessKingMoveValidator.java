package chess.movevalidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessKingMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        // Doesn't consider if the piece will be in check when it moves there

        var piece = board.getPiece(position);
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        final ChessPosition[] testPairs = {
                new ChessPosition(1,1),
                new ChessPosition(1,-1),
                new ChessPosition(1,0),
                new ChessPosition(0,1),
                new ChessPosition(0,-1),
                new ChessPosition(-1,-1),
                new ChessPosition(-1,0),
                new ChessPosition(-1,1)
        };

        checkValidOffsets(testPairs,position,piece,board,validMoves);

        return validMoves;
    }

}
