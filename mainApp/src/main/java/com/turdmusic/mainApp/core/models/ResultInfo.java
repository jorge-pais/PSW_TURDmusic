package com.turdmusic.mainApp.core.models;

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
