package com.turdmusic.mainApp.core;

import java.util.prefs.Preferences;

// This class is responsible to hold the application's preferences
public class Settings {

    public Preferences prefs;
    private final String savePath = "savePath"; // Folder path to where to store the library
    private final String mediaPlayerExecutable = "mediaPlayerExecutable";
    private final String fpcalcExecutable = "fpcalcExecutable";
    private final String firstLaunch = "firstLaunch";

    public Settings(){
        this.prefs = Preferences.userRoot().node(this.getClass().getName());
        if(prefs.getBoolean(firstLaunch, true)) // if there aren't any settings
            defaultSettings();
    }

    public String getMediaPlayerExecutable(){
        return prefs.get(mediaPlayerExecutable, null);
    }
    public boolean getFirstLaunch(){
        return prefs.getBoolean(firstLaunch, true);
    }
    public String getSavePath(){
        return prefs.get(savePath, savePath);
    }
    public String getFpcalcExecutable(){
        return prefs.get(fpcalcExecutable, fpcalcExecutable);
    }

    public void setMediaPlayerExecutable(String executable){
        prefs.put(mediaPlayerExecutable, executable);
    }
    public void setFirstLaunch(boolean first){
        prefs.putBoolean(firstLaunch, first);
    }
    public void setSavePath(String path){
        prefs.put(savePath, path);
    }
    public void setFpcalcExecutable(String executable){
        prefs.put(fpcalcExecutable, executable);
    }

    private void defaultSettings(){
        // This seems to work for all tested Operating Systems
        String osName = System.getProperty("os.name").toLowerCase();
        String homeFolder = System.getProperty("user.home");

        if(osName.startsWith("windows")) {
            System.out.println("is this even being executed?");
            prefs.put(savePath, homeFolder + "\\Music\\TURDmusic");
            prefs.put(mediaPlayerExecutable, "C:\\Program Files\\VideoLAN\\VLC\\vlc.exe");
        }
        else if (osName.contains("linux")) {
            prefs.put(savePath, homeFolder + "/.turd");
            prefs.put(mediaPlayerExecutable, "/usr/bin/vlc");
            prefs.put(fpcalcExecutable, "/usr/bin/fpcalc");
        }
    }
}
