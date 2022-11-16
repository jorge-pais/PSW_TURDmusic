import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Album {

    private String title;
    private Image coverArt;
    private Date releaseDate;

    private ArrayList<Artist> artists;
    private ArrayList<Music> tracklist;

    public Album(String name, Artist artist){
        this.title = name;
        this.tracklist = new ArrayList<>();

        this.artists = new ArrayList<>();
        if (artist != null)
            this.setArtists(artists);
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists.addAll(artists);
    }

    public String getTitle(){
        return title;
    }
    public void addSong(Music song){
        tracklist.add(song);
    }
    public void removeSong(Music song){ tracklist.remove(song); }
    public ArrayList<Music> getAllSongs(){ return tracklist; }


    // UNTESTED FUNCTION
    public void orderTracklist(){
        Collections.sort(this.tracklist, new Comparator<Music>() {
            @Override
            public int compare(Music m1, Music m2) {
                int t1 = m1.getTrackNumber();
                int t2 = m2.getTrackNumber();
                if(t1 == t2) // this probably shouldn't happen
                    return 0;

                return t1 < t2 ? -1 : 1;
            }
        });
    }
}
