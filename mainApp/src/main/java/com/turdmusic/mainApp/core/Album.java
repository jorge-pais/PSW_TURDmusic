package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ExecutionException;

//
//  Album Class
//  Holds all album data and relations with their respective songs and artists
//

@JsonIdentityInfo(
        generator= ObjectIdGenerators.IntSequenceGenerator.class,
        property="@json_id")
public class Album {
    public int id;

    private String title;
    private Image coverArt;
    private Date releaseDate; // Maybe just save the year, many albums do not include the actual release date

    private ArrayList<Artist> artists;
    private ArrayList<Music> tracklist;

    public Album(){
        super();
    }

    public Album(String name, Artist artist, int id){
        this.title = name;
        this.id = id;
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
    public ArrayList<Music> getTracklist(){ return tracklist; }

    //
    // UNTESTED FUNCTION
    // This function is to address a problem with song scanning on linux
    // on Windows this does not seem to be a problem
    //
    public void orderTracklist(){
        this.tracklist.sort((m1, m2) -> {
            int t1 = m1.getTrackNumber();
            int t2 = m2.getTrackNumber();
            if (t1 == t2) // this probably shouldn't happen
                return 0;

            return t1 < t2 ? -1 : 1;
        });
    }

    // TODO: FINISH THIS!
    public void findAlbumCover(){
        // try to search the song files for artwork data
        try{
            AudioFile fileIn = AudioFileIO.read(tracklist.get(0).getFile());
            Tag tag = fileIn.getTag();

            Artwork art = tag.getFirstArtwork();

            if(art != null){ //
                //BufferedImage image = art.getImage();
            }
        } catch (Exception e){
            System.out.println("Error trying to read artwork for album");
            e.printStackTrace();
        }
    }
}
