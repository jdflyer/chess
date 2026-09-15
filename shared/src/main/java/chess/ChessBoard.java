package chess;

import java.util.Arrays;
import java.util.Objects;

public class ChessBoard {
    public static final int width = 8;
    public static final int height = 8;

    ChessPiece[][] pieces;

    public ChessBoard() {
        pieces = new ChessPiece[height][width];
    }

    /**
     *
     * @return true if there is no piece in the given position
     */
    public boolean isEmpty(ChessPosition position) {
        return getPiece(position) == null;
    }

    /**
     *
     * @param position  the piece to check if we can take
     * @param team      the team to check against
     * @return true if the piece at the given position is on a different team than the supplied team
     */
    public boolean canTake(ChessPosition position, ChessGame.TeamColor team) {
        return getPiece(position).getTeamColor() != team;
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
        // Initialize the pieces to be a null board
        pieces = new ChessPiece[height][width];
        final ChessPiece.PieceType[] backRow = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT,
                ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};
        assert (backRow.length == width);

        // Put white pieces in the right position
        for (int i = 1; i <= width; i++) {
            addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, backRow[i - 1]));
        }
        for (int i = 1; i <= width; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        // Put black pieces in the right position
        for (int i = 1; i <= width; i++) {
            addPiece(new ChessPosition(height, i), new ChessPiece(ChessGame.TeamColor.BLACK, backRow[i - 1]));
        }
        for (int i = 1; i <= width; i++) {
            addPiece(new ChessPosition(height - 1, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(pieces, that.pieces);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(pieces);
    }
}
