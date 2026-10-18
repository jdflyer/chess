package chess;

import java.util.Collection;

public interface ChessMoveValidator {
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece);
}
