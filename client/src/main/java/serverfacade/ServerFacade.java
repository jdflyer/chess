package serverfacade;

import client.ClientMain;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.AuthData;
import model.UserData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class ServerFacade implements ServerFacadeInterface {
    private final HttpClient httpClient;
    final String serverAddress;
    final int serverPort;

    private HttpRequest.Builder getUrlBuilder(String path) throws URISyntaxException {
        String url = String.format(Locale.getDefault(), "http://%s:%d%s",serverAddress,serverPort,path);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(java.time.Duration.ofMillis(5000));
        if (ClientMain.getSingleton().authData != null) {
            builder = builder.header("authorization",ClientMain.getSingleton().authData.authToken());
        }
        return builder;
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = getUrlBuilder(path).GET().build();
        return httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> post(String path, String body) throws Exception {
        HttpRequest request = getUrlBuilder(path).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> put(String path, String body) throws Exception {
        HttpRequest request = getUrlBuilder(path).PUT(HttpRequest.BodyPublishers.ofString(body)).build();
        return httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> delete(String path) throws Exception {
        HttpRequest request = getUrlBuilder(path).DELETE().build();
        return httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    }

    private String serialize(Object objectToSerialize) {
        return new Gson().toJson(objectToSerialize);
    }

    protected <T> T deserializeRecord(String json, Class<T> recordOfT) throws JsonSyntaxException {
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
        httpClient = HttpClient.newHttpClient();
    }

    public AuthData register(UserData userData) throws UsernameTakenException, BadRequestException {
        try {
            var response = post("/user",serialize(userData));
            if (response.statusCode() == 200) {
                return deserializeRecord(response.body(),AuthData.class);
            }else if (response.statusCode() == 403) {
                throw new UsernameTakenException("Username already taken!");
            }else {
                throw new BadRequestException("");
            }
        }catch (UsernameTakenException e) {
            throw e;
        }catch (Exception e) {
            throw new BadRequestException("The request to the server failed!");
        }
    }

    public AuthData login(String username, String password) {
        return new AuthData("","");
    }

    public void logout() {

    }
}
