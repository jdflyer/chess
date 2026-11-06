package client;

import chess.*;
import serverfacade.ServerFacade;
import serverfacade.ServerFacadeInterface;

import java.util.Scanner;

public class ClientMain {
    public enum State {
        LOGGED_OUT,
        LOGGED_IN,
        QUIT
    };

    public State state;
    Scanner scanner;
    ServerFacadeInterface facade;

    ClientMain() {
        state = State.LOGGED_OUT;
        scanner = new Scanner(System.in);
        facade = new ServerFacade();
    }

    boolean handleLogin() {
        return true;
    }

    boolean handleRegister() {
        return true;
    }

    void handlePreLogin(String input) {
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
                state = State.QUIT;
                break;
            case "login":
                if (handleLogin()) {
                    state = State.LOGGED_IN;
                }
            case "register":
                if (handleRegister()) {
                    state = State.LOGGED_IN;
                }
            default:
                System.out.println("Unrecognized command. Type help for a list of commands.");
                break;
        }
    }

    void handlePostLogin(String input) {

    }

    void run() {
        System.out.println("Welcome to Chess! Type Help for a list of available commands");
        while (state != State.QUIT) {
            System.out.print("["+state.toString()+"]> ");
            String input = scanner.nextLine().toLowerCase().trim();
            switch (state) {
                case LOGGED_OUT -> handlePreLogin(input);
                case LOGGED_IN -> handlePostLogin(input);
            }
        }
    }

    public static void main(String[] args) {
        ClientMain client = new ClientMain();
        client.run();
    }
}
