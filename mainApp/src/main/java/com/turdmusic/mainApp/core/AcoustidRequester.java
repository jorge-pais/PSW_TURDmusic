package com.turdmusic.mainApp.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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
public class AcoustidRequester {

    public static Settings settings;

    // API key
    private static final String key = "PFjVWjJwPw";

    // API request parameters
    private static final String baseURL_acosticid = "https://api.acoustid.org/v2/lookup";
    private static final String baseURL_coverarch = "https://coverartarchive.org/release-group";
    private static final String os = System.getProperty("os.name").toLowerCase();

    // Possibly in a different class - with Token for Spotify
    private static String getAPIRequest(String url, String path) throws URISyntaxException, IOException, InterruptedException {
        if(url == null || path==null){return null;}
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url + path))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(!(response.statusCode() == 200 || response.statusCode() == 301 || response.statusCode() == 302)){return null;}

        return response.body();
    }

    private static String getFingerprint(String filepath) throws Exception{
        if(filepath == null){return null;}
        ProcessBuilder processBuilder = new ProcessBuilder();

        String fpcalcPath = settings.getFpcalcExecutable();

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
        if (exitVal == 0) {
            return output.toString();
        }
        return null;
    }

    // returns: List of all covers url available for an album
    public static List<String> getCoversURL(MusicInfo.Result.Record.ReleaseGroup releaseGroup) throws URISyntaxException, IOException, InterruptedException {
        if(releaseGroup == null){return null;}
        System.out.println("Fetching Data...");
        String response = getAPIRequest(baseURL_coverarch, "/" + releaseGroup.getId());

        Gson gson = new Gson();
        CoverInfo covers = gson.fromJson(response, CoverInfo.class);

        if(covers == null){return null;}

        System.out.println("Data Fetched...");

        List<Map<String, String>> urlCover = covers.getImages().stream()
                .map(CoverInfo.Image::getThumbnails)
                .collect(Collectors.toList());

        List<String> totalURLsmall = urlCover.stream()
                .map(w->w.get("250"))
                .collect(Collectors.toList());

        if(totalURLsmall.size() == 0){return null;}

        return totalURLsmall;
    }

    // Used to download selected cover
    public static ImageInfo downloadCover(String linkImage, String nameImage) throws Exception {
        if(linkImage==null || nameImage==null){return null;}
        URL url = new URL(linkImage);
        BufferedImage image = ImageIO.read(url);
        return (new ImageInfo(image, nameImage));
    }

    // Maps json response to MusicInfo (java class)
    // Returned value has music, artist information
    public static List<MusicInfo.Result.Record> getMusicInfo(Music music) throws Exception {
        if(music == null){return null;}
        String musicPath = music.getFile().getPath();

        String fingerprint = getFingerprint(musicPath);
        int duration = music.getTrackLength();

        System.out.println("Fetching Data...");
        String response = getAPIRequest(baseURL_acosticid,"?client="+key+"&meta=recordings+compress+releasegroups&duration="+duration+"&fingerprint="+fingerprint);

        Gson gson = new Gson();
        MusicInfo musicInfo = gson.fromJson(response, MusicInfo.class);

        if(!musicInfo.getStatus().equals("ok")){
            return null;
        }

        System.out.println("Data Fetched...");
        // Chooses the recording with the highest score
        MusicInfo.Result filteredResult = musicInfo.getResults().stream()
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


