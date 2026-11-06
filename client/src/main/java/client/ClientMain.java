package client;

import serverfacade.ServerFacade;
import serverfacade.ServerFacadeInterface;

import java.util.Scanner;

public class ClientMain {
    private static ClientMain singleton = new ClientMain();
    public static ClientMain getSingleton() { return singleton; }

    public enum State {
        LOGGED_OUT,
        LOGGED_IN,
        QUIT
    };

    public State state;
    Scanner scanner;
    ServerFacadeInterface facade;
    ClientPreLogin clientPreLogin;
    ClientPostLogin clientPostLogin;

    ClientMain() {
        state = State.LOGGED_OUT;
        scanner = new Scanner(System.in);
        facade = new ServerFacade();
        clientPreLogin = new ClientPreLogin();
        clientPostLogin = new ClientPostLogin();
    }

    void run() {
        System.out.println("Welcome to Chess! Type Help for a list of available commands");
        while (state != State.QUIT) {
            System.out.print("["+state.toString()+"]> ");
            String input = scanner.nextLine();
            switch (state) {
                case LOGGED_OUT -> clientPreLogin.handlePreLogin(input);
                case LOGGED_IN -> clientPostLogin.handlePostLogin(input);
            }
        }
    }

    public static void main(String[] args) {
        singleton.run();
    }
}
