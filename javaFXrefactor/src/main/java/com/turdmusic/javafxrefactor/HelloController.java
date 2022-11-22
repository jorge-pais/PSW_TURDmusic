package com.turdmusic.javafxrefactor;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
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
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("pathSelection.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Select Folders");
        stage.setScene(scene);
        stage.show();
        stage.initOwner(label1.getScene().getWindow());
    }

    @FXML
    protected void onKeyPressed(){


    }
}