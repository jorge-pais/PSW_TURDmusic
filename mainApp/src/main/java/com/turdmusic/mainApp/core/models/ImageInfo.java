package com.turdmusic.mainApp.core.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.turdmusic.mainApp.core.Settings;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

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
    // Create an image file within the settings
    public ImageInfo(BufferedImage image, String fileName) throws Exception{
        String filePath;
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.startsWith("windows"))
            filePath = new String(settings.getSavePath() + "\\images\\");
        else if (osName.contains("linux"))
            filePath = new String(settings.getSavePath() + "/images/");
        else // Unsupported OS
            throw new Exception();
        
        File folder = new File(filePath);
        folder.mkdirs();

        this.path = new File(filePath + fileName + ".jpg");
        ImageIO.write(image, "jpg", this.path);
    }
    // If there's an existing image file
    public ImageInfo(File path){
        this.path = path;
    }

    @JsonIgnore
    public Image getImageObj(){
        try {
            BufferedImage image = ImageIO.read(path);

            return SwingFXUtils.toFXImage(image, null);
        }catch (Exception e){
            System.out.println("Error retrieving image from file");
            return null;
        }
    }

    public File getPath(){ return this.path; }
}
