import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.util.ArrayList;

public class Artist{

    private String name;
    //private Image picture;

    @JsonBackReference
    private ArrayList<Music> songs;
    @JsonBackReference
    private ArrayList<Album> albums;

    public Artist(String name){
        this.name = name;

        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    public String getName(){
        return name;
    }

    public void addSong(Music song){
        songs.add(song);
    }
    public void removeSong(Music song){songs.remove(song);}

    public ArrayList<Album> getAllAlbums(){
        return albums;
    }
    public ArrayList<Music> getAllSongs(){return songs;}

    public void addAlbum(Album album){
        albums.add(album);
    }
    public void removeAlbum(Album album){ albums.remove(album); }

}
