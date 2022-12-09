package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.turdmusic.mainApp.core.models.ImageInfo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@JsonIdentityInfo(
        generator= ObjectIdGenerators.IntSequenceGenerator.class,
        property="@json_id")
public class Playlist{
    public int id;

    private String title;
    private ImageInfo playlistArt;
    private Date dateCreated;

    private ArrayList<Music> tracklist;

    public Playlist(){
        super();
    }

    public Playlist(String title, ArrayList<Music> songsToAdd){
        this.title = title;
        //this.id = id;
        tracklist = new ArrayList<>();
        if(songsToAdd != null)
            tracklist.addAll(songsToAdd);

        // Use the current system time
        this.dateCreated = Date.from(Instant.now());
    }

    public ArrayList<Music> getTracklist(){ return tracklist; }
    public String getTitle(){ return title; }
    public Date getDateCreated(){ return dateCreated; }
}
