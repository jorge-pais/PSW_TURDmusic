package com.turdmusic.mainApp.core;

import com.turdmusic.mainApp.core.models.ImageInfo;
import jdk.jshell.execution.Util;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Running some of these tests will generate the preferences
 * folder for the application
 * */
class LibraryTest {

    /* Load the test Resources*/
    URL url1 = this.getClass().getResource("album_1");
    URL url2 = this.getClass().getResource("album_2");
    URL url3 = this.getClass().getResource("album_3");

    /**  Adds three paths to the library, one for each album.
    *   The function will test both the addPath and the scanFilePath
    *   functionality. The tests described will verify that the object
    *   was created correctly.
    */
    @Test
    void addPath() {
        Library library = new Library();
        // initialize settings
        Library.settings = new Settings();
        // Otherwise the image tests will fail
        ImageInfo.settings = Library.settings;

        assert url1 != null && url2 != null && url3 != null;
        library.addPath(url1.getPath());
        library.addPath(url2.getPath());
        library.addPath(url3.getPath());

        Music myBoy = library.searchSongs("My Boy (Twin Fantasy)").get(0); assertNotNull(myBoy);
        Music stopSmoking = library.searchSongs("Stop Smoking").get(0); assertNotNull(stopSmoking);
        Music fillInTheBlank = library.searchSongs("03-Fill in the Blank").get(0); assertNotNull(fillInTheBlank);

        Artist csh = library.searchArtists("Car Seat Headrest").get(0); assertNotNull(csh);
        Artist undefinedArtist = library.searchArtists("Undefined").get(0); assertNotNull(undefinedArtist);
        Album twinFantasy = library.searchAlbums("Twin Fantasy").get(0); assertNotNull(twinFantasy);
        Album undefinedAlbum = library.searchAlbums("Undefined").get(0); assertNotNull(undefinedAlbum);
        String coverPath = this.getClass().getResource("album_1/cover.png").getPath();
        assertNotNull(coverPath);

        // Verify that the structure has been created correctly
        assertAll(
                () -> assertTrue(csh.getSongs().contains(myBoy)),
                () -> assertTrue(csh.getSongs().contains(stopSmoking)),
                () -> assertEquals(twinFantasy.getArtist(), csh),
                () -> assertTrue(twinFantasy.getTracklist().contains(myBoy)),
                () -> assertTrue(twinFantasy.getTracklist().contains(stopSmoking)),
                () -> assertTrue(csh.getAlbums().contains(twinFantasy)),
                () -> assertEquals(twinFantasy.getImageInfo().getPath().getPath(), coverPath)
        );

        /*Although this song contains metadata for Artist, it does not have
        * album information, as so both should return undefined*/
        assertTrue(undefinedArtist.getSongs().contains(fillInTheBlank));
        assertTrue(undefinedAlbum.getTracklist().contains(fillInTheBlank));

        Music ultimoDia = library.searchSongs("O Último Dia Na Terra").get(0); assertNotNull(ultimoDia);
        Music oCaos = library.searchSongs("O Caos").get(0); assertNotNull(oCaos);
        Artist joseCid = library.searchArtists("Jose Cid").get(0); assertNotNull(joseCid);
        Album dezMilAnos = library.searchAlbums("10.000 Anos Depois Entre Vénus E Marte").get(0); assertNotNull(dezMilAnos);

        assertAll(
                () -> assertTrue(joseCid.getSongs().contains(ultimoDia)),
                () -> assertTrue(joseCid.getSongs().contains(oCaos)),
                () -> assertTrue(joseCid.getAlbums().contains(dezMilAnos)),
                () -> assertEquals(dezMilAnos.getArtist(), joseCid),
                () -> assertTrue(dezMilAnos.getTracklist().contains(ultimoDia)),
                () -> assertTrue(dezMilAnos.getTracklist().contains(oCaos)),
                () -> assertNull(dezMilAnos.getImageInfo())
        );

    }

    @Test
    void removePath() {

        Library library = new Library();
        // initialize settings
        Library.settings = new Settings();
        // Otherwise the image tests will fail
        ImageInfo.settings = Library.settings;

        assert url1 != null && url2 != null && url3 != null;
        library.addPath(url1.getPath());
        library.addPath(url2.getPath());
        library.addPath(url3.getPath());

        Music myBoy = library.searchSongs("My Boy (Twin Fantasy)").get(0); assertNotNull(myBoy);
        Music stopSmoking = library.searchSongs("Stop Smoking").get(0); assertNotNull(stopSmoking);
        Album twinFantasy = library.searchAlbums("Twin Fantasy").get(0); assertNotNull(twinFantasy);
        Artist csh = library.searchArtists("Car Seat Headrest").get(0); assertNotNull(csh);

        library.removePath(url1.getPath());
        assertAll(
                () -> assertFalse(library.getSongs().contains(myBoy)),
                () -> assertFalse(library.getSongs().contains(stopSmoking)),
                () -> assertFalse(library.getArtists().contains(csh)),
                () -> assertFalse(library.getAlbums().contains(twinFantasy)),
                () -> assertFalse(library.getLibraryPaths().contains(url1.getPath()))
        );
    }

    @Test
    void removeSong() {
        Library library = new Library();

        assert url1 != null && url2 != null;

        library.addPath(url1.getPath());
        library.addPath(url2.getPath());

        Music myBoy = library.searchSongs("My Boy (Twin Fantasy)").get(0); assertNotNull(myBoy);
        Music stopSmoking = library.searchSongs("Stop Smoking").get(0); assertNotNull(stopSmoking);
        Album twinFantasy = library.searchAlbums("Twin Fantasy").get(0); assertNotNull(twinFantasy);
        Artist csh = library.searchArtists("Car Seat Headrest").get(0); assertNotNull(csh);

        library.removeSong(myBoy);

        assertAll(
                () -> assertFalse(library.getSongs().contains(myBoy)),
                () -> assertTrue(library.getAlbums().contains(twinFantasy)),
                () -> assertTrue(library.getArtists().contains(csh)),
                () -> assertFalse(twinFantasy.getTracklist().contains(myBoy)),
                () -> assertFalse(csh.getSongs().contains(myBoy)),
                () -> assertTrue(csh.getAlbums().contains(twinFantasy))
        );
    }

    @Test
    void loadSaveLibrary() {
        Library library = new Library();
        Library.settings = new Settings();
        ImageInfo.settings = Library.settings;

        assert url1 != null && url2 != null && url3 != null;
        library.addPath(url1.getPath());
        library.addPath(url2.getPath());
        library.addPath(url3.getPath());

        Library newLibrary = null;

        try {
            library.saveLibrary("test.json");
        } catch (Exception e){
            e.printStackTrace();
            fail("Error saving library to file");
        }

        try{
            newLibrary = Library.loadLibrary("test.json");
        }catch (Exception e){
            e.printStackTrace();
            fail("Error loading new lobrary");
        }

        for (Music i: library.getSongs()) {
            Music j = newLibrary.searchSongs(i.getTitle()).get(0); assertNotNull(j);
            assertEquals(i.getTitle(), j.getTitle());
            assertEquals(i.getFile(), j.getFile());
            assertEquals(i.getAlbum().getTitle(), j.getAlbum().getTitle());
            assertEquals(i.getArtist().getName(), i.getArtist().getName());
        }

        for (Album i: library.getAlbums()){
            Album j = newLibrary.searchAlbums(i.getTitle()).get(0); assertNotNull(j);
            assertEquals(i.getTitle(), j.getTitle());
            assertEquals(i.getCoverArt().getUrl(), j.getCoverArt().getUrl());

            for (int index = 0; index < i.getTracklist().size(); index++){
                Music musicI = i.getTracklist().get(index);
                Music musicJ = j.getTracklist().get(index);
                assertEquals(musicI.getTitle(), musicJ.getTitle());
                assertEquals(musicJ.getFile(), musicJ.getFile());
            }
        }

        for (Artist i: library.getArtists()){
            Artist j = newLibrary.searchArtists(i.getName()).get(0); assertNotNull(j);
            assertEquals(i.getName(), j.getName());
            try{
                assertEquals(i.getPicture().getUrl(), j.getPicture().getUrl());
            }catch (Exception e){
                fail("Error while checking artist pictures");
            }

            for (int index = 0; index < i.getAlbums().size(); index++){
                Album albumI = i.getAlbums().get(index);
                Album albumJ = j.getAlbums().get(index);
                assertEquals(albumI.getTitle(), albumJ.getTitle());
            }
        }
    }
}