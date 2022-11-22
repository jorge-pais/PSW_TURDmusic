package repository;

import com.google.gson.Gson;
import model.GenreInfo;
import model.Token;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;

public class Repository {
    private static final String BASE_URL = "https://api.spotify.com";
    private static final String CLIENT_ID = "ebd890b98f21490a9ab881d3ba0cbb45";
    private static final String CLIENT_SECRET = "64442f6027c04bef8a7caef8ffe2dbcf";
    // getGenres
    public static GenreInfo getGenres() throws URISyntaxException, IOException, InterruptedException {
        String response = getRequest("/v1/recommendations/available-genre-seeds");

        Gson gson = new Gson();
        GenreInfo gSet = gson.fromJson(response, GenreInfo.class);
        System.out.println(gSet.getGenres());
        return gSet;
    }

    // getArtists
    /*public static ArtistInfo getArtists() throws URISyntaxException, IOException, InterruptedException {
        String response = getRequest("/genre/all");

        Gson gson = new Gson();
        GenreInfo gSet = gson.fromJson(response, GenreInfo.class);
        System.out.println(gSet.getGenreCount());
        return gSet;
    }*/

    // private getRequest(String path)

    private static String getRequest(String path) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(BASE_URL + path))
                .header("Authorization", "Bearer " + getToken())
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        System.out.println(response.body());
        return response.body();
    }

    private static String getToken() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://accounts.spotify.com/api/token"))
                .header("Authorization","Basic " + Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        System.out.println(response.body());

        Token token = new Gson().fromJson(response.body(), Token.class);
        return token.getAccess_token();
    }
}
