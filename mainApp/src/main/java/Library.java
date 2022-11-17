import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.File;

public class Library{

    // Library stuff
    private ArrayList<String> libraryFilePaths; // We can change the library paths from strings to file objects
    private ArrayList<Music> songs;
    private ArrayList<Album> albums;
    private ArrayList<Artist> artists;
    private ArrayList<Playlist> playlists;

    private Album undefinedAlbum;
    private Artist undefinedArtist;

    public Library() {
        // Initialize all lists
        this.libraryFilePaths = new ArrayList<>();
        this.songs = new ArrayList<>();
        this.artists = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.undefinedAlbum = null;
        this.undefinedArtist = null;
    }

    public void addPath(String path){
        libraryFilePaths.add(path);

        ArrayList<Music> scanResult = scanFilePath(path);
        if(scanResult != null)
            songs.addAll(scanResult);

        System.out.println("Path added!");
    }

    public ArrayList<Music> getAllSongs(){
        return songs;
    }
    public ArrayList<String> getPaths(){ return libraryFilePaths; }
    public ArrayList<Artist> getArtists(){ return artists; }
    public ArrayList<Album> getAlbums(){ return albums; }
    public ArrayList<Playlist> getPlaylists(){ return playlists; }

    public void removePath(String path){
        libraryFilePaths.remove(path);

        // Remove all children music
        // We can't do this with an iterator since we're removing elements
        // and that throws ConcurrentModificationException
        for (int i = 0; i < songs.size(); i++){
            if(songs.get(i).getFile().getPath().startsWith(path)){
                removeSong(songs.get(i));
                i--;
            }
        }
    }

    // Minutos depois de escrever esta função percebi que isto
    // era um user story do renato, sry
    public void removeSong(Music song){
        Artist artist = song.getArtist();
        Album album = song.getAlbum();
        album.removeSong(song);
        artist.removeSong(song);

        //
        // Remove any empty artists and albums; The java garbage
        // collector should take care of cleaning up memory (hopefully)
        //
        if(album.getAllSongs().size() == 0) {
            artist.removeAlbum(album);
            this.albums.remove(album);
        }
        if(artist.getAllSongs().size() == 0){
            this.artists.remove(artist);
        }

        this.songs.remove(song);
    }

    // coise?
    public void removePlaylist(Playlist list){
        this.playlists.remove(list);
    }

    private boolean checkFileExtension(String name){

        final String[] supportedExtensions = {"mp3", "ogg", "flac", "wav", "aif", "dsf", "wma", "mp4"};

        for (String i: supportedExtensions)
            if(name.endsWith(i))
                return true;

        return false;
    }
    //
    // Search for music files in a given path and create artist and album
    // entries for each file
    //
    public ArrayList<Music> scanFilePath(String path){

        File filePath = new File(path);
        File[] contents = filePath.listFiles();

        ArrayList<Music> musicList = new ArrayList<>();

        if(contents == null) return null;
        for(File file: contents) {
            // check children folders recursively
            if(!file.isFile())
                musicList.addAll(scanFilePath(file.getPath()));
            else
            if(checkFileExtension(file.getName())) { // check for a valid music file
                Music song;

                try { // this is where we get the metadata
                    song = createSongMetadata(file);
                } catch (Exception e){
                    //e.printStackTrace();

                    // if the function call fails we can assume that no metadata is defined for
                    System.out.println("Error getting metadata");
                    if(undefinedArtist == null && undefinedAlbum == null){
                        undefinedArtist = new Artist("Undefined");
                        undefinedAlbum = new Album("Undefined", undefinedArtist);
                        undefinedArtist.addAlbum(undefinedAlbum);
                        this.artists.add(undefinedArtist);
                        this.albums.add(undefinedAlbum);
                    }
                    int track = undefinedAlbum.getAllSongs().size() + 1;
                    song = new Music(file.getName(), file, undefinedArtist, undefinedAlbum, track);
                }

                musicList.add(song);
            }
        }

        return musicList;
    }

    //
    // This function gets the metadata from a file and returns null if
    // the file does not contain metadata this PERHAPS SHOULD throw some exception
    //
    private Music createSongMetadata(File fileHandle) throws Exception{
        AudioFile f = AudioFileIO.read(fileHandle);
        Tag tag = f.getTag();

        String artistName = tag.getFirst(FieldKey.ARTIST);
        Artist artist = null;
        String albumTitle = tag.getFirst(FieldKey.ALBUM);
        Album album = null;
        String trackTitle = tag.getFirst(FieldKey.TITLE);
        String trackNumber = tag.getFirst(FieldKey.TRACK);

        for (Artist i : this.artists)
            if (i.getName().equals(artistName))
                artist = i;
        if(artist == null){
            artist = new Artist(artistName);
            this.artists.add(artist);
        }

        for (Album i : this.albums)
            if(i.getTitle().equals(albumTitle))
                album = i;
        if(album == null){
            album = new Album(albumTitle, artist);
            artist.addAlbum(album);
            this.albums.add(album);
        }

        Music song = new Music(trackTitle, fileHandle, artist, album, Integer.parseInt(trackNumber));
        artist.addSong(song);
        album.addSong(song);
        return song;
    }

    // This needs to be done
    public void saveLibrary(){}
}
