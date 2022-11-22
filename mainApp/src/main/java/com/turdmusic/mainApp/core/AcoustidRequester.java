package com.turdmusic.mainApp.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//
// Resources for implementing this:
//      https://acoustid.org/webservice
// The acoustid web service can utilize fpcalc fingerprints to
// match songs with ones from the musicbrainz database
//      https://musicbrainz.org/doc/MusicBrainz_API
// The resulting ID's can then be queried to obtain the metadata in
//
// TODO: implement the function prototypes (easier said than done)
// TODO: Verify if the user has the fpcalc application installed
//

public class AcoustidRequester {

    // API key
    public final String key = "PFjVWjJwPw";

    // API request parameters
    public final String baseURL = "https://api.acoustid.org/v2/lookup";
    public final String format = "json";

    public String getFingerprint(Music music, String fpcalcPath) throws Exception{

        ProcessBuilder processBuilder = new ProcessBuilder();

        // UNIX shell command assuming a bash shell environment
        // Utilizing -plain for only outputting the actual fingerprint, flag might be different in windows
        processBuilder.command("bash" , "-c", fpcalcPath + " -plain '" + music.getFile().getPath()+ "'");

        // Windows (I dunno how fpcalc works on Windows)
        // processBuilder.command("cmd.exe /c" + fpcalcPath + " " + music.getFile().getPath());

        String result;

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );
        String line;
        while((line = reader.readLine()) != null)
            output.append(line+"\n");

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

    // Find out the song's ID through Musicbrainz
    public void getMusicbrainzID(String fingerprint){

    }

    // Query musicbrainz API
    public void getMetadata(String MBID){

    }
}


