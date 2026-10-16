package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    GameData getGameById(String gameId) throws InvalidGameException;

    Collection<GameData> getAllGames();

    void createGame(GameData gameData);

    void updateGame(GameData data) throws InvalidGameException;
}
