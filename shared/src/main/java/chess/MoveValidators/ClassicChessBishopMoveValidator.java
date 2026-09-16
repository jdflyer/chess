package chess.MoveValidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessBishopMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        // TODO: Refactor to use a single walkBoard method

        var piece = board.getPiece(position);
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        // Up Right
        for (int r = position.getRow()+1, c = position.getColumn()+1; r <= ChessBoard.height && c <= ChessBoard.width; r++,c++) {
            ChessPosition newPos = new ChessPosition(r,c);
            ChessPiece testPiece = board.getPiece(newPos);
            if (testPiece != null) {
                if (testPiece.getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPos));
                }
                break;
            }else {
                validMoves.add(new ChessMove(position, newPos));
            }
        }

        // Up Left
        for (int r = position.getRow()+1, c = position.getColumn()-1; r <= ChessBoard.height && c > 0; r++,c--) {
            ChessPosition newPos = new ChessPosition(r,c);
            ChessPiece testPiece = board.getPiece(newPos);
            if (testPiece != null) {
                if (testPiece.getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPos));
                }
                break;
            }else {
                validMoves.add(new ChessMove(position, newPos));
            }
        }

        // Down Left
        for (int r = position.getRow()-1, c = position.getColumn()-1; r > 0 && c > 0; r--,c--) {
            ChessPosition newPos = new ChessPosition(r,c);
            ChessPiece testPiece = board.getPiece(newPos);
            if (testPiece != null) {
                if (testPiece.getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPos));
                }
                break;
            }else {
                validMoves.add(new ChessMove(position, newPos));
            }
        }

        // Down Right
        for (int r = position.getRow()-1, c = position.getColumn()+1; r > 0 && c <= ChessBoard.width; r--,c++) {
            ChessPosition newPos = new ChessPosition(r,c);
            ChessPiece testPiece = board.getPiece(newPos);
            if (testPiece != null) {
                if (testPiece.getTeamColor() != piece.getTeamColor()) {
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
