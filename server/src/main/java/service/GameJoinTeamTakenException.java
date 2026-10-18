package service;

public class GameJoinTeamTakenException extends RuntimeException {
    public GameJoinTeamTakenException(String message) {
        super(message);
    }
}
