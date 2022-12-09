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

/**
    Controller class for the path selection window
*/
public class FolderController {

    public static boolean addedFolder; // This tells the hello view that folder have been added and scanned
    public static Library library;
    public static Settings settings;

    public ListView<String> pathList;
    private final ObservableList<String> items = FXCollections.observableArrayList();
    public Stage stage;

    public Label scannedLabel;

    public void initialize(){
        addedFolder = false;
        pathList.setItems(items);

        items.setAll(library.getLibraryPaths());
        // Allow multiple items to be selected
        pathList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void addFolderClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File path = directoryChooser.showDialog(null);
        if(path == null) return;

        items.add(path.getPath());
        pathList.setItems(items); // Update list
        //addedFolder = true;
    }

    public void removeFolderClicked(){
        // get the selected items
        ObservableList<String> selectedItems = pathList.getSelectionModel().getSelectedItems();

        items.removeAll(selectedItems);
        pathList.setItems(items); //Update list
        if(items.isEmpty()){
            addedFolder = false;
        }
        for (String i: selectedItems)
            library.removePath(i);
    }

    public void scanSelectedPressed(){
        // TODO: POP-UP NOTIFICATION OR FADEOUT LABEL
        ObservableList<String> selectedItems = pathList.getSelectionModel().getSelectedItems();

        for (String i: selectedItems){
            library.addPath(i);
            addedFolder = true;
            showAndFadeLabel();
        }
    }

    public void scanAllPressed(){
        // TODO: POP-UP NOTIFICATION
        ObservableList<String> allItems = pathList.getItems();

        for (String i: allItems){
            library.addPath(i);
            addedFolder = true;
            showAndFadeLabel();
        }
    }

    public void finishButtonClicked() {
        if(addedFolder==true){
            stage = (Stage) pathList.getScene().getWindow();
            stage.close();
        }
        else {
            ButtonType cancel = new ButtonType("Cancel");
            ButtonType yes = new ButtonType("Yes");
            Alert alert = new Alert(Alert.AlertType.WARNING, "You don't have scan paths. Are you sure you want exit", cancel, yes);
            alert.showAndWait().ifPresent(response -> {
                if (response == yes) {
                    stage = (Stage) pathList.getScene().getWindow();
                    stage.close();
                }
            });
        }
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
