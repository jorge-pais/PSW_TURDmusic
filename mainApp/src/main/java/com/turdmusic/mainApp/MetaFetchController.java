package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Music;
import com.turdmusic.mainApp.core.models.MusicInfo;
import com.turdmusic.mainApp.core.models.ResultInfo;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
        }
        else{
            ResultInfo resultInfo = selectedItems.get(0);
            library.removeSong(song);
            Music music = library.createSongEntry(resultInfo.artist, resultInfo.album, resultInfo.title, song.getFile(), library.getSongs().size(), song.getTrackNumber());
            library.getSongs().add(music);

            CoverFetchController.music = music;
            CoverFetchController.resultInfo = resultInfo;

            ButtonType yes = new ButtonType("Yes");
            ButtonType no = new ButtonType("No");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to also get the album cover art?", no, yes);
            alert.showAndWait().ifPresent(response -> {
                if (response == no){
                    ((Stage)selectButton.getScene().getWindow()).close();
                }
                if (response == yes) {
                    FXMLLoader loader = new FXMLLoader(MetaFetchController.class.getResource("coverFetch.fxml"));

                    try {
                        Scene scene = new Scene(loader.load());
                        Stage stage = new Stage();
                        stage.setTitle("Select Album Cover");
                        stage.getIcons().add(MainGUI.getAppIcon());
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (Exception e) {
                        System.out.println("Error during cover art fetching");
                    }
                    ((Stage)selectButton.getScene().getWindow()).close();
                }
            });
        }
        // call another function to create the new song
    }

}
