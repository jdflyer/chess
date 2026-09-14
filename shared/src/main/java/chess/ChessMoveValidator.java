package chess;

import java.util.Collection;

public interface ChessMoveValidator {
    public boolean isValid(ChessPiece piece, ChessMove move, ChessBoard board);
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board);
}
