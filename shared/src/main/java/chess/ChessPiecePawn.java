package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessPiecePawn extends ChessPiece {
    ChessPiecePawn(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        super(pieceColor, type);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return new ClassicChessPawnMoveValidator().getValidMoves(position,board);
    }

    @Override
    public int getStartingRow() {
        if (teamColor == ChessGame.TeamColor.BLACK) {
            return ChessBoard.height-1;
        }else {
            return 2;
        }
    }

    private int getPromotionRow() {
        if (teamColor == ChessGame.TeamColor.BLACK) {
            return 1;
        }else {
            return ChessBoard.height;
        }
    }
}
