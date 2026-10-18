package service.gameservice;

public class GameJoinTeamTakenException extends RuntimeException {
    public GameJoinTeamTakenException(String message) {
        super(message);
    }
}
