package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Settings;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Objects;

/**
    Controller class for the path selection window
*/
public class FolderController {
    public static Library library;
    public static Settings settings;

    public ListView<String> pathList;
    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final ObservableList<String> newItems = FXCollections.observableArrayList();
    private final ObservableList<String> allItems = FXCollections.observableArrayList();
    public Stage stage;

    public Label scannedLabel;

    public void initialize(){
        items.setAll(library.getLibraryPaths());
        updatePathList();

        // Allow multiple items to be selected
        pathList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    public void addFolderClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File path = directoryChooser.showDialog(null);
        if(path == null) return;

        newItems.add(path.getPath());
        //check if the path exist
        newItems.removeAll(items);

        //update view
        updatePathList();
    }
    public void removeFolderClicked(){
        // get the selected items
        ObservableList<String> selectedItems = pathList.getSelectionModel().getSelectedItems();

        //Check if the path is on newItems
        newItems.removeAll(selectedItems);

        //Check if the path is on items
        for (String i: selectedItems) {
            for (String j : items) {
                if (Objects.equals(i, j)) {
                    library.removePath(i);
                }
            }
        }
        items.removeAll(selectedItems);
        updatePathList();
    }
    public void scanSelectedPressed(){
        ObservableList<String> selectedItems = pathList.getSelectionModel().getSelectedItems();

        //Check if the path is on newItems and scan
        for (String i: selectedItems) {
            for (String j : newItems) {
                if (Objects.equals(i, j)) {
                    library.addPath(i);
                    items.add(i);
                    showAndFadeLabel();
                }
            }
        }
        newItems.removeAll(selectedItems);
    }
    public void scanAllPressed(){
        //ObservableList<String> allItems = pathList.getItems();

        for (String i: newItems){
            library.addPath(i);

            //POP-UP NOTIFICATION
            showAndFadeLabel();
        }
        items.addAll(newItems);
        newItems.clear();
    }
    public void finishButtonClicked() {
        stage = (Stage) pathList.getScene().getWindow();
        //No paths define
        if (items.isEmpty() && newItems.isEmpty()){
            ButtonType cancel = new ButtonType("Cancel");
            ButtonType exit = new ButtonType("Exit");
            Alert alert = new Alert(Alert.AlertType.WARNING, "You don't have scan paths. Are you sure you want exit", cancel, exit);
            alert.showAndWait().ifPresent(response -> {
                if (response == exit) {
                    stage.close();
                }
            });
        }
        else if (!newItems.isEmpty()){
            ButtonType cancel = new ButtonType("Cancel");
            ButtonType exit = new ButtonType("Don't save");
            ButtonType save_all = new ButtonType("Save all paths");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The paths are not save. Scan all new paths first or they will be remove", cancel, exit, save_all);
            alert.showAndWait().ifPresent(response -> {
                if (response == exit) {
                    newItems.clear();
                    stage.close();
                }
                if (response == save_all){
                    scanAllPressed();
                    stage.close();
                }
            });
        }
        else {
            newItems.removeAll();
            stage.close();
        }
    }


    private void updatePathList() {
        allItems.clear();
        allItems.addAll(items);
        allItems.addAll(newItems);
        pathList.setItems(allItems);
    }
    // Fade label for visual feedback
    private void showAndFadeLabel(){
        FadeTransition fade = new FadeTransition();
        fade.setDuration(Duration.millis(2000));

        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setCycleCount(1);

        fade.setNode(scannedLabel);
        scannedLabel.setVisible(true);

        fade.play();
    }
}
