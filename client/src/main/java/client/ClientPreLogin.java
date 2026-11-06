package client;

import model.AuthData;
import model.UserData;
import serverfacade.BadRequestException;
import serverfacade.UsernameTakenException;

public class ClientPreLogin {
    boolean handleLogin() {
        return true;
    }

    boolean handleRegister() {
        System.out.print("Please type in your username: ");
        String username = ClientMain.getSingleton().scanner.nextLine().trim();
        System.out.print("Please type in your password: ");
        String password = ClientMain.getSingleton().scanner.nextLine().trim();
        System.out.print("Please type in your email: ");
        String email = ClientMain.getSingleton().scanner.nextLine().trim();

        try {
            ClientMain.getSingleton().authData = ClientMain.getSingleton().facade.register(
                    new UserData(username,password,email));
            return true;
        }catch (UsernameTakenException e) {
            System.out.println("Error while registering user: Username already taken");
        }catch (BadRequestException e) {
            System.out.println("Error communicating to server while registering user!");
        }
        return false;
    }

    void preLoginMain(String input) {
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
