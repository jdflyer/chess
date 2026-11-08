package client;

import model.AuthData;
import model.LoginData;
import model.UserData;
import serverfacade.BadRequestException;
import serverfacade.InvalidUsernamePasswordException;
import serverfacade.UsernameTakenException;

public class ClientPreLogin {
    boolean handleLogin() {
        System.out.print("Username: ");
        String username = ClientMain.getSingleton().scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = ClientMain.getSingleton().scanner.nextLine().trim();

        try {
            ClientMain.getSingleton().authData = ClientMain.getSingleton().facade.login(
                    new LoginData(username,password));
            return true;
        }catch (InvalidUsernamePasswordException e) {
            System.out.println("Invalid Username or Password!");
        }catch (BadRequestException e) {
            ClientMain.printBadRequest();
        }
        return false;
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
            System.out.println("Error registering user: The username "+username+" is already taken!");
        }catch (BadRequestException e) {
            ClientMain.printBadRequest();
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
