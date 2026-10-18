package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData getGameById(String gameId) throws InvalidGameException;

    Collection<GameData> getAllGames() throws DataAccessException;

    void createGame(GameData gameData) throws DataAccessException;

    void updateGame(GameData data) throws InvalidGameException;
}
