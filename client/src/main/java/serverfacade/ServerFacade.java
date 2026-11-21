package serverfacade;

import client.ClientMain;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.*;
import websocket.commands.UserGameCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Locale;

public class ServerFacade implements ServerFacadeInterface {
    final String serverAddress;
    final int serverPort;
    HttpCommunicator http;
    WebsocketCommunicator ws;

    // Saved during ws connect, used for subsequent requests
    int gameId;

    private String serialize(Object objectToSerialize) {
        return new Gson().toJson(objectToSerialize);
    }

    private <T> T deserializeRecord(String json, Class<T> recordOfT) throws JsonSyntaxException {
        T record = new Gson().fromJson(json, recordOfT);
        if (recordOfT.isRecord()) {
            for (RecordComponent component : recordOfT.getRecordComponents()) {
                try {
                    if (component.getAccessor().invoke(record) == null) {
                        throw new JsonSyntaxException(component.getName()+" is expected but not included!");
                    }
                }catch (IllegalAccessException | InvocationTargetException e) {
                    continue;
                }
            }
        }
        return record;
    }

    public ServerFacade(String address, int port) {
        serverAddress = address;
        serverPort = port;
        http = new HttpCommunicator(address,port);
    }

    public AuthData register(UserData userData) throws UsernameTakenException, BadRequestException {
        try {
            var response = http.post("/user",serialize(userData));
            return switch (response.statusCode()) {
                case HttpCommunicator.STATUS_OK -> deserializeRecord(response.body(), AuthData.class);
                case 403 -> throw new UsernameTakenException("Username already taken!");
                default -> throw new BadRequestException("");
            };
        }catch (UsernameTakenException e) {
            throw e;
        }catch (Exception e) {
            throw new BadRequestException("The request to the server failed!");
        }
    }

    public AuthData login(LoginData loginData) throws InvalidUsernamePasswordException, BadRequestException {
        try {
            var response = http.post("/session",serialize(loginData));
            return switch (response.statusCode()) {
                case HttpCommunicator.STATUS_OK -> deserializeRecord(response.body(), AuthData.class);
                case HttpCommunicator.STATUS_UNAUTHORIZED -> throw new InvalidUsernamePasswordException("Username already taken!");
                default -> throw new BadRequestException("");
            };
        }catch (InvalidUsernamePasswordException e) {
            throw e;
        }catch (Exception e) {
            throw new BadRequestException("Failed!");
        }
    }

    public void logout() {
        try {
            var response = http.delete("/session");
        }catch (Exception e){
            return;
        }
    }

    public GameList listAllGames() throws BadRequestException, InvalidSessionException {
        try {
            var response = http.get("/game");
            return switch (response.statusCode()) {
                case HttpCommunicator.STATUS_OK -> deserializeRecord(response.body(),GameList.class);
                case HttpCommunicator.STATUS_UNAUTHORIZED -> throw new InvalidSessionException("Not authorized!");
                default -> throw new BadRequestException("");
            };
        }catch (InvalidSessionException e) {
            throw e;
        }catch (Exception e) {
            throw new BadRequestException("Bad request");
        }
    }

    public GameCreateResponse createGame(GameCreateParams gameCreateParams) throws BadRequestException, InvalidSessionException {
        try {
            var response = http.post("/game",serialize(gameCreateParams));
            return switch (response.statusCode()) {
                case HttpCommunicator.STATUS_OK -> deserializeRecord(response.body(),GameCreateResponse.class);
                case HttpCommunicator.STATUS_UNAUTHORIZED -> throw new InvalidSessionException("Not authorized!");
                default -> throw new BadRequestException("");
            };
        }catch (InvalidSessionException e) {
            throw e;
        }catch (Exception e) {
            throw new BadRequestException("Bad request");
        }
    }

    public void joinGame(GameJoinParams joinParams) throws BadRequestException, InvalidSessionException {
        try {
            var response = http.put("/game",serialize(joinParams));
            // We already handle not joining a taken team on the client side (when joining), so we won't handle that error here
            switch (response.statusCode()) {
                case HttpCommunicator.STATUS_OK:
                    return;
                case HttpCommunicator.STATUS_UNAUTHORIZED:
                    throw new InvalidSessionException("Not authorized!");
                default:
                    throw new BadRequestException("");
            }
        }catch (InvalidSessionException e) {
            throw e;
        }catch (Exception e) {
            throw new BadRequestException("Bad request");
        }
    }

    public void connectToGame(int gameId) throws BadRequestException {
        this.gameId = gameId;
        try {
            this.ws = new WebsocketCommunicator(serverAddress, serverPort);
            ws.connect();
            UserGameCommand command = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT, ClientMain.getSingleton().authData.authToken(), gameId);
            ws.send(serialize(command));
        }catch (Exception e) {
            throw new BadRequestException("Bad request");
        }
    }
}
