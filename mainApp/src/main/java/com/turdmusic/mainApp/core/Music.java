package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;

import java.io.File;

/*
    This class holds all the information of a song
    Also maintains all relationships with the respective Artists and Albums
*/
@JsonIdentityInfo(
        generator= ObjectIdGenerators.IntSequenceGenerator.class,
        property="@json_id")
public class Music {
    //
    // Now that we are able to save the library utilizing json, the field id
    // is not need, we'll keep it in case the UI/UX benefits from this
    //
    public int id;

    private String title;
    private File file;

    private Artist artist; // for now, we'll assume that each song has only one artist
    private Album album;
    private int trackNumber;
    private int trackLength;

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle(){ return title; }
    public File getFile(){ return file; }
    public Album getAlbum(){ return album; }
    public Artist getArtist(){ return artist; }
    public int getTrackNumber(){ return trackNumber; }
    @JsonIgnore
    public String getFormattedTrackLength(){
        return String.format("%d:%d", trackLength/60, trackLength%60);
    }
    public int getTrackLength(){ return trackLength; }

    public Music(){
        super();
    }

    public Music(String songTitle, int id, File fileHandle, Artist artist, Album album, int trackNumber){
        this.title = songTitle;
        this.file = fileHandle;
        this.id = id;
        if(artist == null || album == null || trackNumber == 0)
            return;

        this.artist = artist;
        this.album = album;
        this.trackNumber = trackNumber;

        // get the track's length
        try{
            AudioFile f = AudioFileIO.read(fileHandle);
            this.trackLength = f.getAudioHeader().getTrackLength();
        }catch (Exception e){
            this.trackLength = -1; // error
            e.printStackTrace();
        }
    }
}
