package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import com.turdmusic.mainApp.core.models.ImageInfo;
import com.turdmusic.mainApp.core.models.ResultInfo;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class CoverFetchController {

    public TilePane tileView;
    public Button selectButton;

    public static ResultInfo resultInfo;
    public static Music music;

    public static Library library;
    public static Settings settings;

    public void initialize(){

        try{
            List<String> covers = AcoustidRequester.getCoversURL(resultInfo.releaseGroup);

            if(covers.toArray().length > 0){
                for (int i = 0; i < covers.toArray().length; i++){
                    ImageInfo imageInfo = AcoustidRequester.downloadCover(covers.get(i), "tempCover_" + i + ".jpg");

                    ImageView imageView = createImageview(imageInfo);

                    imageView.setOnMouseClicked(mouseEvent -> {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
                            Album album = music.getAlbum();
                            album.setImageInfo(imageInfo);

                            ((Stage)tileView.getScene().getWindow()).close();
                        }
                    });

                    tileView.getChildren().add(imageView);
                }
            }
            else{
                ((Stage)tileView.getScene().getWindow()).close();
            }
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "No album cover was found");
            alert.showAndWait();

            // Close this window
            ((Stage)tileView.getScene().getWindow()).close();
        }
    }

    private ImageView createImageview(ImageInfo imageInfo){
        ImageView imageView = new ImageView();
        imageView.setImage(imageInfo.getImageObj());

        return imageView;
    }

    public void selectButtonPressed(){
        return;
    }
}
