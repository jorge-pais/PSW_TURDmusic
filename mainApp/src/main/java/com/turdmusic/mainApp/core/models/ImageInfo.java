package com.turdmusic.mainApp.core.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.turdmusic.mainApp.core.Settings;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
    Class to store the image files for the
    artists and albums
*/
@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "@json_id"
)
public class ImageInfo {

    public static Settings settings;

    // store the image filePath
    // This will break when moving for example the
    private File path;

    public ImageInfo(){
        super();
    }

    // Create a image file within the settings
    public ImageInfo(BufferedImage image){

    }

    // If there's an existing image file
    public ImageInfo(File path){

    }

    public Image getImageObj() throws IOException {
        BufferedImage image = ImageIO.read(path);

        return SwingFXUtils.toFXImage(image, null);
    }

}
