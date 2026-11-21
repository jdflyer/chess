package serverfacade;

import client.ClientMain;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class HttpCommunicator {
    private final HttpClient httpClient;
    final String serverAddress;
    final int serverPort;

    public HttpCommunicator(String address, int port) {
        serverAddress = address;
        serverPort = port;
        httpClient = HttpClient.newHttpClient();
    }

    public static final int STATUS_OK = 200;
    public static final int STATUS_UNAUTHORIZED = 401;
    public static final int STATUS_ERROR = 500;

    private HttpRequest.Builder getUrlBuilder(String path) throws URISyntaxException {
        String url = String.format(Locale.getDefault(), "http://%s:%d%s",serverAddress,serverPort,path);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(java.time.Duration.ofMillis(5000));
        // Add authorization to the request if the client is logged in
        if (ClientMain.getSingleton().authData != null) {
            builder = builder.header("authorization",ClientMain.getSingleton().authData.authToken());
        }
        return builder;
    }

    public HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = getUrlBuilder(path).GET().build();
        return httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> post(String path, String body) throws Exception {
        HttpRequest request = getUrlBuilder(path).POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> put(String path, String body) throws Exception {
        HttpRequest request = getUrlBuilder(path).PUT(HttpRequest.BodyPublishers.ofString(body)).build();
        return httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> delete(String path) throws Exception {
        HttpRequest request = getUrlBuilder(path).DELETE().build();
        return httpClient.send(request,HttpResponse.BodyHandlers.ofString());
    }
}
