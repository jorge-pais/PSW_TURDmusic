package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.turdmusic.mainApp.core.models.ImageInfo;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@JsonIdentityInfo(
        generator= ObjectIdGenerators.IntSequenceGenerator.class,
        property="@json_id")
public class Playlist{
    public int id;

    private String title;
    private ImageInfo imageInfo;
    private Date dateCreated;

    private ArrayList<Music> tracklist;

    public Playlist(){
        super();
    }

    public Playlist(String title, ArrayList<Music> songsToAdd){
        this.title = title;
        tracklist = new ArrayList<>();
        if(songsToAdd != null)
            tracklist.addAll(songsToAdd);

        // Use the current system time
        this.dateCreated = Date.from(Instant.now());
    }

    public ArrayList<Music> getTracklist(){ return tracklist; }
    public String getTitle(){ return title; }
    public Date getDateCreated(){ return dateCreated; }
    @JsonIgnore
    public void setPicture(BufferedImage image){
        if(image == null) {
            imageInfo = null;
            return;
        }
        try {
            this.imageInfo = new ImageInfo(image, "album_" + id);
        }catch (Exception e){
            System.out.println("Error setting coverArt");
            e.printStackTrace();
        }
    }
    @JsonIgnore
    public Image getPlaylistPicture(){
        if(imageInfo != null)
            return this.imageInfo.getImageObj();
        else {
            // TODO Change this to a default playlist picture
            InputStream imageStream = getClass().getResourceAsStream("/com/turdmusic/mainApp/defaultphotos/album_default.png");
            assert imageStream != null;
            return new Image(imageStream);
        }
    }

    public ImageInfo getImageInfo(){ return this.imageInfo; }
    public void setImageInfo(ImageInfo imageInfo){ this.imageInfo = imageInfo; }
}
