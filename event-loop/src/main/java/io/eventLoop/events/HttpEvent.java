package io.eventLoop.events;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import lombok.Getter;

@Getter
public class HttpEvent extends AbstractEvent<Integer> {

    private final String url;
    private final String method;
    private final String body;

    public HttpEvent(String url, String method, String body) {
        super();
        this.url = url;
        this.method = method;
        this.body = body;
    }


    public Integer sendRequest() throws IOException, InterruptedException {
        var httpClient = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .method(method, HttpRequest.BodyPublishers.ofString(body))
            .build();
        var response = httpClient.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
        return response.statusCode();
    }
}
