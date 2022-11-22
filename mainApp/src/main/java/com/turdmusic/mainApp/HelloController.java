package com.turdmusic.mainApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

// TODO : make the text labels centered
public class HelloController {

    @FXML
    protected Label label1;
    protected Scene scene;

    public void initialize(){
        // When this controller's scene is loaded into the stage
        // this method is the first being executed
        this.scene = label1.getScene();
    }

    @FXML
    protected void onMouseClicked() throws IOException {

        // Create a new stage (window) and load the file selection scene
        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("pathManager.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        newStage.setTitle("Select Folders");
        newStage.setScene(scene);

        // Change new window modality (block input from current window)
        //newStage.initOwner((Stage)scene.getWindow());
        //newStage.initModality(Modality.WINDOW_MODAL);

        newStage.show();

    }

    @FXML
    protected void onKeyPressed(){


    }
}