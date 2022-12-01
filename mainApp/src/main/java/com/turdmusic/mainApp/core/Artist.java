package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.turdmusic.mainApp.MainGUI;
import com.turdmusic.mainApp.SongController;
import com.turdmusic.mainApp.core.models.ImageInfo;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@JsonIdentityInfo(
        generator= ObjectIdGenerators.IntSequenceGenerator.class,
        property="@json_id")
public class Artist {

    public int id;
    private ImageInfo picture;
    private String name;
    private ArrayList<Music> songs;
    private ArrayList<Album> albums;

    public Artist(){
        super();
    }

    public Artist(String name, int id){
        this.name = name;
        this.id = id;
        this.picture = null;

        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    public String getName(){
        return name;
    }
    public ImageInfo getImageInfo(){ return this.picture; }
    @JsonIgnore
    public Image getPicture() throws IOException {
        if(picture != null)
            return picture.getImageObj();
        else
            return new Image(getClass().getResourceAsStream("/com/turdmusic/mainApp/defaultphotos/artist_default.png"));
    }

    public void addSong(Music song){
        songs.add(song);
    }
    public void removeSong(Music song){songs.remove(song);}

    public ArrayList<Album> getAlbums(){
        return albums;
    }
    public ArrayList<Music> getSongs(){return songs;}

    public void addAlbum(Album album){
        albums.add(album);
    }
    public void removeAlbum(Album album){ albums.remove(album); }

    public void setPicture(BufferedImage image){
        try {
            this.picture = new ImageInfo(image, "artist_" + id);
        }catch (Exception e){
            System.out.println("Error setting artist picture");
            e.printStackTrace();
        }
    }
}
