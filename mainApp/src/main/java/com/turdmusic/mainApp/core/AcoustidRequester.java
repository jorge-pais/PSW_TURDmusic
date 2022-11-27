package com.turdmusic.mainApp.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.turdmusic.mainApp.core.models.MusicInfo;
import com.google.gson.Gson;


//
// Resources for implementing this:
//      https://acoustid.org/webservice
// The acoustid web service can utilize fpcalc fingerprints to
// match songs with ones from the musicbrainz database
//      https://musicbrainz.org/doc/MusicBrainz_API
// The resulting ID's can then be queried to obtain the metadata in
//
// TODO: Instead of searching for fpcalc existance, we should have it in a folder in our app
// TODO: Manage multiple Artists and Albuns
public class AcoustidRequester {

    // API key
    private final String key = "PFjVWjJwPw";

    // API request parameters
    private final String baseURL = "https://api.acoustid.org/v2/lookup";
    private final String fpcalcPath = "C:\\Users\\David\\Downloads\\fpcalc";
    private final String os = System.getProperty("os.name").toLowerCase();



    // Possibly in a different class - with Token for Spotify
    private String getAPIRequest(String url, String path) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url + path))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Maps json response to MusicInfo (java class)
    // Returned value has music, artist information
    private MusicInfo.Result.Record getMusicInfo(String fingerprint, int duration) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Fetching Data...");
        String response = getAPIRequest(baseURL,"?client="+key+"&meta=recordings+compress&duration="+duration+"&fingerprint="+fingerprint);

        Gson gson = new Gson();
        MusicInfo music = gson.fromJson(response, MusicInfo.class);

        if(!music.getStatus().equals("ok")){
            return null;
        }

        System.out.println("Data Fetched...");
        double THRESHOLD = 0.6;
        MusicInfo.Result filteredResult = music.getResults().stream()
                .filter(e -> e.getScore() > THRESHOLD)
                .max((val1, val2) -> (int) (val1.getScore()*10000 - val2.getScore()*10000))
                .orElse(null);


        List<MusicInfo.Result.Record> filteredRecord = filteredResult.getRecordings();
        Map<String, Long> recordsName = filteredRecord.stream()
                .filter(record -> record.getTitle()!=null)
                .collect(Collectors.groupingBy(MusicInfo.Result.Record::getTitle, Collectors.counting()));

        String musicName = recordsName.entrySet().stream()
                .max(Map.Entry.comparingByValue()).get().getKey();


        MusicInfo.Result.Record musicInformation = filteredRecord.stream()
                .filter(record -> record.getTitle()!=null && record.getTitle().equals(musicName))
                .findFirst()
                .orElse(null);

        return musicInformation;
    }

    private String getFingerprint(String filepath) throws Exception{

        ProcessBuilder processBuilder = new ProcessBuilder();


        // Cross-platform compatibility
        if (os.contains("windows")) {
            processBuilder.command("cmd.exe", "/c", fpcalcPath + " -plain \"" + filepath + "\"");
        } else if (os.contains("linux")) {
            processBuilder.command("bash" , "-c", fpcalcPath + " -plain '" + filepath + "'");
        } else {
            System.out.println("Operative system is not supported");
        }

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );
        String line;
        while((line = reader.readLine()) != null) {
            output.append(line);
        }

        int exitVal = process.waitFor();
        switch (exitVal) {
        case 0:
            System.out.println("Success");
            return output.toString();
        default:
            System.out.println("Something went wrong");
            return null;
        }
    }


    public Music fetchMetadata(Music music) throws Exception {
        String musicPath = music.getFile().getPath();

        MusicInfo.Result.Record musicInfo = getMusicInfo(getFingerprint(musicPath), music.getTrackLength());

        music.setTitle(musicInfo.getTitle());
        //music.setArtist();
        //music.setAlbuns();

        System.out.println();

        //System.out.println(music.getArtists().get(0).getName());
        //System.out.println(music.getTitle());

        return music;
    }


}


