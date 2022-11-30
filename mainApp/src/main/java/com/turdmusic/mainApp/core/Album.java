package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.turdmusic.mainApp.core.models.ImageInfo;
import javafx.scene.image.Image;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.Images;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import javafx.embed.swing.SwingFXUtils;

/**
    Album Class which holds all album data and relations with their respective songs and artists
*/
@JsonIdentityInfo(
        generator= ObjectIdGenerators.IntSequenceGenerator.class,
        property="@json_id")
public class Album {
    public int id;

    private String title;
    private ImageInfo coverArt;
    private Date releaseDate; // Maybe just save the year, many albums do not include the actual release date

    private Artist artist;
    private ArrayList<Music> tracklist;

    public Album(){
        super();
    }

    public Album(String name, Artist artist, int id){
        this.title = name;
        this.id = id;
        this.tracklist = new ArrayList<>();
        this.artist = artist;
    }

    //public void setArtists(Artist artist) { this.artist = artist; }
    public Artist getArtist(){ return this.artist; }
    public String getTitle(){
        return title;
    }
    public void addSong(Music song){
        tracklist.add(song);
    }
    public void removeSong(Music song){ tracklist.remove(song); }
    public ArrayList<Music> getTracklist(){ return tracklist; }
    public void setCoverArt(BufferedImage image){
        try {
            this.coverArt = new ImageInfo(image, "album_" + id);
        }catch (Exception e){
            System.out.println("Error setting coverArt");
            e.printStackTrace();
        }
    }
    @JsonIgnore
    public Image getCoverArt(){ return this.coverArt.getImageObj();}
    public ImageInfo getImageInfo(){ return this.coverArt; }

    /**
        This function sorts the tracklist array
    */
    public void sortTrackList(){
        this.tracklist.sort((m1, m2) -> {
            int t1 = m1.getTrackNumber();
            int t2 = m2.getTrackNumber();
            if (t1 == t2) // this probably shouldn't happen
                return 0;

            return t1 < t2 ? -1 : 1;
        });
    }

    /**
        This function will look for the cover art within the
        first song's parent directory, or if there is no image file
        it tries to read from the song metadata
    */
    public void findAlbumCover(){
        try { // First check all children files for any pictures
            File folder = new File(tracklist.get(0).getFile().getParent());

            for (File child : folder.listFiles()) {
                if (Utils.checkFileExtension(child.getName(), Utils.fileType.Image)) {
                    BufferedImage image = ImageIO.read(child);

                    int h = image.getHeight(), w = image.getWidth();
                    if (Math.abs((float) (w - h) / w) <= 0.02) { // If the image is at least "98% square"
                        this.coverArt = new ImageInfo(child);
                        return;
                    }
                }
            }

            System.out.println("No album art files found for the album");

            AudioFile fileIn = AudioFileIO.read(tracklist.get(0).getFile());
            Tag tag = fileIn.getTag();

            Artwork art = tag.getFirstArtwork();
            BufferedImage image = Images.getImage(art);

            assert image != null;
            setCoverArt(image);

        }catch (Exception e){
            System.out.println("Something went wrong when fetching the images");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
