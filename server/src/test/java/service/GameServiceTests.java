package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {
    @BeforeEach
    public void setup() {
        try {
            new DatabaseService().clearDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void listAllGamesNoGames() {
        try {
            GameList list = new GameService().listAllGames();
            Assertions.assertEquals(0,list.games().length);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createGame() {
        try {
            int id = new GameService().createGame("my game name");
            Assertions.assertEquals(1,new GameService().listAllGames().games().length);
            Assertions.assertEquals(id,new GameService().gameDAO.getGameById(id).gameID());

            // Same name is valid
            int id2 = new GameService().createGame("my game name");
            Assertions.assertEquals(2,new GameService().listAllGames().games().length);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void joinGameNormal() {
        try {
            int id = new GameService().createGame("my game name");
            new GameService().joinGame(id, ChessGame.TeamColor.BLACK,"myuser");
            new GameService().joinGame(id, ChessGame.TeamColor.WHITE,"myuser2");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void joinGameBothBlack() {
        try {
            int id = new GameService().createGame("my game name");
            new GameService().joinGame(id, ChessGame.TeamColor.BLACK, "myuser");
            new GameService().joinGame(id, ChessGame.TeamColor.BLACK, "myuser2");
            throw new Exception("GameJoinTeamTakenException should've been thrown!");
        } catch (GameJoinTeamTakenException e) {
            return;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void joinGameBothWhite() {
        try {
            int id = new GameService().createGame("my game name");
            new GameService().joinGame(id, ChessGame.TeamColor.WHITE, "myuser");
            new GameService().joinGame(id, ChessGame.TeamColor.WHITE, "myuser2");
            throw new Exception("GameJoinTeamTakenException should've been thrown!");
        } catch (GameJoinTeamTakenException e) {
            return;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
