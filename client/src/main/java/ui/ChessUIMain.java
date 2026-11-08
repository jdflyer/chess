package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ClientMain;
import ui.EscapeSequences;

public class ChessUIMain {
    boolean viewFromBlackTeam;
    ChessGame currentGame;

    public ChessUIMain(boolean viewFromBlackTeam) {
        this.viewFromBlackTeam = viewFromBlackTeam;

        // Temporary
        currentGame = new ChessGame();
    }

    private String pieceToString(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch(piece.getPieceType()) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
        }else {
            return switch(piece.getPieceType()) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        }
    }

    private final String BG_MAIN = EscapeSequences.SET_BG_COLOR_DARK_GREEN;
    private final String BG_BLACK = EscapeSequences.SET_BG_COLOR_DARK_GREY;
    private final String BG_WHITE = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

    private final String TEXT_WHITE = EscapeSequences.SET_TEXT_COLOR_WHITE;
    private final String TEXT_BLACK = EscapeSequences.SET_TEXT_COLOR_BLACK;
    private final String TEXT_EMPTY = "   ";

    void printEmptyRow() {
        System.out.print(BG_MAIN);
        for (int i = 0; i<10;i++) {
            System.out.print(EscapeSequences.EMPTY);
        }
        System.out.print(EscapeSequences.RESET_TEXT_COLOR+EscapeSequences.RESET_BG_COLOR+"\n");
    }

    void drawBoard() {
        printEmptyRow();
        for (int r = 0; r < 8; r++) {
            System.out.print(BG_MAIN+TEXT_EMPTY);
            for (int c = 0; c < 8; c++) {
                String bgColor = (r+c) % 2 == 0 ? BG_WHITE : BG_BLACK;
                ChessPosition pos = viewFromBlackTeam ? new ChessPosition(r+1,8-c) : new ChessPosition(8-r,c+1) ;
                ChessPiece piece = currentGame.getBoard().getPiece(pos);
                System.out.print(bgColor);
                if (piece == null) {
                    System.out.print(TEXT_EMPTY);
                    continue;
                }
                String textColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? TEXT_WHITE : TEXT_BLACK;
                System.out.print(textColor);
                System.out.print(pieceToString(piece));
            }
            System.out.print(BG_MAIN+TEXT_EMPTY+EscapeSequences.RESET_BG_COLOR+"\n");
        }
        printEmptyRow();
    }

    public void run() {
        boolean exitUI = false;
        while (!exitUI) {
            drawBoard();
            System.out.print("> ");
            String c = ClientMain.getSingleton().scanner.nextLine().trim().toLowerCase();
            if (c.equals("quit")) {
                exitUI = true;
            }
        }
    }
}
