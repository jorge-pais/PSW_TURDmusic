import java.awt.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class Playlist{

    private String title;
    private Image playlistArt; // maybe?
    private Date dateCreated;

    private ArrayList<Music> tracklist;

    public Playlist(String title, ArrayList<Music> songsToAdd){
        this.title = title;
        tracklist = new ArrayList<>();
        if(songsToAdd != null)
            tracklist.addAll(songsToAdd);

        // Use the current system time
        this.dateCreated = Date.from(Instant.now());
    }

    public ArrayList<Music> getSongs(){ return tracklist; }
    public String getTitle(){ return title; }
    public Date getDateCreated(){ return dateCreated; }

}
