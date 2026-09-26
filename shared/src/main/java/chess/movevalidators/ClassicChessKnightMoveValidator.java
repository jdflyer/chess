package chess.movevalidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessKnightMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPosition[] testOffsets = {
                new ChessPosition(1,2),
                new ChessPosition(1,-2),
                new ChessPosition(2,1),
                new ChessPosition(2,-1),
                new ChessPosition(-1,2),
                new ChessPosition(-1,-2),
                new ChessPosition(-2,1),
                new ChessPosition(-2,-1),
        };

        checkValidOffsets(board,position,piece,testOffsets,moves);

        return moves;
    }

}
