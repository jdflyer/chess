package dataaccess.sqldao;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import dataaccess.*;
import model.GameData;
import model.UserData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SQLGameDAO implements GameDAO {
    private String getUsernameString(String username) {
        return username.isEmpty() ? null : username;
    }
    private String getUsernameData(String username) {
        return username == null ? "" : username;
    }
    private String serializeGameData(ChessGame game) {
        return new Gson().toJson(game);
    }
    private ChessGame deserializeGameData(String gameData) {
        return new Gson().fromJson(gameData,ChessGame.class);
    }

    @Override
    public GameData getGameById(int gameId) throws InvalidGameException,DataAccessException {
        try {
            var statement = DatabaseManager.getConnection().prepareStatement("SELECT * FROM games where id=?");
            statement.setInt(1, gameId);
            var rs = statement.executeQuery();
            if (!rs.next()) {
                throw new InvalidGameException("There is no game with the id "+gameId);
            }
            return new GameData(gameId,getUsernameString(rs.getString("white_username")),
                    getUsernameString(rs.getString("black_username")),rs.getString("game_name"),
                    deserializeGameData(rs.getString("game_data")));
        }catch (SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }

    @Override
    public Collection<GameData> getAllGames() throws DataAccessException {
        try {
            ArrayList<GameData> games = new ArrayList<>();
            var rs = DatabaseManager.getConnection().prepareStatement("SELECT * FROM games;").executeQuery();
            while (rs.next()) {
                games.add(new GameData(rs.getInt("id"),getUsernameString(rs.getString("white_username")),
                        getUsernameString(rs.getString("black_username")),rs.getString("game_name"),
                        deserializeGameData(rs.getString("game_data"))));
            }
            return games;
        }catch (SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try {
            var statement = DatabaseManager.getConnection().prepareStatement("""
                    INSERT INTO games (white_username,black_username,game_name,game_data) VALUES (?,?,?,?);
                    """,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,"");
            statement.setString(2,"");
            statement.setString(3,gameName);
            statement.setString(4,serializeGameData(new ChessGame()));
            var rows_affected = statement.executeUpdate();
            if (rows_affected > 0) {
                var keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch(SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }

    @Override
    public void updateGame(GameData data) throws InvalidGameException,DataAccessException {
        try {
            // Triggers InvalidGameException if game is not found
            var game = getGameById(data.gameID());
            var statement = DatabaseManager.getConnection().prepareStatement("""
                    UPDATE games
                    SET white_username=?,
                        black_username=?,
                        game_name=?,
                        game_data=?
                    WHERE id=?;
                    """);
            statement.setString(1,getUsernameData(data.whiteUsername()));
            statement.setString(2,getUsernameData(data.blackUsername()));
            statement.setString(3,data.gameName());
            statement.setString(4,serializeGameData(data.game()));
            statement.setInt(5,data.gameID());
            statement.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessException("Error Accessing Database");
        }
    }
}
