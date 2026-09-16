package chess.MoveValidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessRookMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        var piece = board.getPiece(position);
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        // TODO: Refactor to use a single walkBoard method
        // Walk the board until we hit a wall or a piece, if the piece is on the other team, add it to our valid moves

        // Up
        for (int i = position.getRow()+1; i <= ChessBoard.height; i++) {
            ChessPosition newPos = new ChessPosition(i,position.getColumn());
            ChessPiece upPiece = board.getPiece(newPos);
            if (upPiece != null) {
                if (upPiece.getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPos));
                }
                break;
            }else {
                validMoves.add(new ChessMove(position, newPos));
            }
        }

        // Down
        for (int i = position.getRow()-1; i > 0; i--) {
            ChessPosition newPos = new ChessPosition(i,position.getColumn());
            ChessPiece downPiece = board.getPiece(newPos);
            if (downPiece != null) {
                if (downPiece.getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPos));
                }
                break;
            }else {
                validMoves.add(new ChessMove(position, newPos));
            }
        }

        // Left
        for (int i = position.getColumn()-1; i > 0; i--) {
            ChessPosition newPos = new ChessPosition(position.getRow(),i);
            ChessPiece leftPiece = board.getPiece(newPos);
            if (leftPiece != null) {
                if (leftPiece.getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPos));
                }
                break;
            }else {
                validMoves.add(new ChessMove(position, newPos));
            }
        }

        // Right
        for (int i = position.getColumn()+1; i <= ChessBoard.width; i++) {
            ChessPosition newPos = new ChessPosition(position.getRow(),i);
            ChessPiece rightPiece = board.getPiece(newPos);
            if (rightPiece != null) {
                if (rightPiece.getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPos));
                }
                break;
            }else {
                validMoves.add(new ChessMove(position, newPos));
            }
        }

        return validMoves;
    }
}
