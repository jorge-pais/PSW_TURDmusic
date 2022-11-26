package com.turdmusic.mainApp.p2p;

import com.turdmusic.mainApp.core.Music;

import java.io.File;


public class MusicShare {

    public String title;
    public String artist;
    public String album;
    public File file;

    public MusicShare(Music source){
        this.file = source.getFile();
        this.title = source.getTitle();
        this.artist = source.getArtist().getName();
        this.album = source.getAlbum().getTitle();


    }
}
