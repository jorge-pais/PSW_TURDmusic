import java.util.ArrayList;
import java.io.File;

public class Music {

    public String name;
    public File file;

    public ArrayList<Artist> artists;
    public Album album;

    public Music(String songName, File fileHandle){
        name = songName;
        file = fileHandle;
    }



}
