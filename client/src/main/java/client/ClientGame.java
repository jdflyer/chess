package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.JsonSyntaxException;
import model.GameMetadata;
import serverfacade.BadRequestException;
import serverfacade.ServerFacade;
import serverfacade.ServerMessageObserverInterface;
import ui.ChessUIMain;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;

import javax.sound.midi.SysexMessage;

public class ClientGame implements ServerMessageObserverInterface {
    final Object gameLock = new Object();
    ChessGame game;

    ChessGame.TeamColor team;

    int gameId;
    String blackUsername;
    String whiteUsername;

    ChessUIMain ui;

    boolean waitForNewGameState(int timeoutMs) {
        try {
            synchronized (gameLock) {
                while (game == null) {
                    gameLock.wait(timeoutMs);
                }
            }
            return true;
        } catch(InterruptedException e) {
            return false;
        }
    }

    void setAndShowGameState(ChessGame game) {
        synchronized (gameLock) {
            this.game = game;
            ui.drawBoard(game);
            gameLock.notifyAll();
        }
    }

    void initGame(GameMetadata metadata, ChessGame.TeamColor team) {
        this.gameId = metadata.gameID();
        this.whiteUsername = metadata.whiteUsername();
        this.blackUsername = metadata.blackUsername();
        this.team = team;
        ui = new ChessUIMain(team == ChessGame.TeamColor.BLACK);

        ClientMain.getSingleton().facade.connectToGame(metadata.gameID(), this);

        waitForNewGameState(5000);
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
        if (piece == null) {
            System.out.println("Invalid piece! (Select a piece that is on your team)");
            return null;
        }
        return piece;
    }

    void handleMove() {
        if (team == null) {
            System.out.println("Can't move! You are an observer!");
            return;
        }
        if (game.getTeamTurn() != team) {
            System.out.println("Can't move! It isn't your turn yet!");
            return;
        }
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

        try {
            ClientMain.getSingleton().facade.makeMove(move);
        } catch (BadRequestException e) {
            System.out.println("The move request failed!");
        }
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

    void notifyNotification(NotificationMessage notification) {
        String[] fullMessage = notification.getMessage().split("\n");
        if (fullMessage.length== 0) {
            return;
        }
        System.out.print("Notification: ");
        switch (fullMessage[0]) {
            case "join":
                if (fullMessage.length != 3) {
                    break;
                }
                String teamString = "as an observer";
                if (fullMessage[2].equals("black")) {
                    teamString = "the black team";
                    blackUsername = fullMessage[1];
                }else if (fullMessage[2].equals("white")) {
                    teamString = "the white team";
                    whiteUsername = fullMessage[1];
                }
                System.out.println(fullMessage[1]+" has joined "+teamString);
                break;
            case "move":
                if (fullMessage.length != 3) {
                    break;
                }
                // We should already have the updated board here: Read the new end position to determine
                // which team and player made the move
                ChessPosition endPos = stringToChessPosition(fullMessage[2]);
                if (endPos == null) {
                    break;
                }
                ChessPiece pieceMoved = game.getBoard().getPiece(endPos);
                String otherTeam = pieceMoved.getTeamColor() == ChessGame.TeamColor.BLACK ? "black" : "white";
                String playerMoved = pieceMoved.getTeamColor() == ChessGame.TeamColor.BLACK ? blackUsername : whiteUsername;
                System.out.println(playerMoved+" their "+otherTeam+" "+pieceMoved.getPieceType()+" from "+fullMessage[1]+
                        " to "+fullMessage[2]);
                break;
            case "resign":
                if (fullMessage.length != 2) {
                    break;
                }
                System.out.println(fullMessage[1]+" has resigned from the game.");
                break;
            case "leave":
                if (fullMessage.length != 2) {
                    break;
                }
                System.out.println(fullMessage[1]+" has left the game.");
                break;
            case "check":
                if (fullMessage.length != 2) {
                    break;
                }
                System.out.println(fullMessage[1]+" is in check.");
                break;
            case "checkmate":
                if (fullMessage.length != 2) {
                    break;
                }
                System.out.println(fullMessage[1]+" is in checkmate. The game has ended.");
                break;
            case "stalemate":
                if (fullMessage.length != 2) {
                    break;
                }
                System.out.println(fullMessage[1]+" cannot make any more moves. The game has ended in a stalemate.");
                break;
        }
    }

    void notifyError(ServerErrorMessage error) {
        System.out.println("Error From Server: "+error);
    }

    void notifyLoadGame(LoadGameMessage message) {
        setAndShowGameState(message.getGame());
    }

    @Override
    public void notifyMessage(String message) {
        try {
            ServerMessage serverMessage = ServerFacade.deserializeRecord(message, ServerMessage.class);
            // Clear current scanner input
            ClientMain.getSingleton().scanner.nextLine();
            System.out.print('\n');
            switch (serverMessage.getServerMessageType()) {
                case NOTIFICATION:
                    notifyNotification(ServerFacade.deserializeRecord(message, NotificationMessage.class));
                    break;
                case ERROR:
                    notifyError(ServerFacade.deserializeRecord(message, ServerErrorMessage.class));
                    break;
                case LOAD_GAME:
                    notifyLoadGame(ServerFacade.deserializeRecord(message, LoadGameMessage.class));
                    break;
            }

            System.out.print("[GAME]> ");
        }catch (JsonSyntaxException e) {
            // Invalid message was sent, don't handle it
            return;
        }
    }
}
