package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.InvalidAuthTokenException;
import dataaccess.UserDAO;
import dataaccess.sqldao.SQLAuthDAO;
import dataaccess.sqldao.SQLDatabaseDAO;
import dataaccess.sqldao.SQLGameDAO;
import dataaccess.sqldao.SQLUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SQLGameDAOTests {
    @BeforeEach
    public void setup() {
        try {
            new SQLDatabaseDAO().clearDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createGame() {
        try {
            var dao = new SQLGameDAO();
            int id = dao.createGame("my_game");
            var games = dao.getAllGames();
            Assertions.assertEquals(1,games.size());
            var game = dao.getGameById(id);
            Assertions.assertEquals("my_game",game.gameName());
            Assertions.assertEquals(null,game.whiteUsername());
            Assertions.assertEquals(null,game.blackUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void updateGame() {
        try {
            var dao = new SQLGameDAO();
            int id = dao.createGame("my_game");
            var games = dao.getAllGames();
            Assertions.assertEquals(1,games.size());
            dao.updateGame(new GameData(id,"my_username","username2","my_game",null));
            var game = dao.getGameById(id);
            Assertions.assertEquals("my_game",game.gameName());
            Assertions.assertEquals("my_username",game.whiteUsername());
            Assertions.assertEquals("username2",game.blackUsername());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
