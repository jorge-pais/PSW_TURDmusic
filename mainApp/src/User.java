import java.util.ArrayList;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {

    private ArrayList<String> libraryFilePaths;
    private ArrayList<Music> songs;
    public String defaultMediaPlayerExecutable;

    public void addPath(String path){
        libraryFilePaths.add(path);
    }

    public void removePath(String path){
        libraryFilePaths.remove(path);
    }

    public void changeMediaPlayer(String newExecutable){
        defaultMediaPlayerExecutable = newExecutable;
    }

    private final String[] supportedExtensions = {"mp3", "mp4","ogg", "flac", "wav", "aif", "dsf", "wma"};

    // Search for songs in a given path, is recursive
    public void scanFilePath(String path){
        File filePath = new File(path);
        File[] contents = filePath.listFiles();
        for(File file: contents) {
            if(!file.isFile()){
                scanFilePath(file.getPath());
            }
            else{
                if(file.getName().endsWith(".mp3"))
                    System.out.println(file.getName());
                Music song = new Music(file.getName(), file);
                songs.add(song);

            }
        }
    }
}
