import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

import java.util.ArrayList;
import java.io.File;

public class Music {

    private String title;
    private File file;

    private ArrayList<Artist> artists;
    private Album album;
    private int trackNumber;

    public String getTitle(){
        return title;
    }
    public File getFile(){
        return file;
    }

    public Music(String songTitle, File fileHandle){
        title = songTitle;
        file = fileHandle;
    }

    public void getSongAttribute() {
        try {
            //This doesn't work (?)
            AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();

            System.out.println("Album:" + tag.getFirst(FieldKey.ALBUM));
            System.out.println("Artist: " + tag.getFirst(FieldKey.ARTIST));
            System.out.println("Title: "+ tag.getFirst(FieldKey.TITLE));

        } catch (Exception e){
            System.out.println(e);
        }
    }
}
