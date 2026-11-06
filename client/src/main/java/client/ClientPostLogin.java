package client;

public class ClientPostLogin {

    void handlePostLogin(String input) {
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
                ClientMain.getSingleton().state = ClientMain.State.LOGGED_OUT;
                break;
            case "create":
                break;
            case "list":
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
