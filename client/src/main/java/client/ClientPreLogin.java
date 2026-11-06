package client;

public class ClientPreLogin {
    boolean handleLogin() {
        return true;
    }

    boolean handleRegister() {
        return true;
    }

    void handlePreLogin(String input) {
        input = input.toLowerCase().trim();
        switch (input) {
            case "help":
                System.out.println("""
                        Chess Pre-login Help:
                        \thelp: list available commands
                        \tquit: exit the chess program
                        \tlogin: login to the chess server
                        \tregister: register a new user to the chess server""");
                break;
            case "quit":
                ClientMain.getSingleton().state = ClientMain.State.QUIT;
                break;
            case "login":
                if (handleLogin()) {
                    ClientMain.getSingleton().state = ClientMain.State.LOGGED_IN;
                }
                break;
            case "register":
                if (handleRegister()) {
                    ClientMain.getSingleton().state = ClientMain.State.LOGGED_IN;
                }
                break;
            default:
                System.out.println("Unrecognized command. Type help for a list of commands.");
                break;
        }
    }
}
