package com.turdmusic.mainApp.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.util.ArrayList;
import java.io.File;

/**
    Library class which houses all saved music, album, artist and playlists
    objects
*/
public class Library{

    // We can change the library paths from strings to file objects
    private final ArrayList<String> libraryFilePaths;
    private final ArrayList<Music> songs;
    private final ArrayList<Album> albums;
    private final ArrayList<Artist> artists;
    private final ArrayList<Playlist> playlists;

    private Album undefinedAlbum = null;
    private Artist undefinedArtist = null;

    public static Settings settings;

    public ArrayList<Music> getSongs(){
        return songs;
    }
    public ArrayList<String> getLibraryPaths(){ return libraryFilePaths; }
    public ArrayList<Artist> getArtists(){ return artists; }
    public ArrayList<Album> getAlbums(){ return albums; }
    public ArrayList<Playlist> getPlaylists(){ return playlists; }
    public Album getUndefinedAlbum(){ return undefinedAlbum; }
    public Artist getUndefinedArtist(){ return undefinedArtist; }

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

    /** Adds a given file path to the library and creates all the appropriate
     * artist/album/music structures
     * @param path Folder path to be added
     * */
    public void addPath(String path){
        if(this.libraryFilePaths.contains(path))
            return;
        libraryFilePaths.add(path);

        ArrayList<Music> scanResult = scanFilePath(path, this.songs.size());
        if(scanResult != null)
            songs.addAll(scanResult);

        // Update the album listings
        for (Album i: this.albums) {
            i.findAlbumCover();
            i.sortTrackList();
        }

        System.out.println("Path added!");
    }
    /** Removes all music that are contained within a given path
     * @param path Path to be removed
     * */
    public void removePath(String path){
        if(!libraryFilePaths.contains(path)) return;
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
    /** Removes a given music reference from the library
     * @param song Music object to be removed
     * */
    public void removeSong(Music song){
        Artist artist = song.getArtist();
        Album album = song.getAlbum();
        album.removeSong(song);
        artist.removeSong(song);

        /*
        Remove any empty artists and albums; The java garbage
        collector should take care of cleaning up memory (hopefully)
        */
        if(album.getTracklist().size() == 0) {
            artist.removeAlbum(album);
            for (int i = this.albums.indexOf(album)+1; i < this.albums.size(); i++)
                this.albums.get(i).id--;

            this.albums.remove(album);
            if(album.equals(undefinedAlbum))
                undefinedAlbum = null;
        }
        if(artist.getSongs().size() == 0){
            for (int i = this.artists.indexOf(artist); i < this.artists.size(); i++)
                this.artists.get(i).id--;

            this.artists.remove(artist);
            if(artist.equals(undefinedArtist))
                undefinedArtist = null;
        }

        // Decrement all the songs with an id above that of the current song
        for (int i = this.songs.indexOf(song)+1; i < this.songs.size(); i++)
            this.songs.get(i).id--;

        this.songs.remove(song);
    }

    /** Scans a given file path for music files, checking sub-folders
     * also.
     * @param path Path to be scanned
     * @param startId Integer id reference to begin referencing
     * @return Array containing all the scanned music objects
     */
    private ArrayList<Music>scanFilePath(String path, int startId){
        //
        // Search for music files in a given path and create artist and album
        // entries for each file
        //
        File filePath = new File(path);
        File[] contents = filePath.listFiles();

        ArrayList<Music> musicList = new ArrayList<>();

        int songsAdded = 0; // major spaghetti code due to recursion

        if(contents == null) return null;
        for(File file: contents) {
            // check children folders recursively
            if(!file.isFile()) {
                ArrayList<Music> scanResult = scanFilePath(file.getPath(), (startId + songsAdded));
                assert scanResult != null;

                musicList.addAll(scanResult);
                songsAdded += scanResult.size();

            }
            else {
                if (Utils.checkFileExtension(file.getName(), Utils.fileType.Audio)) { // check for a valid music file
                    for (Music i : this.songs) // Prevent songs from being added twice
                        if (file.getPath().equals(i.getFile().getPath()))
                            return null;
                    Music song;

                    try { // this is where we get the metadata
                        song = readSongMetadata(file, songsAdded + startId);
                    } catch (Exception e) {
                        // if the function call fails we can assume that no metadata is defined in the file
                        //System.out.println("Error getting metadata, using undefined parameters");
                        if (undefinedArtist == null && undefinedAlbum == null) {
                            undefinedArtist = new Artist("Undefined", this.artists.size());
                            undefinedAlbum = new Album("Undefined", undefinedArtist, this.albums.size());
                            undefinedArtist.addAlbum(undefinedAlbum);
                            this.artists.add(undefinedArtist);
                            this.albums.add(undefinedAlbum);
                        }
                        int track = undefinedAlbum.getTracklist().size() + 1;
                        song = new Music(file.getName(), this.songs.size(), file, undefinedArtist, undefinedAlbum, track);
                        undefinedArtist.getSongs().add(song);
                        undefinedAlbum.getTracklist().add(song);
                    }
                    songsAdded++;
                    musicList.add(song);
                }
            }
        }

        return musicList;
    }
    /** Reads the metadata on a given music file and creates the
     * appropriate objects. If either the title, artist or album are
     * missing then an Exception is thrown
     * @param fileHandle Path to the song file
     * @param id Integer id for the song
     * @return Music object with the appropriate artist/album relations
     */
    private Music readSongMetadata(File fileHandle, int id) throws Exception{
        //
        // This function gets the metadata from a file and creates
        // artist and album objects for the song, or adds the song
        // to an existing album of artist
        //
        AudioFile f = AudioFileIO.read(fileHandle);
        Tag tag = f.getTag();

        // regex used to remove whitespace at the end of string
        String artistName = tag.getFirst(FieldKey.ARTIST).replaceFirst("\\s++$", "");
        String albumTitle = tag.getFirst(FieldKey.ALBUM).replaceFirst("\\s++$", "");
        String trackTitle = tag.getFirst(FieldKey.TITLE).replaceFirst("\\s++$", "");
        String trackNumber = tag.getFirst(FieldKey.TRACK).replaceFirst("\\s++$", "");

        Album album = null;
        Artist artist = null;

        // If anything of these fields are missing the undefined
        // artist and albums will be assigned
        if (artistName.length() == 0 || albumTitle.length() == 0 || trackTitle.length() == 0)
            throw new Exception();

        for (Artist i : this.artists)
            if (i.getName().equals(artistName))
                artist = i;
        if(artist == null){
            artist = new Artist(artistName, this.artists.size());
            this.artists.add(artist);
        }

        for (Album i : this.albums)
            if(i.getTitle().equals(albumTitle))
                album = i;
        if(album == null){
            album = new Album(albumTitle, artist, this.albums.size());
            artist.addAlbum(album);
            this.albums.add(album);
        }

        int track = (trackNumber.length() == 0) ? 0 : Integer.parseInt(trackNumber);

        Music song = new Music(trackTitle, id, fileHandle, artist, album, track);
        artist.addSong(song);
        album.addSong(song);
        return song;
    }

    /*
    Repeating functions for simplicity
    TODO: find a way to reuse the same function without major class alterations
    */
    public ArrayList<Music> searchSongs(String searchTerm){
        String query = searchTerm.toLowerCase().replaceFirst("\\s++$", "");

        ArrayList<Music> output = new ArrayList<>();

        for (Music i: this.songs)
            if(i.getTitle().toLowerCase().contains(query))
                output.add(i);

        if(output.size() > 0)
            return output;
        return null;
    }
    public ArrayList<Album> searchAlbums(String searchTerm){
        String query = searchTerm.toLowerCase().replaceFirst("\\s++$", "");

        ArrayList<Album> output = new ArrayList<>();

        for (Album i: this.albums)
            if(i.getTitle().toLowerCase().contains(query))
                output.add(i);

        if(output.size() > 0)
            return output;
        return null;
    }
    public ArrayList<Artist> searchArtists(String searchTerm){
        String query = searchTerm.toLowerCase().replaceFirst("\\s++$", "");

        ArrayList<Artist> output = new ArrayList<>();

        for (Artist i: this.artists)
            if(i.getName().toLowerCase().contains(query))
                output.add(i);

        if(output.size() > 0)
            return output;
        return null;
    }
    public ArrayList<Playlist> searchPlaylist(String searchTerm){
        String query = searchTerm.toLowerCase().replaceFirst("\\s++$", "");

        ArrayList<Playlist> output = new ArrayList<>();

        for (Playlist i: this.playlists)
            if(i.getTitle().toLowerCase().contains(query))
                output.add(i);

        if(output.size() > 0)
            return output;
        return null;
    }

    /** Save the current state of the Library object to a .json file
     * @param filePath Path to the output .json file
     * */
    public void saveLibrary(String filePath) throws Exception{
        File outputFile = new File(filePath);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputFile,this);

        //System.out.println("Library saved to file!");
    }
    /** Loads a new Library object from a specified .json file
     * @param filePath Path to the .json Library file
     * @return Loaded Library object
     * */
    public static Library loadLibrary(String filePath) throws Exception{
        File inputFile = new File(filePath);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(inputFile, Library.class);
    }

    /** Open the provided song files on the specified media player
     * program.
     * @param songs Array of songs to be opened
     * */
    public void openSongs(ArrayList<Music> songs){
        ProcessBuilder processBuilder = new ProcessBuilder();

        StringBuilder commandString = new StringBuilder();

        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.startsWith("windows")) {
            for (Music i: songs)
                commandString.append("\"").append(i.getFile().getPath()).append("\" ");
            // delete the last space char, this makes it work!
            commandString.deleteCharAt(commandString.length()-1);

            processBuilder.command(settings.getMediaPlayerExecutable(), commandString.toString());
        }
        else if (osName.contains("linux")) {
            commandString.append(settings.getMediaPlayerExecutable());
            for (Music i: songs)
                commandString.append(" \"").append(i.getFile().getPath()).append("\"");

            processBuilder.command("bash", "-c", commandString.toString());
        }
        else if (osName.contains("macos"))
            System.out.println("error, unsupported");

        try {
            processBuilder.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
