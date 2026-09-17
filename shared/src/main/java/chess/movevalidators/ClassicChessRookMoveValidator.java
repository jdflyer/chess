package chess.movevalidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessRookMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        var piece = board.getPiece(position);
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        // Walk the board until we hit a wall or a piece, if the piece is on the other team, add it to our valid moves
        walkBoard(position,position.getOffset(1,0),board,piece,validMoves);
        walkBoard(position,position.getOffset(-1,0),board,piece,validMoves);
        walkBoard(position,position.getOffset(0,-1),board,piece,validMoves);
        walkBoard(position,position.getOffset(0,1),board,piece,validMoves);

        return validMoves;
    }
}
