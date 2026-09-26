package chess.movevalidators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class ClassicChessPawnMoveValidator extends ClassicChessMoveValidator {
    public static int getPawnForwardDir(ChessPiece piece) {
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
    }

    public static int getPawnStartRow(ChessPiece piece, ChessBoard board) {
        return piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 2 : ChessBoard.HEIGHT - 1;
    }


    @Override
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessPiece piece) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        boolean canPromote = (piece.getTeamColor() == ChessGame.TeamColor.WHITE &&
                position.getRow() == ChessBoard.HEIGHT-1) ||
                (piece.getTeamColor() == ChessGame.TeamColor.BLACK && position.getRow() == 2);

        ArrayList<ChessPiece.PieceType> typesToTest = new ArrayList<>();
        if (canPromote) {
            typesToTest.add(ChessPiece.PieceType.ROOK);
            typesToTest.add(ChessPiece.PieceType.BISHOP);
            typesToTest.add(ChessPiece.PieceType.KNIGHT);
            typesToTest.add(ChessPiece.PieceType.QUEEN);
        }else {
            typesToTest.add(null);
        }

        for (ChessPiece.PieceType promotionType : typesToTest) {
            ChessPosition testPos = position.getOffset(getPawnForwardDir(piece),0);
            if (getPawnStartRow(piece,board) == position.getRow() && testPosition(board,piece,testPos) == TakeCondition.NO_PIECE) {
                testPos = position.getOffset(getPawnForwardDir(piece)*2,0);
                if (board.isValidPosition(testPos) && testPosition(board,piece,testPos) == TakeCondition.NO_PIECE) {
                    moves.add(new ChessMove(position,testPos,promotionType));
                }
            }

            testPos = position.getOffset(getPawnForwardDir(piece),0);
            if (testPosition(board,piece,testPos) == TakeCondition.NO_PIECE) {
                moves.add(new ChessMove(position,testPos,promotionType));
            }

            testPos = position.getOffset(getPawnForwardDir(piece),1);
            if (testPosition(board,piece,testPos) == TakeCondition.OTHER_TEAM) {
                moves.add(new ChessMove(position,testPos,promotionType));
            }

            testPos = position.getOffset(getPawnForwardDir(piece),-1);
            if (testPosition(board,piece,testPos) == TakeCondition.OTHER_TEAM) {
                moves.add(new ChessMove(position,testPos,promotionType));
            }
        }

        return moves;
    }
}
