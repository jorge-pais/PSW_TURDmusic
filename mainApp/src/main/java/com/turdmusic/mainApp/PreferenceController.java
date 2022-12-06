package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Settings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class PreferenceController {

    public static Library library;
    public static Settings settings;
    private Stage newStage;

    public TextField pathToMediaPlayerText;

    public void initialize(){}

    @FXML
    protected void onMouseClickedOpenPathManager() throws IOException {
        // Create a new stage (window) and load the file selection scene
        newStage = new Stage();
        /*FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pathManager.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        newStage.setTitle("Select Folders");
        newStage.setScene(scene);

        // Change the new window's modality
        // block input from all other application windows
        // TODO: investigate context menu buttons
        newStage.initModality(Modality.APPLICATION_MODAL);

        newStage.showAndWait();*/
        //Stage newStage = new Stage();
        MainGUI.openPathManager(newStage);
    }

    public void onMouseClickedClosePathManager(){
        //newStage.close();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setAlertType(Alert.AlertType.CONFIRMATION);
        alert.showAndWait();

        newStage = (Stage) pathToMediaPlayerText.getScene().getWindow();
        MainGUI.closePreferenceController(newStage);
    }

    public void onMouseClickedApplyPreference(){
        //in progress
    }

}
