package handler;

import chess.ChessGame;
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
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerMessage;

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
        ctx.send(createErrorMessage(createErrorMessage(errorString)));
    }

    void sendLoadGame(WsMessageContext ctx, ChessGame game) {
        LoadGameMessage message = new LoadGameMessage(game);
        ctx.send(new Gson().toJson(message));
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

    void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = Handler.deserialize(UserGameCommand.class, ctx.message());
            
            int gameId = command.getGameID();
            GameData game = new GameService().getGameById(gameId);

            SessionService sessionService = new SessionService();
            AuthData playerAuth = sessionService.getAuthData(command.getAuthToken());
            
            // Move to move commands
//            if (!playerAuth.username().equals(game.whiteUsername()) && !playerAuth.username().equals(game.blackUsername())) {
//                sendError(ctx, "Player does not have correct auth to send a command to this game!");
//                return;
//            }
            
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(ctx, command, game, playerAuth);
                case LEAVE -> handleLeave(ctx, command, game, playerAuth);
                case RESIGN -> handleResign(ctx, command, game, playerAuth);
                case MAKE_MOVE -> handleMove(ctx, command, game, playerAuth);
                default -> sendError(ctx, "Invalid command type!");
            }
        }catch (InvalidAuthTokenException e) {
            sendError(ctx, "Invalid session!");
        }
        catch (InvalidGameException e) {
            sendError(ctx, "Invalid game ID!");
        } catch (Exception e) {
            sendError(ctx, "Error parsing message on server!");
        }
    }

    private void handleMove(WsMessageContext ctx, UserGameCommand command, GameData game, AuthData playerAuth) {
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command, GameData game, AuthData playerAuth) {
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command, GameData game, AuthData playerAuth) {
    }

    private void handleConnect(WsMessageContext ctx, UserGameCommand command, GameData game, AuthData playerAuth) {
        // Register this player as able to receive notifications
        HashMap<String, WsContext> stringToContextMap = gameIdToStringToWsContextMap.computeIfAbsent(
                game.gameID(), k -> new HashMap<String, WsContext>());
        stringToContextMap.put(playerAuth.username(), ctx);

        sendLoadGame(ctx, game.game());

        String playerTeam = "observer";
        if (game.blackUsername().equals(playerAuth.username())) {
            playerTeam = "black";
        }else if (game.whiteUsername().equals(playerAuth.username())) {
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
