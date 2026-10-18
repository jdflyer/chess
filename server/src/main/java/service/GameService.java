package service;

import dataaccess.*;
import model.GameData;

import java.util.Collection;
import java.util.UUID;

public class GameService {
    GameDAO gameDAO;


    public GameService() {
        this.gameDAO = new MemoryGameDAO();
    }

    public GameList listAllGames() throws DataAccessException {
        Collection<GameData> gameList = gameDAO.getAllGames();
        GameMetadata[] metadata = new GameMetadata[gameList.size()];

        int idx = 0;
        for (GameData game : gameList) {
            metadata[idx] = new GameMetadata(game.gameID(),game.whiteUsername(),game.blackUsername(),game.gameName());
            idx++;
        }

        return new GameList(metadata);
    }

    public int createGame(String gameName) throws DataAccessException {
        return gameDAO.createGame(gameName);
    }
}
