package handler;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.InvalidAuthTokenException;
import dataaccess.InvalidGameException;
import dataaccess.sqldao.SQLGameDAO;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import model.GameMetadata;
import service.SessionService;
import service.gameservice.GameService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.HashMap;

public class WebsocketHandler {
    WsConfig wsConfig;
    HashMap<Integer, HashMap<String, WsContext>> gameIdToStringToWsContextMap;

    public WebsocketHandler(WsConfig wsConfig) {
        this.wsConfig = wsConfig;
        gameIdToStringToWsContextMap = new HashMap<>();
    }

    String createErrorMessage(String errorString) {
        ServerErrorMessage message = new ServerErrorMessage("Error: "+errorString);
        return new Gson().toJson(message);
    }

    void sendError(WsMessageContext ctx, String errorString) {
        if (!ctx.session.isOpen()) {
            return;
        }
        ctx.send(createErrorMessage(errorString));
    }

    void sendLoadGame(WsContext ctx, ChessGame game) {
        if (!ctx.session.isOpen()) {
            return;
        }
        LoadGameMessage message = new LoadGameMessage(game);
        ctx.send(new Gson().toJson(message));
    }

    void sendLoadGameToAll(int gameId, ChessGame game) {
        if (!gameIdToStringToWsContextMap.containsKey(gameId)) {
            return;
        }
        gameIdToStringToWsContextMap.get(gameId).forEach((username,context) -> {
            sendLoadGame(context, game);
        });
    }

    void sendNotification(WsContext ctx, String message) {
        if (!ctx.session.isOpen()) {
            return;
        }

        NotificationMessage messageToSend = new NotificationMessage(message);
        ctx.send(new Gson().toJson(messageToSend));
    }

    void sendNotificationToAll(int gameId, String message) {
        if (!gameIdToStringToWsContextMap.containsKey(gameId)) {
            return;
        }
        gameIdToStringToWsContextMap.get(gameId).forEach((username,context) -> {
            sendNotification(context, message);
        });
    }

    void sendNotificationToAllButOne(int gameId, String userToExclude, String message) {
        if (!gameIdToStringToWsContextMap.containsKey(gameId)) {
            return;
        }
        gameIdToStringToWsContextMap.get(gameId).forEach((username,context) -> {
            if (username.equals(userToExclude)) {
                return;
            }
            sendNotification(context, message);
        });
    }

    private record UsernameData(String whiteUsername, String blackUsername) {}

    void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = Handler.deserialize(UserGameCommand.class, ctx.message());
            
            int gameId = command.getGameID();
            GameService gameService = new GameService();
            GameData game = gameService.getGameById(gameId);

            SessionService sessionService = new SessionService();
            AuthData playerAuth = sessionService.getAuthData(command.getAuthToken());

            ChessGame chessGame = game.game();
            UsernameData usernameData = new UsernameData(game.whiteUsername(), game.blackUsername());
            
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(ctx, command, game, playerAuth);
                case LEAVE -> usernameData = handleLeave(ctx, command, game, playerAuth, usernameData);
                case RESIGN -> handleResign(ctx, command, game, playerAuth, chessGame);
                case MAKE_MOVE -> handleMove(ctx, command, game, playerAuth, chessGame);
                default -> sendError(ctx, "Invalid command type!");
            }

            gameService.gameDAO.updateGame(new GameData(game.gameID(), usernameData.whiteUsername(), usernameData.blackUsername(),
                    game.gameName(), chessGame));
        }catch (InvalidAuthTokenException e) {
            sendError(ctx, "Invalid session!");
        }
        catch (InvalidGameException e) {
            sendError(ctx, "Invalid game ID!");
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            sendError(ctx, "Error parsing message on server!");
        }
    }

    String getPositionString(ChessPosition position) {
        char col = (char)('a'+position.getColumn()-1);
        return Character.toString(col) + position.getRow();
    }

    private void handleMove(WsMessageContext ctx, UserGameCommand command, GameData game, AuthData playerAuth, ChessGame chessGame) {
        if (chessGame.getGameEnded()) {
            sendError(ctx, "Can't move! The game has already ended!");
            return;
        }

        UserMoveCommand moveCommand = Handler.deserialize(UserMoveCommand.class, ctx.message());
        if (moveCommand.getMove() == null) {
            sendError(ctx, "Can't move! Invalid move sent to server!");
            return;
        }
        String username = playerAuth.username();
        if (!username.equals(game.whiteUsername()) && !username.equals(game.blackUsername())) {
            sendError(ctx, "Can't move! Player isn't in the game!");
            return;
        }
        if (username.equals(game.whiteUsername()) && chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK) {
            sendError(ctx, "Can't move! It isn't white team's turn!");
            return;
        }
        if (username.equals(game.blackUsername()) && chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE) {
            sendError(ctx, "Can't move! It isn't black team's turn!");
            return;
        }

        try {
            chessGame.makeMove(moveCommand.getMove());
        }catch (InvalidMoveException e) {
            sendError(ctx, "The requested move is invalid!");
            return;
        }

        sendLoadGameToAll(game.gameID(),chessGame);

        String startString = getPositionString(moveCommand.getMove().getStartPosition());
        String endString = getPositionString(moveCommand.getMove().getEndPosition());
        sendNotificationToAllButOne(game.gameID(), playerAuth.username(), "move "+startString+" "+endString);

        if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
            chessGame.setGameEnded(true);
            sendNotificationToAll(game.gameID(),"stalemate "+ game.blackUsername());
            return;
        }
        if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
            chessGame.setGameEnded(true);
            sendNotificationToAll(game.gameID(),"stalemate "+ game.whiteUsername());
            return;
        }

        if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            chessGame.setGameEnded(true);
            sendNotificationToAll(game.gameID(),"checkmate "+ game.blackUsername());
            return;
        }
        if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            chessGame.setGameEnded(true);
            sendNotificationToAll(game.gameID(),"checkmate "+ game.whiteUsername());
            return;
        }

        if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
            sendNotificationToAll(game.gameID(),"check "+ game.blackUsername());
            return;
        }
        if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)) {
            sendNotificationToAll(game.gameID(),"check "+ game.whiteUsername());
        }
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command, GameData game, AuthData playerAuth, ChessGame chessGame) {
        if (!playerAuth.username().equals(game.whiteUsername()) && !playerAuth.username().equals(game.blackUsername())) {
            sendError(ctx, "You can't resign if you aren't in the game!");
            return;
        }
        if (chessGame.getGameEnded()) {
            sendError(ctx, "You can't resign: The game has already ended");
            return;
        }

        chessGame.setGameEnded(true);
        sendNotificationToAll(game.gameID(), "resign "+playerAuth.username());
    }

    private UsernameData handleLeave(WsMessageContext ctx, UserGameCommand command, GameData game, AuthData playerAuth, UsernameData usernameData) {
        if (!gameIdToStringToWsContextMap.containsKey(game.gameID())) {
            return usernameData;
        }
        HashMap<String, WsContext> stringToContextMap = gameIdToStringToWsContextMap.get(game.gameID());
        if (!stringToContextMap.containsKey(playerAuth.username())) {
            return usernameData;
        }
        stringToContextMap.remove(playerAuth.username());

        sendNotificationToAll(game.gameID(), "leave "+playerAuth.username());

        if (playerAuth.username().equals(game.blackUsername())) {
            return new UsernameData(usernameData.whiteUsername(), null);
        }else if (playerAuth.username().equals(game.whiteUsername())) {
            return new UsernameData(null, usernameData.blackUsername());
        }
        return usernameData;
    }

    private void handleConnect(WsMessageContext ctx, UserGameCommand command, GameData game, AuthData playerAuth) {
        // Register this player as able to receive notifications
        HashMap<String, WsContext> stringToContextMap = gameIdToStringToWsContextMap.computeIfAbsent(
                game.gameID(), k -> new HashMap<String, WsContext>());

        stringToContextMap.put(playerAuth.username(), ctx);

        sendLoadGame(ctx, game.game());

        String playerTeam = "observer";
        if (game.blackUsername() != null && game.blackUsername().equals(playerAuth.username())) {
            playerTeam = "black";
        }else if (game.whiteUsername() != null && game.whiteUsername().equals(playerAuth.username())) {
            playerTeam = "white";
        }

        sendNotificationToAllButOne(game.gameID(), playerAuth.username(),
                "join "+playerAuth.username()+" "+playerTeam);
    }

    public void handleWebsocketconfig() {
        wsConfig.onConnect(ctx -> {
            ctx.enableAutomaticPings();
            System.out.println("Websocket connected from "+ctx.session.getRemote().toString());
        });
        wsConfig.onMessage(ctx -> {
            handleMessage(ctx);
        });
        wsConfig.onClose(ctx -> {
            System.out.println("Websocket closed from "+ctx.session.getRemote().toString());
        });
    }
}
