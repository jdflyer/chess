package chess.movevalidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessQueenMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        walkBoard(board,piece,position,position.getOffset(1,0),moves);
        walkBoard(board,piece,position,position.getOffset(-1,0),moves);
        walkBoard(board,piece,position,position.getOffset(0,1),moves);
        walkBoard(board,piece,position,position.getOffset(0,-1),moves);
        walkBoard(board,piece,position,position.getOffset(1,1),moves);
        walkBoard(board,piece,position,position.getOffset(-1,-1),moves);
        walkBoard(board,piece,position,position.getOffset(-1,1),moves);
        walkBoard(board,piece,position,position.getOffset(1,-1),moves);

        return moves;
    }
}
