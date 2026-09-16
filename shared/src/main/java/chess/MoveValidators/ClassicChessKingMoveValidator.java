package chess.MoveValidators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessKingMoveValidator extends ClassicChessMoveValidator {
    @Override
    public Collection<ChessMove> getValidMoves(ChessPosition position, ChessBoard board) {
        // Doesn't consider if the piece will be in check when it moves there

        var piece = board.getPiece(position);
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        final ChessPosition[] testPairs = {new ChessPosition(1,1), new ChessPosition(1,-1), new ChessPosition(1,0), new ChessPosition(0,1),
                new ChessPosition(0,-1), new ChessPosition(-1,-1), new ChessPosition(-1,0), new ChessPosition(-1,1)};

        for (ChessPosition offset : testPairs) {
            ChessPosition testPos = position.getOffset(offset.getRow(),offset.getColumn());

            if (testPos.isValid() && canTake(piece,board.getPiece(testPos))) {
                validMoves.add(new ChessMove(position,testPos));
            }
        }

        return validMoves;
    }

    protected boolean canTake(ChessPiece myPiece, ChessPiece other) {
        if (other == null) {
            return true;
        }
        return myPiece.getTeamColor() != other.getTeamColor();
    }

}
