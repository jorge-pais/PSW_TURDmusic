import java.util.ArrayList;
import java.util.Date;

public class Album {

    private String title;
    private String coverArt;
    private Date releaseDate;

    private ArrayList<Artist> artists;
    private ArrayList<Music> tracklist;

    public Album(String name, Artist artist){
        tracklist = new ArrayList<>();
        artists = new ArrayList<>();
    }

}
