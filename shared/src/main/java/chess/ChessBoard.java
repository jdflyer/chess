package chess;

public class ChessBoard {
    public static final int width = 8;
    public static final int height = 8;

    ChessPiece[][] pieces;

    public ChessBoard() {
        resetBoard();
    }

    /**
     *
     * @return true if there is no piece in the given position
     */
    public boolean isEmpty(ChessPosition position) {
        return getPiece(position) == null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pieces[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return pieces[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        pieces = new ChessPiece[height][width];
    }
}
