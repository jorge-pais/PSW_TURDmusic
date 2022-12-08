package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Settings;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class PreferenceController {

    public static Library library;
    public static Settings settings;
    private Stage newStage;

    public TextField mediaPlayerText;
    public TextField fpcalcText;
    public TextField libraryPathText;

    public void initialize(){
        setupTextFields();

        // FIXME there might be a way to reuse the same lambda in different spots
        mediaPlayerText.setOnMouseClicked((mouseEvent) -> {
            if(mouseEvent.getClickCount() == 2){
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(null);
                if(file != null)
                    mediaPlayerText.setText(file.getPath());
            }
        });
        fpcalcText.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){
                FileChooser fileChooser = new FileChooser();
                File file = fileChooser.showOpenDialog(null);
                if(file != null)
                    fpcalcText.setText(file.getPath());
            }
        });
        libraryPathText.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File file = directoryChooser.showDialog(null);
                if(file != null)
                    libraryPathText.setText(file.getPath());
            }
        });
    }

    private void setupTextFields(){
        mediaPlayerText.setText(settings.getMediaPlayerExecutable());
        fpcalcText.setText(settings.getFpcalcExecutable());
        libraryPathText.setText(settings.getSavePath());
    }

    public void onMouseClickedOpenPathManager() throws IOException {
        newStage = new Stage();
        MainGUI.openPathManager(newStage);
    }

    public void onMouseClickedCancel(){
        newStage = (Stage) mediaPlayerText.getScene().getWindow();
        newStage.close();
    }

    public void onMouseClickedApply(){
        ButtonType cancel = new ButtonType("Cancel");
        ButtonType yes = new ButtonType("Yes");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to apply these settings?", cancel, yes);
        if(!settings.getSavePath().equals(libraryPathText.getText()) || !settings.getMediaPlayerExecutable().equals(mediaPlayerText.getText()) || !settings.getFpcalcExecutable().equals(fpcalcText.getText())) {
            alert.showAndWait().ifPresent(response -> {
                if (response == yes) {
                    settings.setSavePath(libraryPathText.getText());
                    settings.setFpcalcExecutable(fpcalcText.getText());
                    settings.setMediaPlayerExecutable(mediaPlayerText.getText());
                }
                else if(response == cancel)
                    setupTextFields();
            });
        }
    }

    public void onMouseClickedOK(){
        onMouseClickedApply();
        onMouseClickedCancel();
    }

}
