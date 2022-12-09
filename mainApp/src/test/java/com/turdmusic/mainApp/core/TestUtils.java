package com.turdmusic.mainApp.core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class TestUtils {

    /**
     * This function returns a percentage indicating how
     * different two images are
     */
    public static double compareTwoImages(File fileA, File fileB){
        BufferedImage imgA = null, imgB = null;

        try{
            imgA = ImageIO.read(fileA);
            imgB = ImageIO.read(fileB);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        int widthA = imgA.getWidth(), widthB = imgB.getWidth(),
            heightA = imgA.getHeight(), heightB = imgB.getHeight();

        if((widthA != widthB) || (heightA != heightB)) {
            System.out.println("different dimensions");
            return 100;
        }

        long diff = 0;

        /*
        * https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        * */
        for (int y = 0; y < heightA; y++){
            for (int x = 0; x < widthA; x++){
                int rgbA = imgA.getRGB(x, y); // 24 bit color, 8 bit per channel
                int rgbB = imgB.getRGB(x, y);
                int redA = (rgbA >> 16) & 0xFF;
                int redB = (rgbB >> 16) & 0xFF;
                int greenA = (rgbA >> 8) & 0xFF;
                int greenB = (rgbB >> 8) & 0xFF;
                int blueA = rgbA & 0xFF;
                int blueB = rgbB & 0xFF;

                diff += Math.abs(redA - redB) + Math.abs(greenA - greenB) + Math.abs(blueA - blueB);
            }
        }

        double totalPixels = widthA * heightA * 3;
        double avgDifferentPixels = totalPixels / diff;

        return (avgDifferentPixels / 255) * 100;
    }

}
