package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 */
public class ChessGame {
    ChessBoard board;
    TeamColor currentTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    public TeamColor getOpposingTeamTurn(TeamColor team) {
        return team == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }
    public TeamColor getOpposingTeamTurn() {
        return getOpposingTeamTurn(getTeamTurn());
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = getBoard().getPiece(startPosition);
        if (getBoard().getPiece(startPosition) == null) {
            return null;
        }
        return piece.pieceMoves(getBoard(),startPosition);
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!board.isValidPosition(move.getStartPosition()) || !board.isValidPosition(move.getEndPosition())) {
            throw new InvalidMoveException("Move start/end position is out of bounds of the board!");
        }
        ChessPiece startPiece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), new ChessPiece(startPiece.teamColor,move.getPromotionPiece()));
        board.addPiece(move.getStartPosition(),null);
    }

    ChessPosition getKingPosition(ChessBoard board, TeamColor color) {
        Collection<ChessPosition> kingPositions = board.getAllPiecePositionsByPieceTypeAndColor(ChessPiece.PieceType.KING, color);
        assert(kingPositions.size() == 1);
        return kingPositions.iterator().next();
    }
    ChessPosition getKingPosition(TeamColor color) {
        return getKingPosition(getBoard(),color);
    }
    ChessPosition getKingPosition() {
        return getKingPosition(getTeamTurn());
    }

    public Collection<ChessPosition> getPiecePositionsThatCanAttackKing(ChessBoard board, TeamColor color) {
        Collection<ChessPosition> opposingTeamPiecePositions = board.getAllPiecePositionsByColor(getOpposingTeamTurn(color));

        ChessPosition kingPosition = getKingPosition(board,color);

        ArrayList<ChessPosition> piecePositionsThatCanAttackKing = new ArrayList<>();

        for (ChessPosition position : opposingTeamPiecePositions) {
            ChessPiece piece = board.getPiece(position);
            Collection<ChessMove> validMoves = piece.pieceMoves(board,position);
            for (ChessMove move : validMoves) {
                if (move.getEndPosition() == kingPosition) {
                    piecePositionsThatCanAttackKing.add(move.getStartPosition());
                }
            }
        }

        return piecePositionsThatCanAttackKing;
    }

    public Collection<ChessPosition> getPiecePositionsThatCanAttackKing(TeamColor color) {
        return getPiecePositionsThatCanAttackKing(getBoard(), color);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return !getPiecePositionsThatCanAttackKing(teamColor).isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessPiece> piecePositionsThatCanAttackKing = getPiecePositionsThatCanAttackKing(teamColor);
        if (piecePositionsThatCanAttackKing.isEmpty()) {
            return false;
        }
        ChessPosition kingPosition = getKingPosition();

        Collection<ChessPosition> myTeamPiecePositions = getBoard().getAllPiecePositionsByColor(getTeamTurn());
        for (ChessPosition position : myTeamPiecePositions) {
            ChessPiece piece = getBoard().getPiece(position);
            Collection<ChessMove> validMoves = piece.pieceMoves(getBoard(),position);
            for (ChessMove move : validMoves) {
                ChessBoard testBoard = new ChessBoard(getBoard());
                testBoard.addPiece(move.getEndPosition(),piece);
                testBoard.addPiece(move.getStartPosition(),piece);
                // Evaluate if king is in danger after the result of the new move
                Collection<ChessPosition> testPositionsThreateningKing = getPiecePositionsThatCanAttackKing(testBoard,teamColor);
                if (testPositionsThreateningKing.isEmpty()) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> teamPiecePositions = getBoard().getAllPiecePositionsByColor(teamColor);
        for (ChessPosition position : teamPiecePositions) {
            ChessPiece piece = getBoard().getPiece(position);
            if (!piece.pieceMoves(getBoard(),position).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

}
