package chess.movevalidators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ClassicChessMoveValidator implements ChessMoveValidator {
    @Override
    public boolean isValid(ChessPiece piece, ChessMove move, ChessBoard board) {
        return getValidMoves(board, move.getStartPosition(), board.getPiece(move.getStartPosition())).contains(move);
    }

    @Override
    public abstract Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece);

    public enum TakeCondition {
        NO_PIECE,
        SAME_TEAM,
        OTHER_TEAM,
        OUT_OF_BOUNDS
    };

    public TakeCondition testPosition(ChessBoard board, ChessPiece piece, ChessPosition pos) {
        if (!board.isValidPosition(pos)) {
            return TakeCondition.OUT_OF_BOUNDS;
        }
        ChessPiece other = board.getPiece(pos);
        if (other == null) {
            return TakeCondition.NO_PIECE;
        }
        return other.getTeamColor() == piece.getTeamColor() ? TakeCondition.SAME_TEAM : TakeCondition.OTHER_TEAM;
    }

    protected int getForwardDirection(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return 1;
        }else {
            return -1;
        }
    }

    protected void walkBoard(ChessBoard board, ChessPiece piece, ChessPosition startPosition,
                             ChessPosition endPosition,
                             ArrayList<ChessMove> outValidMoves) {
        int rowDir = Integer.signum(startPosition.getRow()-endPosition.getRow());
        int colDir = Integer.signum(startPosition.getColumn()-endPosition.getColumn());
        for (int r = startPosition.getRow()+rowDir, c = startPosition.getColumn()+colDir; r>0 &&
                r<= ChessBoard.WIDTH &&c>0&&c<= ChessBoard.WIDTH ; r += rowDir,c += colDir) {
            ChessPosition testPos = new ChessPosition(r,c);
            TakeCondition cond = testPosition(board,piece,testPos);
            if (cond == TakeCondition.SAME_TEAM || cond == TakeCondition.OUT_OF_BOUNDS) {
                return;
            }
            outValidMoves.add(new ChessMove(startPosition,testPos,null));
            if (cond == TakeCondition.OTHER_TEAM) {
                return;
            }
        }
    }

    protected void checkValidOffsets(ChessBoard board, ChessPosition startPos, ChessPiece piece,
                                     ChessPosition[] offsets, ArrayList<ChessMove> outMoves) {
        for (ChessPosition offset : offsets) {
            ChessPosition testPos = startPos.getOffset(offset.getRow(),offset.getColumn());
            TakeCondition cond = testPosition(board,piece,testPos);
            if (cond == TakeCondition.SAME_TEAM || cond == TakeCondition.OUT_OF_BOUNDS) {
                continue;
            }
            outMoves.add(new ChessMove(startPos,testPos,null));
        }
    }
}
