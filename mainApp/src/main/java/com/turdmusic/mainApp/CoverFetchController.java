package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Music;
import com.turdmusic.mainApp.core.models.ImageInfo;
import com.turdmusic.mainApp.core.models.ResultInfo;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class CoverFetchController {

    public TilePane tileView;
    public Button selectButton;
    public static ResultInfo resultInfo;
    public static Music music;

    public void initialize(){

    }

    private ImageView createImageview(ImageInfo imageInfo){

        ImageView imageView = new ImageView();



        return imageView;
    }

    public void selectButtonPressed(){
        return;
    }
}
