package com.turdmusic.mainApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class PathSelection {

    public ListView<String> pathList;
    private ObservableList<String> items = FXCollections.observableArrayList();
    public Stage stage;

    public void initialize(){
        // TODO : Load existing library path strings from current library object

        // Allow multiple items to be selected
        pathList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    protected void addFolderClicked(){
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File path = directoryChooser.showDialog(null);

        items.add(path.getPath());
        pathList.setItems(items); // Update list
    }

    @FXML
    protected void removeFolderClicked(){
        // get the selected items
        ObservableList<String> selectedItems = pathList.getSelectionModel().getSelectedItems();

        items.removeAll(selectedItems);
        pathList.setItems(items); //Update list
    }

    @FXML
    protected void finishButtonClicked() {
        stage = (Stage) pathList.getScene().getWindow();
        stage.close();
        // We should somehow relay the information that this button was pressed,
        // and that folder paths have been scanned
    }

    @FXML
    protected void cancelButtonClicked() throws IOException{
        stage = (Stage) pathList.getScene().getWindow();
        stage.close();
    }

}
