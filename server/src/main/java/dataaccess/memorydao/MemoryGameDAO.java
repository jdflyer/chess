package dataaccess.memorydao;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.InvalidGameException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    static public HashMap<Integer, GameData> gameDataMap = new HashMap<>();
    static public int nextGameId = 1;

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
    public int createGame(String gameName) throws DataAccessException {
        int gameId = nextGameId;
        gameDataMap.put(gameId,new GameData(gameId,null,null,gameName, new ChessGame()));
        nextGameId++;
        return gameId;
    }

    @Override
    public void updateGame(GameData data) throws InvalidGameException {
        verifyValidGame(data.gameID());
        gameDataMap.put(data.gameID(),data);
    }
}
