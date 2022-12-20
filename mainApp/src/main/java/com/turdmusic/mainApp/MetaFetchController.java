package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Music;
import com.turdmusic.mainApp.core.models.MusicInfo;
import com.turdmusic.mainApp.core.models.ResultInfo;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class MetaFetchController {

    public static Music song;
    public static List<MusicInfo.Result.Record> results;
    public static Library library;

    public TableView<ResultInfo> resultsTable;
    public TableColumn<ResultInfo, String> titleResults;
    public TableColumn<ResultInfo, String> albumResults;
    public TableColumn<ResultInfo, String> artistResults;

    public Button selectButton;

    public void initialize(){
        ArrayList<ResultInfo> extractedResults = extractResults();
        resultsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        titleResults.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().title));
        albumResults.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().album));
        artistResults.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().artist));

        ObservableList<ResultInfo> resultsToShow = FXCollections.observableArrayList();
        resultsToShow.addAll(extractedResults);

        resultsTable.setItems(resultsToShow);
    }

    public ArrayList<ResultInfo> extractResults(){

        ArrayList<ResultInfo> resultInfo = new ArrayList<>();

        for (MusicInfo.Result.Record i: results)
            for (MusicInfo.Result.Record.ReleaseGroup j: i.getReleaseGroups())
                resultInfo.add(new ResultInfo(i.getArtists().get(0).name, j.title, i.title, j));

        return resultInfo;
    }

    public void selectButtonPressed(){
        ObservableList<ResultInfo> selectedItems = resultsTable.getSelectionModel().getSelectedItems();
        if(selectedItems.size() == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR, "No result was selected, please choose the most appropriate result");
            alert.show();
            return;
        }
        else{
            ResultInfo resultInfo = selectedItems.get(0);
            library.removeSong(song);
            Music music = library.createSongEntry(resultInfo.artist, resultInfo.album, resultInfo.title, song.getFile(), library.getSongs().size(), song.getTrackNumber());
            library.getSongs().add(music);
            // open the album cover fetching function
        }
        // call another function to create the new song
    }

}
