import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;

public class Music {

    private String title;
    private File file;

    private boolean undefined = true;
    private Artist artist; // for now, we'll assume that each song has only one artist
    private Album album;
    private int trackNumber;

    public String getTitle(){
        return title;
    }
    public File getFile(){
        return file;
    }
    public Album getAlbum(){ return album; }
    public Artist getArtist(){ return artist; }
    public int getTrackNumber(){ return trackNumber; }

    public Music(String songTitle, File fileHandle, Artist artist, Album album, int trackNumber){
        this.title = songTitle;
        this.file = fileHandle;
        if(artist == null || album == null || trackNumber == 0)
            return;

        this.artist = artist;
        this.album = album;
        this.trackNumber = trackNumber;
    }

    public void getSongAttribute() {
        try {
            AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();

                System.out.println("Album: " + tag.getFirst(FieldKey.ALBUM));
                System.out.println("Artist: " + tag.getFirst(FieldKey.ARTIST));
                System.out.println("Title: "+ tag.getFirst(FieldKey.TITLE));

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
