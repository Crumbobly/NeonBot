package ru.lab.service;

import org.springframework.stereotype.Service;
import ru.lab.http.HttpContentType;
import ru.lab.http.HttpMethod;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class RequestService {

    private final HttpClient client;
    private final CookieManager cookieManager;

    public RequestService() {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client = HttpClient.newBuilder().cookieHandler(cookieManager).build();
    }

    public HttpResponse<String> sendRequest(String url, HttpMethod method, HttpContentType contentType, String accessToken, String body) {

        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", contentType.getValue());


            if (accessToken != null && !accessToken.isEmpty()) {
                builder.header("Authorization", "Bearer " + accessToken);
            }

            switch (method) {
                case POST -> builder.POST(HttpRequest.BodyPublishers.ofString(body != null ? body : ""));
                case GET -> builder.GET();
                default -> throw new IllegalArgumentException("Unsupported method: " + method);
            }

            HttpRequest request = builder.build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to send request", e);
        }
    }

    public void clearCookie(){
        cookieManager.getCookieStore().removeAll();
    }


}
