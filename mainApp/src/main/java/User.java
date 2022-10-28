import java.util.ArrayList;
import java.io.File;

public class User {

    // Library stuff
    private ArrayList<String> libraryFilePaths;
    private ArrayList<Music> songs;
    private ArrayList<Album> albums;
    private ArrayList<Artist> artists;

    public User() {
        libraryFilePaths = new ArrayList<>();
        songs = new ArrayList<>();
    }

    public void addPath(String path){
        libraryFilePaths.add(path);

        ArrayList<Music> scanResult = scanFilePath(path);
        if(scanResult != null)
            songs.addAll(scanResult);
    }

    public ArrayList<Music> getAllSongs(){
        return songs;
    }

    public void removePath(String path){
        libraryFilePaths.remove(path);

        // Remove all children music
        songs.removeIf(i -> i.getFile().getPath().startsWith(path));

    }

    public ArrayList<String> getPaths(){
        return libraryFilePaths;
    }

    private boolean checkFileExtension(String name){

        final String[] supportedExtensions = {"mp3", "ogg", "flac", "wav", "aif", "dsf", "wma", "mp4"};

        for (String i: supportedExtensions)
            if(name.endsWith(i))
                return true;

        return false;
    }

    // Search for music files in a given path
    public ArrayList<Music> scanFilePath(String path){

        File filePath = new File(path);
        File[] contents = filePath.listFiles();

        ArrayList<Music> musicList = new ArrayList<>();

        if(contents == null) return null;
        for(File file: contents) {
            if(!file.isFile())
                musicList.addAll(scanFilePath(file.getPath()));
                //scanFilePath(file.getPath());
            else
                if(checkFileExtension(file.getName()))
                    musicList.add(new Music(file.getName(), file));
        }

        return musicList;
    }
}
