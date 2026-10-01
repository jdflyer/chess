package chess;

import chess.movevalidators.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 */
public class ChessPiece {
    ChessGame.TeamColor teamColor;
    PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    ChessMoveValidator getValidator() {
        return switch (type) {
            case PAWN -> new ClassicChessPawnMoveValidator();
            case ROOK -> new ClassicChessRookMoveValidator();
            case KNIGHT -> new ClassicChessKnightMoveValidator();
            case BISHOP -> new ClassicChessBishopMoveValidator();
            case QUEEN -> new ClassicChessQueenMoveValidator();
            case KING -> new ClassicChessKingMoveValidator();
            default -> null;
        };
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        ChessMoveValidator validator = getValidator();
        if (validator != null) {
            return validator.getValidMoves(board, myPosition, piece);
        }
        return new ArrayList<>();
    }

    public int getStartingRow() {
        if (getTeamColor() == ChessGame.TeamColor.BLACK) {
            return type == PieceType.PAWN ? ChessBoard.HEIGHT - 1 : ChessBoard.HEIGHT;
        }else {
            return type == PieceType.PAWN ? 2 : 1;
        }
    }

    public int getPromotionRow() {
        if (teamColor == ChessGame.TeamColor.BLACK) {
            return 1;
        }else {
            return ChessBoard.HEIGHT;
        }
    }
}
