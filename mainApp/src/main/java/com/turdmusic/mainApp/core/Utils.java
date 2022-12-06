package com.turdmusic.mainApp.core;

// Holds utility methods used by the core library classes
public class Utils {

    // Types of files to check
    public enum fileType {Audio, Image};

    public static boolean checkFileExtension(String name, fileType type){
        String[] supportedExtensions;

        switch (type) {
            case Audio:
                supportedExtensions = new String[]{"mp3", "ogg", "flac", "wav", "aif", "dsf", "wma", "mp4"};
                break;
            case Image:
                supportedExtensions = new String[]{"bmp", "gif", "jpeg", "jpg", "png"};
                break;
            default:
                return false;
        }
        for (String i: supportedExtensions)
            if(name.endsWith(i))
                return true;
        return false;
    }
}
