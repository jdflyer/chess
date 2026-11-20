package client;

import chess.ChessGame;
import model.GameMetadata;
import ui.ChessUIMain;

public class ClientGame {
    ChessGame game;
    GameMetadata metadata;
    ChessUIMain ui;

    void initGame(GameMetadata metadata, boolean viewFromBlackTeam) {
        this.metadata = metadata;
        game = new ChessGame();
        ui = new ChessUIMain(viewFromBlackTeam);
        ui.drawBoard(game);
    }

    void gameMain(String input) {
        input = input.trim().toLowerCase();
        switch (input) {
            case "help":
                System.out.println("""
                        Chess Game Help:
                        \thelp: list available commands
                        \tleave: leave the current game""");
                break;
            case "leave":
                ClientMain.getSingleton().switchToLoggedInState();
                break;
            default:
                System.out.println("Unrecognized command. Type help for a list of commands.");
                break;
        }
//        ui.drawBoard(game);
    }
}
