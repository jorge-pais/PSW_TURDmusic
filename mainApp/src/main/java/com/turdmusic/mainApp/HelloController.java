package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    protected Label label1;
    protected Scene scene;
    public static Library library;


    public void initialize(){
        // When this controller's scene is loaded into the stage
        // this method is the first being executed
        this.scene = label1.getScene();
    }

    @FXML
    protected void onMouseClicked() throws IOException {
        // Create a new stage (window) and load the file selection scene
        Stage newStage = new Stage();
        MainGUI.openFolderPage(newStage);

        if(FolderSelection.addedFolder) {
            // Mark the first launch here
            Library.settings.setFirstLaunch(false);
            System.out.println("Paths have been added");

            // Change to music view
            try{
                Stage stage = (Stage) label1.getScene().getWindow();
                MainGUI.createStage(stage);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}