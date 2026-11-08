package client;

import model.GameCreateParams;
import model.GameJoinParams;
import model.GameList;
import model.GameMetadata;
import serverfacade.BadRequestException;
import serverfacade.InvalidSessionException;
import ui.ChessUIMain;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientPostLogin {
    // Stores the real Database game IDs. Note: the exposed IDs start at index 1
    GameList gameList;

    void handleLogout() {
        ClientMain.getSingleton().state = ClientMain.State.LOGGED_OUT;
        ClientMain.getSingleton().authData = null;
    }

    void handleLogout_InvalidSession() {
        System.out.println("Access Denied! Now logging out...");
        handleLogout();
    }

    void listAllGames() {
        try {
            gameList = ClientMain.getSingleton().facade.listAllGames();
            if (gameList.games().length == 0) {
                System.out.println("There are no currently running games.");
                return;
            }
            int currentGame = 1;
            for (var game : gameList.games()) {
                int playerCount = game.blackUsername() == null ? 0 : 1;
                playerCount += game.whiteUsername() == null ? 0 : 1;
                System.out.println("["+currentGame+"] "+game.gameName()+" ("+playerCount+"/2 Players)");
                currentGame += 1;
            }
        }catch (InvalidSessionException e) {
            handleLogout_InvalidSession();
        }catch (BadRequestException e) {
            ClientMain.printBadRequest();
        }
    }

    void createGame() {
        try {
            System.out.print("Game Name: ");
            String gameName = ClientMain.getSingleton().scanner.nextLine().trim();
            int dbGameId = ClientMain.getSingleton().facade.createGame(new GameCreateParams(gameName)).gameID();
            System.out.println("Successfully created game \""+gameName+"\"! Use the list command to see its id.");
        }catch (InvalidSessionException e) {
            handleLogout_InvalidSession();
        }catch (BadRequestException e) {
            ClientMain.printBadRequest();
        }
    }

    // Calls "list" and then prompts for a game ID
    GameMetadata getGameFromUser() throws InvalidGameIdException {
        System.out.println("Current games:");
        listAllGames();
        if (gameList == null) {
            throw new InvalidGameIdException("Couldn't get game list from server!");
        }
        System.out.print("Which game would you like to join?\nEnter Game ID: ");
        String id = ClientMain.getSingleton().scanner.nextLine().trim();
        int gameId;
        try {
            gameId = Integer.parseInt(id) - 1;
        }catch (NumberFormatException e) {
            throw new InvalidGameIdException("Invalid game ID!");
        }

        if (gameId < 0 || gameId >= gameList.games().length) {
            throw new InvalidGameIdException("Invalid game ID!");
        }
        return gameList.games()[gameId];
    }

    void joinGame() {
        try {
            GameMetadata metadata = getGameFromUser();
            System.out.print("Which team would you like to join (White/Black): ");
            String teamToJoin = ClientMain.getSingleton().scanner.nextLine().trim().toLowerCase();
            String teamJoinParam = null;
            switch (teamToJoin) {
                case "w":
                case "white":
                    if (metadata.whiteUsername() != null) {
                        System.out.println("Could not join game! Player "+metadata.whiteUsername()+" is already on the White team!");
                        return;
                    }
                    teamJoinParam = "WHITE";
                    break;
                case "b":
                case "black":
                    if (metadata.blackUsername() != null) {
                        System.out.println("Could not join game! Player "+metadata.blackUsername()+" is already on the Black team!");
                        return;
                    }
                    teamJoinParam = "BLACK";
                    break;
                default:
                    System.out.println("Invalid Team!");
                    return;
            }
            ClientMain.getSingleton().facade.joinGame(new GameJoinParams(teamJoinParam,metadata.gameID()));
            new ChessUIMain(teamJoinParam.equals("BLACK")).run();
        }catch (InvalidGameIdException e) {
            System.out.println(e.getMessage());
        }
    }

    void observeGame() {
        // Currently doesn't communicate to server, just displays the game UI
        try {
            GameMetadata metadata = getGameFromUser();
            new ChessUIMain(false).run();
        }catch (InvalidGameIdException e) {
            System.out.println(e.getMessage());
        }
    }

    void postLoginMain(String input) {
        input = input.toLowerCase().trim();
        switch (input) {
            case "help":
                System.out.println("""
                        Chess Post-login Help:
                        \thelp: list available commands
                        \tlogout: logout from chess
                        \tcreate: create a new game but don't join it
                        \tlist: get a list of all games
                        \tjoin: request to join a game
                        \tobserve: request to observe a game""");
                break;
            case "logout":
                ClientMain.getSingleton().facade.logout();
                handleLogout();
                break;
            case "create":
                createGame();
                break;
            case "list":
                listAllGames();
                break;
            case "join":
                joinGame();
                break;
            case "observe":
                observeGame();
                break;
            default:
                System.out.println("Unrecognized command. Type help for a list of commands.");
                break;
        }
    }
}
