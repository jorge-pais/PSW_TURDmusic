package com.turdmusic.mainApp.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.turdmusic.mainApp.core.models.CoverInfo;
import com.turdmusic.mainApp.core.models.ImageInfo;
import com.turdmusic.mainApp.core.models.MusicInfo;
import com.google.gson.Gson;

import javax.imageio.ImageIO;


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
// TODO: Completar MusicInfo, ligacao ao cover...  .org/releasegroup/[id]
// TODO: Ver casos 302, nulls...
// TODO: Verificar se existe ligacao a internet
// TODO: Verificar todos os albuns do Nome musica maioritario
public class AcoustidRequester {

    // API key
    private static final String key = "PFjVWjJwPw";

    // API request parameters
    private static final String baseURL_acosticid = "https://api.acoustid.org/v2/lookup";
    private static final String baseURL_coverarch = "https://coverartarchive.org/release-group";
    // TODO: Have fpcalc inside the project
    private static final String fpcalcPath = "C:\\Users\\David\\Downloads\\fpcalc";
    private static final String os = System.getProperty("os.name").toLowerCase();



    // Possibly in a different class - with Token for Spotify
    private static String getAPIRequest(String url, String path) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url + path))
                .GET()
                .build();

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static String getFingerprint(String filepath) throws Exception{
        ProcessBuilder processBuilder = new ProcessBuilder();


        // Cross-platform compatibility
        if (os.contains("windows")) {
            processBuilder.command("cmd.exe", "/c", fpcalcPath + " -plain \"" + filepath + "\"");
        } else if (os.contains("linux")) {
            processBuilder.command("bash" , "-c", fpcalcPath + " -plain '" + filepath + "'");
        } else {
            System.out.println("Operative system is not supported");
            return null;
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

    public static List<String> getCoversURL(MusicInfo.Result.Record.ReleaseGroup releaseGroup) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Fetching Data...");
        String response = getAPIRequest(baseURL_coverarch, "/" + releaseGroup.getId());

        Gson gson = new Gson();
        CoverInfo covers = gson.fromJson(response, CoverInfo.class);
        System.out.println("Data Fetched...");

        //List<Value> list = new ArrayList<Value>(map.values());
        // TODO: Verify if it exists
        List<Map<String, String>> urlCover = covers.getImages().stream()
                .map(CoverInfo.Image::getThumbnails)
                .collect(Collectors.toList());

        List<String> totalURLsmall = urlCover.stream()
                .map(w->w.get("250"))
                .collect(Collectors.toList());

        // TODO: Add selection of one thumbnail (sizewise) from the map, if any. Return String
        //
        // (another function/method should use the link to download the image)

        return totalURLsmall;
    }
    private static ImageInfo downloadCover(String linkImage) throws Exception {
        // Chamar APIRequester
        // Exemplo do URL: "http://coverartarchive.org/release/a73ceee4-7992-4371-bbe1-5f2621a9742a/29884614890-250.jpg"
        URL url = new URL(linkImage);
        BufferedImage image = ImageIO.read(url);
        return (new ImageInfo(image, "teste"));
        // TODO: Understand the name to be given
    }

    // Maps json response to MusicInfo (java class)
    // Returned value has music, artist information
    public static List<MusicInfo.Result.Record> getMusicInfo(Music music) throws Exception {
        String musicPath = music.getFile().getPath();

        String fingerprint = getFingerprint(musicPath);
        int duration = music.getTrackLength();

        System.out.println("Fetching Data...");
        String response = getAPIRequest(baseURL_acosticid,"?client="+key+"&meta=recordings+compress+releasegroups&duration="+duration+"&fingerprint="+fingerprint);
        // TODO: return null if response is not ok

        Gson gson = new Gson();
        MusicInfo musicInfo = gson.fromJson(response, MusicInfo.class);

        if(!musicInfo.getStatus().equals("ok")){
            return null;
        }

        System.out.println("Data Fetched...");
        // Chooses the recording with the highest score
        double THRESHOLD = 0.6;
        MusicInfo.Result filteredResult = musicInfo.getResults().stream()
                .filter(e -> e.getScore() > THRESHOLD)
                .max((val1, val2) -> (int) (val1.getScore()*10000 - val2.getScore()*10000))
                .orElse(null);

        if(filteredResult==null){return null;}

        List<MusicInfo.Result.Record> filteredRecord = filteredResult.getRecordings();
        Map<String, Long> recordsName = filteredRecord.stream()
                .filter(record -> record.getTitle()!=null)
                .collect(Collectors.groupingBy(MusicInfo.Result.Record::getTitle, Collectors.counting()));

        String musicName = recordsName.entrySet().stream()
                .max(Map.Entry.comparingByValue()).get().getKey();

        // List of record
        List<MusicInfo.Result.Record> musicInformation = filteredRecord.stream()
                .filter(record -> record.getTitle()!=null && record.getTitle().equals(musicName))
                .collect(Collectors.toList());

        return musicInformation;
    }

}


