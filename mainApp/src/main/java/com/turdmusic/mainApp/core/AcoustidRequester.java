package com.turdmusic.mainApp.core;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
// https://stackoverflow.com/questions/1383536/including-an-exe-file-to-jar
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
            System.out.println("Success");
            return output.toString();
        }else{
                System.out.println("Something went wrong");
                return null;
        }
    }

    // returns: List of all covers url available for an album
    public static List<String> getCoversURL(MusicInfo.Result.Record.ReleaseGroup releaseGroup) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Fetching Data...");
        String response = getAPIRequest(baseURL_coverarch, "/" + releaseGroup.getId());

        Gson gson = new Gson();
        CoverInfo covers = gson.fromJson(response, CoverInfo.class);
        System.out.println("Data Fetched...");

        List<Map<String, String>> urlCover = covers.getImages().stream()
                .map(CoverInfo.Image::getThumbnails)
                .collect(Collectors.toList());

        List<String> totalURLsmall = urlCover.stream()
                .map(w->w.get("250"))
                .collect(Collectors.toList());

        return totalURLsmall;
    }

    // Used to download selected cover
    public static ImageInfo downloadCover(String linkImage, String nameImage) throws Exception {
        URL url = new URL(linkImage);
        BufferedImage image = ImageIO.read(url);
        return (new ImageInfo(image, nameImage));
    }

    public static File temporaryCover(List<String> linkImage, String nameFile) throws Exception {
        File tempFile = File.createTempFile(nameFile, ".tmp");
        for (String s : linkImage) {
            URL url = new URL(s);
            BufferedImage image = ImageIO.read(url);
            ImageIO.write(image, "jpg", tempFile);
        }

        return (tempFile);
    }

    // Maps json response to MusicInfo (java class)
    // Returned value has music, artist information
    public static List<MusicInfo.Result.Record> getMusicInfo(Music music) throws Exception {
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


