package chess.MoveValidators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ClassicChessMoveValidator implements ChessMoveValidator {
    @Override
    public boolean isValid(ChessPiece piece, ChessMove move, ChessBoard board) {
        return getValidMoves(move.getStartPosition(), board).contains(move);
    }

    @Override
    public abstract Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board);

    protected int getForwardDirection(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return 1;
        }else {
            return -1;
        }
    }

    protected void walkBoard(ChessPosition startPosition, ChessPosition endPosition, ChessBoard board ,ChessPiece piece, Collection<ChessMove> refValidMoves) {
        int rowDirection = Integer.signum(endPosition.getRow()-startPosition.getRow());
        int colDirection = Integer.signum(endPosition.getColumn()-startPosition.getColumn());
        for (int r = startPosition.getRow()+rowDirection, c = startPosition.getColumn()+colDirection; r > 0 && r <= ChessBoard.height && c > 0 && c <= ChessBoard.width; r += rowDirection, c += colDirection) {
            ChessPosition newPos = new ChessPosition(r,c);
            ChessPiece testPiece = board.getPiece(newPos);
            if (testPiece != null) {
                if (testPiece.getTeamColor() != piece.getTeamColor()) {
                    refValidMoves.add(new ChessMove(startPosition, newPos));
                }
                break;
            }else {
                refValidMoves.add(new ChessMove(startPosition, newPos));
            }
        }
    }

    protected boolean canTake(ChessPiece myPiece, ChessPiece other) {
        if (other == null) {
            return true;
        }
        return myPiece.getTeamColor() != other.getTeamColor();
    }

}
