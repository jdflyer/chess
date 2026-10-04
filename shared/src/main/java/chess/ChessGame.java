package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 */
public class ChessGame {
    ChessBoard board;
    TeamColor currentTurn;

    // Helper class that keeps track of castle related pieces per-team
    private static class CastleStatus {
        boolean leftRookMoved;
        boolean kingMoved;
        boolean rightRookMoved;
        CastleStatus(boolean leftRookMoved, boolean kingMoved, boolean rightRookMoved) {
            this.leftRookMoved = leftRookMoved;
            this.kingMoved = kingMoved;
            this.rightRookMoved = rightRookMoved;
        }
        boolean canCastleLeft() {return !leftRookMoved && !kingMoved;}
        boolean canCastleRight() {return !kingMoved && !rightRookMoved;}
    }
    CastleStatus[] canCastleTeams;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        currentTurn = TeamColor.WHITE;

        canCastleTeams = new CastleStatus[TeamColor.values().length];
        for (int i = 0; i < canCastleTeams.length; i++) {
            canCastleTeams[i] = new CastleStatus(false, false, false);
        }
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

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=\n" + board +
                ", currentTurn=" + currentTurn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,
    };

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = getBoard().getPiece(startPosition);
        if (getBoard().getPiece(startPosition) == null) {
            return null;
        }
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(getBoard(),startPosition);

        // Handle castling first left then right
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !isInCheck(getBoard(), color)) {
            int startRow = getBoard().getStartingPiecesRow(color);
            if (canCastleTeams[color.ordinal()].canCastleLeft() &&
                    getBoard().getPiece(new ChessPosition(startRow,4)) == null &&
                    getBoard().getPiece(new ChessPosition(startRow,3)) == null &&
                    getBoard().getPiece(new ChessPosition(startRow,2)) == null) {
                // Make sure the king won't be in check through the castle
                ChessBoard testBoard = new ChessBoard(getBoard());
                testBoard.movePiece(new ChessMove(startPosition,new ChessPosition(startRow,4),null));
                if (!isInCheck(testBoard,color)) {
                    possibleMoves.add(new ChessMove(startPosition,new ChessPosition(startRow,3),null));
                }
            }
            if (canCastleTeams[color.ordinal()].canCastleRight() &&
                    getBoard().getPiece(new ChessPosition(startRow,6)) == null &&
                    getBoard().getPiece(new ChessPosition(startRow,7)) == null) {
                // Make sure the king won't be in check through the castle
                ChessBoard testBoard = new ChessBoard(getBoard());
                testBoard.movePiece(new ChessMove(startPosition,new ChessPosition(startRow,6),null));
                if (!isInCheck(testBoard,color)) {
                    possibleMoves.add(new ChessMove(startPosition, new ChessPosition(startRow, 7), null));
                }
            }
        }

        ArrayList<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            ChessBoard testBoard = new ChessBoard(getBoard());
            testBoard.movePiece(move);
            if (!isInCheck(testBoard,piece.getTeamColor())) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    public boolean isValidMove(ChessMove move) {
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        for (ChessMove testMove : validMoves) {
            if (testMove.equals(move)) {
                return true;
            }
        }
        return false;
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
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("The piece at the move's start position is null!");
        }
        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Attempted to move a piece who's turn it isn't!");
        }
        TeamColor color = piece.getTeamColor();
        if (!isValidMove(move)) {
            throw new InvalidMoveException("Attempted to force-make a move that is not possible!");
        }
        ChessPiece endPiece = board.getPiece(move.getEndPosition());
        if (endPiece != null && endPiece.getTeamColor() == piece.getTeamColor()) {
            throw new InvalidMoveException("Attempted to take a piece of the same color!");
        }
        ChessBoard testBoard = new ChessBoard(getBoard());
        testBoard.movePiece(move);
        if (isInCheck(testBoard,piece.getTeamColor())) {
            throw new InvalidMoveException("Attempted to make a move that would result in the king being in check!");
        }

        // Handle castling
        CastleStatus castleStatus = canCastleTeams[color.ordinal()];
        int startingRow = board.getStartingPiecesRow(color);
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (castleStatus.canCastleLeft() &&
                    move.equals(new ChessMove(new ChessPosition(startingRow, 5), new ChessPosition(startingRow, 3), null)) &&
                    getBoard().getPiece(new ChessPosition(startingRow, 2)) == null &&
                    getBoard().getPiece(new ChessPosition(startingRow, 3)) == null &&
                    getBoard().getPiece(new ChessPosition(startingRow, 4)) == null) {
                // Move left rook
                getBoard().movePiece(new ChessMove(new ChessPosition(startingRow, 1), new ChessPosition(startingRow, 4), null));
                castleStatus.leftRookMoved = true;
            } else if (castleStatus.canCastleRight() &&
                    move.equals(new ChessMove(new ChessPosition(startingRow, 5), new ChessPosition(startingRow, 7), null)) &&
                    getBoard().getPiece(new ChessPosition(startingRow, 6)) == null &&
                    getBoard().getPiece(new ChessPosition(startingRow, 7)) == null) {
                // Move right rook
                getBoard().movePiece(new ChessMove(new ChessPosition(startingRow, 8), new ChessPosition(startingRow, 6), null));
                castleStatus.rightRookMoved = true;
            }
        }

        // Update castle status
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (!castleStatus.leftRookMoved &&
                    move.getStartPosition().equals(new ChessPosition(startingRow, 1))) {
                castleStatus.leftRookMoved = true;
            }else if(!castleStatus.rightRookMoved &&
                    move.getStartPosition().equals(new ChessPosition(startingRow, 8))) {
                castleStatus.rightRookMoved = true;
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING && !castleStatus.kingMoved) {
            castleStatus.kingMoved = true;
        }

        board.movePiece(move);
        setTeamTurn(getOpposingTeamTurn());
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
                if (move.getEndPosition().equals(kingPosition)) {
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
    public boolean isInCheck(ChessBoard board, TeamColor teamColor) {
        return !getPiecePositionsThatCanAttackKing(board,teamColor).isEmpty();
    }
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(getBoard(),teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheckmate(getBoard(),teamColor);
    }
    public boolean isInCheckmate(ChessBoard board, TeamColor teamColor) {
        Collection<ChessPosition> piecePositionsThatCanAttackKing = getPiecePositionsThatCanAttackKing(teamColor);
        if (piecePositionsThatCanAttackKing.isEmpty()) {
            return false;
        }
        ChessPosition kingPosition = getKingPosition(board,teamColor);

        Collection<ChessPosition> myTeamPiecePositions = board.getAllPiecePositionsByColor(teamColor);
        for (ChessPosition position : myTeamPiecePositions) {
            ChessPiece piece = board.getPiece(position);
            Collection<ChessMove> validMoves = piece.pieceMoves(board,position);
            for (ChessMove move : validMoves) {
                ChessBoard testBoard = new ChessBoard(board);
                testBoard.movePiece(move);
                // Evaluate if king is in danger after the result of the new move
                Collection<ChessPosition> testPositionsThreateningKing = getPiecePositionsThatCanAttackKing(testBoard,teamColor);
                if (testPositionsThreateningKing.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return isInStalemate(getBoard(),teamColor);
    }
    public boolean isInStalemate(ChessBoard board, TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> teamPiecePositions = getBoard().getAllPiecePositionsByColor(teamColor);
        for (ChessPosition position : teamPiecePositions) {
            if (!validMoves(position).isEmpty()) {
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
