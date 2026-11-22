package client;

import chess.ChessGame;
import model.AuthData;
import model.GameMetadata;
import serverfacade.ServerFacade;
import serverfacade.ServerFacadeInterface;

import java.util.Scanner;

public class ClientMain {
    private static final ClientMain SINGLETON = new ClientMain();
    public static ClientMain getSingleton() { return SINGLETON; }

    public enum State {
        LOGGED_OUT,
        LOGGED_IN,
        GAME,
        QUIT
    };

    private State state;
    public Scanner scanner;
    public ServerFacadeInterface facade;
    public AuthData authData;
    ClientPreLogin clientPreLogin;
    ClientPostLogin clientPostLogin;
    ClientGame clientGame;

    public State getState() { return state; }

    public void switchToLoggedOutState() {
        state = State.LOGGED_OUT;
        authData = null;
    }

    public void switchToLoggedInState() {
        state = State.LOGGED_IN;
    }

    public void switchToGameState(GameMetadata metadata, ChessGame.TeamColor team) {
        state = State.GAME;
        clientGame.initGame(metadata, team);
    }

    public void switchToQuitState() {
        state = State.QUIT;
    }

    ClientMain() {
        state = State.LOGGED_OUT;
        authData = null;
        scanner = new Scanner(System.in);
        clientPreLogin = new ClientPreLogin();
        clientPostLogin = new ClientPostLogin();
        clientGame = new ClientGame();
    }

    void run(String ip) {
        facade = new ServerFacade(ip,8080);
        System.out.println("Welcome to Chess! Type Help for a list of available commands");
        while (getState() != State.QUIT) {
            System.out.print("["+getState().toString()+"]> ");
            String input = scanner.nextLine();
            switch (getState()) {
                case LOGGED_OUT -> clientPreLogin.preLoginMain(input);
                case LOGGED_IN -> clientPostLogin.postLoginMain(input);
                case GAME -> clientGame.gameMain(input);
            }
        }
    }

    public static void main(String[] args) {
        String ip = "localhost";
        if (args.length >= 1) {
            ip = args[0];
        }
        SINGLETON.run(ip);
    }

    public static void printBadRequest() {
        System.out.println("Error communicating to server!");
    }
}
