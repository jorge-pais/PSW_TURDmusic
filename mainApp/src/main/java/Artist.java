import java.util.ArrayList;

public class Artist {

    private String name;
    private String picture; // path to file

    private ArrayList<Music> songs;
    private ArrayList<Album> albums;

    public Artist(){
        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    public String getName(){
        return name;
    }

}
