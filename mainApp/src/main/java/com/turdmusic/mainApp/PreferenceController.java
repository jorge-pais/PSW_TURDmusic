package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PreferenceController {

    public static Library library;
    public static Settings settings;

    public Stage newStage;
    @FXML
    protected void openPathManager() throws IOException {
        // Create a new stage (window) and load the file selection scene
        newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pathManager.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        newStage.setTitle("Select Folders");
        newStage.setScene(scene);

        // Change the new window's modality
        // block input from all other application windows
        // TODO: investigate context menu buttons
        newStage.initModality(Modality.APPLICATION_MODAL);

        newStage.showAndWait();
    }

    public void closePathManager(){
        //newStage.close();
    }

    public void applyPreference(){

    }

}
