package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessPawnMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        var piece = board.getPiece(position);
        int forwardDir = getForwardDirection(piece);
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        var above = position.getOffset(forwardDir, 0);
        if (above.isValid() && board.isEmpty(above)) {
            validMoves.add(new ChessMove(position, above));
            above = above.getOffset(forwardDir,0);
            if (above.isValid() && piece.getStartingRow() == position.getRow() && board.isEmpty(above)) {
                validMoves.add(new ChessMove(position, above));
            }
        }
        var left = position.getOffset(forwardDir,-1);
        if (left.isValid() && !board.isEmpty(left) && board.canTake(left, piece.getTeamColor())) {
            validMoves.add(new ChessMove(position, left));
        }
        var right = position.getOffset(forwardDir,1);
        if (right.isValid() && !board.isEmpty(right) && board.canTake(right, piece.getTeamColor())) {
            validMoves.add(new ChessMove(position, right));
        }

        return validMoves;
    }
}
