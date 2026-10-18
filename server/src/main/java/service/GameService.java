package service;

import dataaccess.*;
import handler.UsernameAlreadyTakenException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

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
}
