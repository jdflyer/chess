package chess.movevalidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessQueenMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        var piece = board.getPiece(position);
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        walkBoard(position,position.getOffset(1,0),board,piece,validMoves);
        walkBoard(position,position.getOffset(-1,0),board,piece,validMoves);
        walkBoard(position,position.getOffset(0,-1),board,piece,validMoves);
        walkBoard(position,position.getOffset(0,1),board,piece,validMoves);
        walkBoard(position,position.getOffset(1,1),board,piece,validMoves);
        walkBoard(position,position.getOffset(1,-1),board,piece,validMoves);
        walkBoard(position,position.getOffset(-1,1),board,piece,validMoves);
        walkBoard(position,position.getOffset(-1,-1),board,piece,validMoves);

        return validMoves;
    }
}
