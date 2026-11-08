package client;

import model.GameCreateParams;
import model.GameList;
import serverfacade.BadRequestException;
import serverfacade.InvalidSessionException;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientPostLogin {
    // Stores the real Database game IDs. Note: the exposed IDs start at index 1
    ArrayList<Integer> realGameIds = new ArrayList<>();

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
            GameList list = ClientMain.getSingleton().facade.listAllGames();
            realGameIds.clear();
            if (list.games().length == 0) {
                System.out.println("There are no currently running games.");
                return;
            }
            for (var game : list.games()) {
                realGameIds.add(game.gameID());
                int playerCount = game.blackUsername() == null ? 0 : 1;
                playerCount += game.whiteUsername() == null ? 0 : 1;
                System.out.println("["+realGameIds.size()+"] "+game.gameName()+" ("+playerCount+"/2 Players)");
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
                break;
            case "observe":
                break;
            default:
                System.out.println("Unrecognized command. Type help for a list of commands.");
                break;
        }
    }
}
