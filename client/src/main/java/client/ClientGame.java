package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameMetadata;
import ui.ChessUIMain;

public class ClientGame {
    ChessGame game;
    ChessGame.TeamColor team;
    GameMetadata metadata;
    ChessUIMain ui;

    void initGame(GameMetadata metadata, ChessGame.TeamColor team) {
        this.metadata = metadata;
        this.team = team;

        ClientMain.getSingleton().facade.connectToGame(metadata.gameID());
        // await LOAD_GAME message, serialize board from response
        
        game = new ChessGame();
        ui = new ChessUIMain(team == ChessGame.TeamColor.BLACK);
        ui.drawBoard(game);
    }

    ChessPosition stringToChessPosition(String position) {
        if (position.length() != 2) {
            return null;
        }
        int col = position.charAt(0) - 'a' + 1;
        int row = position.charAt(1) - '0';
        if (col < 0 || col > 8 || row < 0 || row > 8) {
            return null;
        }
        return new ChessPosition(row,col);
    }

    private ChessPosition getPiecePos(String pieceString) {
        ChessPosition piecePos = stringToChessPosition(pieceString);
        if (piecePos == null) {
            System.out.println("Invalid position!");
            return null;
        }
        return piecePos;
    }

    private  ChessPiece getPiece(ChessPosition pos) {
        ChessPiece piece = game.getBoard().getPiece(pos);
        if (piece == null || piece.getTeamColor() != team) {
            System.out.println("Invalid piece! (Select a piece that is on your team)");
            return null;
        }
        return piece;
    }

    void handleMove() {
        System.out.print("Which piece would you like to move (eg. A5): ");
        String pieceString = ClientMain.getSingleton().scanner.nextLine().trim().toLowerCase();
        ChessPosition piecePos = getPiecePos(pieceString);
        if (piecePos == null) {
            return;
        }
        ChessPiece piece = getPiece(piecePos);
        if (piece == null) {
            return;
        }
        System.out.print("Where would you like to move the piece to: ");
        String moveString = ClientMain.getSingleton().scanner.nextLine().trim().toLowerCase();
        ChessPosition movePos = getPiecePos(moveString);
        if (movePos == null) {
            return;
        }

        ChessMove move = new ChessMove(piecePos, movePos, null);

        if (!game.isValidMove(move)) {
            System.out.println("Move is invalid!");
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            boolean canPromote = (piece.getTeamColor() == ChessGame.TeamColor.WHITE &&
                    movePos.getRow() == game.getBoard().getStartingPiecesRow(ChessGame.TeamColor.BLACK)-1) ||
                    (piece.getTeamColor() == ChessGame.TeamColor.BLACK && movePos.getRow() ==
                            game.getBoard().getStartingPiecesRow(ChessGame.TeamColor.WHITE)+1);

            if (canPromote) {
                System.out.print("Insert a piece to promote a pawn to: ");
                String promoteString = ClientMain.getSingleton().scanner.nextLine().trim().toLowerCase();
                ChessPiece.PieceType pieceToPromoteTo = null;
                switch (promoteString) {
                    case "r":
                    case "rook":
                        pieceToPromoteTo = ChessPiece.PieceType.ROOK;
                        break;
                    case "k":
                    case "knight":
                        pieceToPromoteTo = ChessPiece.PieceType.KNIGHT;
                        break;
                    case "b":
                    case "bishop":
                        pieceToPromoteTo = ChessPiece.PieceType.BISHOP;
                        break;
                    case "q":
                    case "queen":
                        pieceToPromoteTo = ChessPiece.PieceType.QUEEN;
                        break;
                    default:
                        System.out.println("Invalid piece!");
                        return;
                }
                move = new ChessMove(piecePos, movePos, pieceToPromoteTo);
            }
        }

        // Todo: Send move command
    }

    void handleResign() {
        System.out.print("Are you sure you would like to resign? (y/n): ");
        String answer = ClientMain.getSingleton().scanner.nextLine().trim().toLowerCase();
        if (answer.equals("y") || answer.equals("yes")) {
            // Handle resign (don't leave the game)
        }
    }

    void handleHighlight() {
        System.out.print("Which piece would you like to see valid moves for? (eg. A5): ");
        String pieceString = ClientMain.getSingleton().scanner.nextLine().trim().toLowerCase();
        ChessPosition piecePos = getPiecePos(pieceString);
        if (piecePos == null) {
            return;
        }
        ChessPiece piece = getPiece(piecePos);
        if (piece == null) {
            return;
        }

        ui.drawHighlightedMoves(game, piecePos);
    }

    void gameMain(String input) {
        input = input.trim().toLowerCase();
        switch (input) {
            case "help":
                System.out.println("""
                        Chess Game Help:
                        \thelp: list available commands
                        \tredraw: redraw the chess board
                        \tleave: leave the current game
                        \tmove: move a piece
                        \tresign: resign from the game
                        \thighlight: highlight all legal moves for a piece""");
                break;
            case "redraw":
                ui.drawBoard(game);
                break;
            case "leave":
                ClientMain.getSingleton().switchToLoggedInState();
                break;
            case "move":
                handleMove();
                break;
            case "resign":
                handleResign();
                break;
            case "highlight":
                handleHighlight();
                break;
            default:
                System.out.println("Unrecognized command. Type help for a list of commands.");
                break;
        }
//        ui.drawBoard(game);
    }
}
