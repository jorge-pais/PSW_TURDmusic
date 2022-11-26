package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Album;
import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Music;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.Stack;

//
// Song Controller Class
//  This is the main UI/UX controller class, here are all the views for the main
//  application window, including song/album/artist/playlist views
// TODO: ADD CONTEXT MENU (RIGHT_CLICK) TO SONGS
// TODO: FIGURE OUT HOW TO UTILIZE TILEPLANE(+VBOX) TO DISPLAY IMAGES
// TODO: ADD A PREFERENCES MENU
//

public class SongController {

    public static Library library;

    public Label songsLabelButton;
    public Label albumsLabelButton;
    public Label artistsLabelButton;
    public Label playlistsLabelButton;


    public StackPane stackPane1;

    public TableView<Music> songTable;
    public TableColumn<Music, String> titleColumn;
    public TableColumn<Music, String> artistColumn;
    public TableColumn<Music, String> albumColumn;
    public TableColumn<Music, String> durationColumn;

    public TableView<Album> albumTable;

    //public TilePane albumTiles;

    public void initialize(){
        // Allow multiple table items to be selected
        songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Setup columns (really important Lambda expressions)
        titleColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTitle()));
        artistColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getArtist().getName()));
        albumColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getAlbum().getTitle()));
        durationColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getFormattedTrackLength()));

        updateSongTable();

        // Set up table event handlers to open selected songs on double click
        songTable.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){    // Double click
                openSelectedSongs();
            }
        });
        songTable.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){ // Enter
                openSelectedSongs();
            }
        });

        // Label button event handler
        albumsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                albumTable.toFront();
                /*albumTiles.toFront();

                for (int i=0; i<10; i++)
                    albumTiles.getChildren().add(new Button("Button"+i));
                */

            }
        });
        songsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                songTable.toFront();
            }
        });


    }

    private void updateSongTable(){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(library.getSongs());

        if(songsToAdd.size() > 0)
            songTable.setItems(songsToAdd);
    }

    private void openSelectedSongs(){
        ObservableList<Music> songsSelected = songTable.getSelectionModel().getSelectedItems();
        if(songsSelected.size()>0){
            ArrayList<Music> songsToPlay = new ArrayList<>(songsSelected);
            library.openSongs(songsToPlay);
        }
    }

    /*private void changeTop() {
        //ObservableList<Node> albumTable = this.stackPane1.getChildren();

        if (albumTable.size() > 1) {
            //
            Node topNode =  albumTable.get(albumTable.size()-1);
            topNode.toBack();
        }
    }*/
}
