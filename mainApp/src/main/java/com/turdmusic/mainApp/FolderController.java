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

    public static boolean addedFolder; // This tells the hello view that folder have been added and scanned
    public static boolean noFolder;
    public static Library library;
    public static Settings settings;

    public ListView<String> pathList;
    private final ObservableList<String> items = FXCollections.observableArrayList();
    private final ObservableList<String> newItems = FXCollections.observableArrayList();
    private final ObservableList<String> allItems = FXCollections.observableArrayList();
    public Stage stage;

    public Label scannedLabel;

    public void initialize(){
        addedFolder = false;
        items.setAll(library.getLibraryPaths());
        //pathList.setItems(items);
        updatePathList();

        //this.stage = (Stage) pathList.getScene().getWindow();
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
        //addedFolder = true;
    }

    public void removeFolderClicked(){
        // get the selected items
        ObservableList<String> selectedItems = pathList.getSelectionModel().getSelectedItems();

        //Check if the path is on newItems
        /*for (String i: selectedItems) {
            for (String j : newItems) {
                if (Objects.equals(i, j)) {
                    newItems.remove(i);
                }
            }
        }*/
        newItems.removeAll(selectedItems);
        //Check if the path is on items
        for (String i: selectedItems) {
            for (String j : items) {
                if (Objects.equals(i, j)) {
                    //items.remove(j);
                    library.removePath(i);
                }
            }
        }
        items.removeAll(selectedItems);
        //items.removeAll(selectedItems);
        updatePathList();
        if(items.isEmpty()){
            addedFolder = false;
        }
        //for (String i: selectedItems)
    }

    public void scanSelectedClicked(){
        // TODO: POP-UP NOTIFICATION OR FADEOUT LABEL
        ObservableList<String> selectedItems = pathList.getSelectionModel().getSelectedItems();

        //Check if the path is on newItems and scan
        for (String i: selectedItems) {
            for (String j : newItems) {
                if (Objects.equals(i, j)) {
                    library.addPath(i);
                    items.add(i);
                    //newItems.remove(i);
                    addedFolder = true;
                    showAndFadeLabel();
                }
            }
        }
        newItems.removeAll(selectedItems);

        /*for (String i: selectedItems){
            library.addPath(i);
            addedFolder = true;
            showAndFadeLabel();
        }*/
    }

    public void scanAllClicked(){
        // TODO: POP-UP NOTIFICATION
        //ObservableList<String> allItems = pathList.getItems();

        for (String i: newItems){
            library.addPath(i);
            addedFolder = true;
            showAndFadeLabel();
            //newItems.remove(i);
        }
        items.addAll(newItems);
        newItems.clear();
        //newItems.removeAll(newItems);
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

    private void updatePathList() {
        allItems.clear();
        allItems.addAll(items);
        allItems.addAll(newItems);
        pathList.setItems(allItems);
    }

    public void finishButtonClicked() {
        stage = (Stage) pathList.getScene().getWindow();
        noFolder=false;
        //No paths define
        if (items.isEmpty() && newItems.isEmpty()){
            ButtonType cancel = new ButtonType("Cancel");
            ButtonType exit = new ButtonType("Exit");
            Alert alert = new Alert(Alert.AlertType.WARNING, "You don't have scan paths. Are you sure you want exit", cancel, exit);
            alert.showAndWait().ifPresent(response -> {
                if (response == exit) {
                    noFolder = true;
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
                    if(items.isEmpty()){
                        noFolder = true;
                    }
                    newItems.clear();
                    stage.close();
                }
                if (response == save_all){
                    scanAllClicked();
                    stage.close();
                }
            });
            //TODO: Is missing come back to the heloPage
            //settings.setFirstLaunch(false);
        }
        else {
            //settings.setFirstLaunch(true);
            newItems.removeAll();
            stage = (Stage) pathList.getScene().getWindow();
            stage.close();
        }

    }
}
