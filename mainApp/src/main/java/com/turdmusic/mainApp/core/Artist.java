package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.awt.*;
import java.util.ArrayList;

// Maybe change this to a UUID object identifierChanges merged into devel
@JsonIdentityInfo(
        generator= ObjectIdGenerators.IntSequenceGenerator.class,
        property="@json_id")
public class Artist {

    public int id;
    private Image picture;
    private String name;
    private ArrayList<Music> songs;
    private ArrayList<Album> albums;

    public Artist(){
        super();
    }

    public Artist(String name, int id){
        this.name = name;
        this.id = id;

        songs = new ArrayList<>();
        albums = new ArrayList<>();
    }

    public String getName(){
        return name;
    }
    public Image getPicture(){
        return picture;
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

    public void setPicture(Image image){
        this.picture = image;
    }
}
