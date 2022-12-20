package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.models.MusicInfo;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.List;

public class MetaFetchController {

    public static List<MusicInfo.Result.Record> results;
    public static Library library;

    public TableView<MusicInfo.Result.Record> resultsTable;
    public TableColumn<MusicInfo.Result.Record, String> titleResults;
    public TableColumn<MusicInfo.Result.Record, String> albumResults;
    public TableColumn<MusicInfo.Result.Record, String> artistResults;

    public Button selectButton;

    public void initialize(){
        resultsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        titleResults.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTitle()));
        albumResults.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getReleaseGroups().get(0).getTitle()));
        artistResults.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getArtists().get(0).getName()));

        ObservableList<MusicInfo.Result.Record> resultsToShow = FXCollections.observableArrayList();
        resultsToShow.addAll(results);

        resultsTable.setItems(resultsToShow);
    }

    public void selectButtonPressed(){
        ObservableList<MusicInfo.Result.Record> selectedItems = resultsTable.getSelectionModel().getSelectedItems();
        if(selectedItems.size() == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR, "No result was selected, please choose the most appropriate result");
            alert.show();
            return;
        }
        //else
        // call another function to create the new song
    }

}
