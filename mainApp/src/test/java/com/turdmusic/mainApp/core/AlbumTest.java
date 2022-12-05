package com.turdmusic.mainApp.core;

import com.turdmusic.mainApp.core.models.ImageInfo;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

    URL url1 = this.getClass().getResource("album_1/Car Seat Headrest - Twin Fantasy - 01 My Boy (Twin Fantasy).mp3");
    URL url2 = this.getClass().getResource("album_1/Car Seat Headrest - Twin Fantasy - 01 Stop Smoking.mp3");
    URL url3 = this.getClass().getResource("album_3");

    @Test
    void findAlbumCover() {
        Library library = new Library();
        // initialize settings
        Library.settings = new Settings();
        // Otherwise the image tests will fail
        ImageInfo.settings = Library.settings;

        assert url3 != null;
        library.addPath(url3.getPath());

        Music fillInTheBlank = library.searchSongs("03-Fill in the Blank").get(0);
        assertNotNull(fillInTheBlank);

        File refCover = new File(this.getClass().getResource(
                "album_3/referenceImage/cover.jpg").getFile());
        File albumCover = fillInTheBlank.getAlbum().getImageInfo().getPath();
        assertNotNull(refCover);
        double imageDiff = TestUtils.compareTwoImages(albumCover, refCover);
        System.out.println("Difference between pictures: " + imageDiff);
        assertTrue(imageDiff <= 2.00); // if the pictures are 98% similar, assume a pass
    }
}