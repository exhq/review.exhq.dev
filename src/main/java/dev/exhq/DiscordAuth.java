package dev.exhq;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

public class DiscordAuth {
    public static <T> HttpResponse.BodyHandler<T> getJsonHandler(@NotNull Class<T> tClass) {
        return responseInfo -> HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofInputStream(),
                inp -> new Gson().fromJson(new InputStreamReader(inp), tClass));
    }
    static HttpClient httpClient = HttpClient.newHttpClient();
    public static CurrentAuthorization ValidateAuthorization(String token) {
        try {
            var response = httpClient.send(HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI("https://discord.com/api/v10/oauth2/@me"))
                    .header("Authorization", "Bearer " + token)
                    .build(), getJsonHandler(CurrentAuthorization.class));
            if (response.statusCode() != 200)
                return null;
            var data = response.body();
            if (!Objects.equals(data.application().id(), "1208380910525743134" )) {
                System.out.println("Logged in as " + data.application().id() + " expected 1208380910525743134");
                return null;
            }
            return data;
        } catch (IOException | InterruptedException | URISyntaxException e) {
            return null;
        }
    }
}
