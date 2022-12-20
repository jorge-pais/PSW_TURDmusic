package com.turdmusic.mainApp.core.models;

import java.util.List;
import java.util.Map;

/** Class with music metadata retrieved from Cover Archive
 * Used for interfacing with the json responses
 * */
public class CoverInfo {
    public List<Image> images;

    @Override
    public String toString() {
        return "CoverInfo{" +
                "images=" + images +
                '}';
    }

    public List<Image> getImages() {
        return images;
    }
    public void setImages(List<Image> images) {
        this.images = images;
    }

    public class Image{
        public Map<String, String> thumbnails;


        @Override
        public String toString() {
            return "Image{" +
                    "thumbnails='" + thumbnails + '\'' +
                    '}';
        }

        public Map<String, String> getThumbnails() {
            return thumbnails;
        }
        public void setThumbnails(Map<String, String> thumbnails) {
            this.thumbnails = thumbnails;
        }



    }
}
