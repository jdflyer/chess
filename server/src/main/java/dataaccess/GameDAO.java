package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData getGameById(int gameId) throws InvalidGameException;

    Collection<GameData> getAllGames() throws DataAccessException;

    // Returns a gameID
    int createGame(String gameName) throws DataAccessException;

    void updateGame(GameData data) throws InvalidGameException;
}
