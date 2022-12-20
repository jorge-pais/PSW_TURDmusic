package com.turdmusic.mainApp.core.models;

/**
 * Class that holds the data structure to perform the
 * translation between the MusicInfo json response
 * and the results that are actually presented on the table
 * */
public class ResultInfo {

    public String artist;
    public String album;
    public String title;

    public MusicInfo.Result.Record.ReleaseGroup releaseGroup;

    public ResultInfo(String artist, String album, String title, MusicInfo.Result.Record.ReleaseGroup releaseGroup){
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.releaseGroup = releaseGroup;
    }
}
