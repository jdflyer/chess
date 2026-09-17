package chess.movevalidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessPawnMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        var piece = board.getPiece(position);
        int forwardDir = getForwardDirection(piece);
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        ArrayList<ChessPiece.PieceType> pawnPromotions = new ArrayList<>();

        if (position.getRow() + getForwardDirection(piece) ==  piece.getPromotionRow()) {
            pawnPromotions.add(ChessPiece.PieceType.ROOK);
            pawnPromotions.add(ChessPiece.PieceType.KNIGHT);
            pawnPromotions.add(ChessPiece.PieceType.BISHOP);
            pawnPromotions.add(ChessPiece.PieceType.QUEEN);
        }else{
            pawnPromotions.add(null);
        }

        for (ChessPiece.PieceType promotion : pawnPromotions) {
            var above = position.getOffset(forwardDir, 0);
            if (above.isValid() && board.isEmpty(above)) {
                validMoves.add(new ChessMove(position, above, promotion));
                above = above.getOffset(forwardDir,0);
                if (above.isValid() && piece.getStartingRow() == position.getRow() && board.isEmpty(above)) {
                    validMoves.add(new ChessMove(position, above));
                }
            }
            var left = position.getOffset(forwardDir,-1);
            if (left.isValid() && !board.isEmpty(left) && board.canTake(left, piece.getTeamColor())) {
                validMoves.add(new ChessMove(position, left, promotion));
            }
            var right = position.getOffset(forwardDir,1);
            if (right.isValid() && !board.isEmpty(right) && board.canTake(right, piece.getTeamColor())) {
                validMoves.add(new ChessMove(position, right, promotion));
            }
        }

        return validMoves;
    }
}
