package chess.movevalidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessKingMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        // Doesn't consider if the piece will be in check when it moves there

        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPosition[] testOffsets = {
                new ChessPosition(1,0),
                new ChessPosition(1,1),
                new ChessPosition(1,-1),
                new ChessPosition(0,1),
                new ChessPosition(0,-1),
                new ChessPosition(-1,-1),
                new ChessPosition(-1,0),
                new ChessPosition(-1,1),
        };

        checkValidOffsets(board,position,piece,testOffsets,moves);

        return moves;
    }

}
