package com.turdmusic.javafxrefactor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// TODO : make the text labels centered
public class HelloController {


    public void initialize(){
        // When this controller's scene is loaded into the stage
        // this method is the first being executed

    }

    @FXML
    protected void onMouseClicked() throws IOException {

        // Create a new stage (window) and load the file selection scene
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("pathSelection.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Select Folders");
        stage.setScene(scene);
        stage.show();

    }
}