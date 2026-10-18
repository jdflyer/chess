package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import server.ServerConfig;

import java.util.Collection;
import java.util.UUID;

public class GameService {
    GameDAO gameDAO;


    public GameService() {
        if (ServerConfig.databaseType == ServerConfig.DatabaseType.MEMORY) {
            this.gameDAO = new MemoryGameDAO();
        }
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

    public void joinGame(int gameId, ChessGame.TeamColor color, String username) throws InvalidGameException, GameJoinTeamTakenException ,DataAccessException {
        GameData currentGame = gameDAO.getGameById(gameId);
        String blackUsername = currentGame.blackUsername();
        String whiteUsername = currentGame.whiteUsername();
        if (color == ChessGame.TeamColor.BLACK) {
            if (currentGame.blackUsername() != null) {
                throw new GameJoinTeamTakenException("Attempted to join Black team when there is a player on it");
            }
            blackUsername = username;
        }else if (color == ChessGame.TeamColor.WHITE) {
            if (currentGame.whiteUsername() != null) {
                throw new GameJoinTeamTakenException("Attempted to join White team when there is a player on it");
            }
            whiteUsername = username;
        }

        gameDAO.updateGame(new GameData(gameId, whiteUsername, blackUsername, currentGame.gameName(), currentGame.game()));
    }
}
