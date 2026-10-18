package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    static public HashMap<Integer, GameData> gameDataMap = new HashMap<>();

    private void verifyValidGame(int gameId) throws InvalidGameException {
        if (!gameDataMap.containsKey(gameId)) {
            throw new InvalidGameException("There is no game that exists for ID ("+gameId+")");
        }
    }

    @Override
    public GameData getGameById(int gameId) throws InvalidGameException {
        verifyValidGame(gameId);
        return gameDataMap.get(gameId);
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        return gameDataMap.values();
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        gameDataMap.put(gameData.gameID(),gameData);
    }

    @Override
    public void updateGame(GameData data) throws InvalidGameException {
        verifyValidGame(data.gameID());
        gameDataMap.put(data.gameID(),data);
    }
}
