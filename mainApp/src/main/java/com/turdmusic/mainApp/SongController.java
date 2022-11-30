package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Album;
import com.turdmusic.mainApp.core.Artist;
import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Music;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

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

    public ScrollPane playlistScroll;
    public ScrollPane artistScroll;
    public ScrollPane albumScroll;
    public TilePane artistTiles;
    public TilePane albumTiles;

    public void initialize(){
        // Allow multiple table items to be selected
        songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        artistTiles.setStyle("-fx-background-color: #FFFFFF;");

        // Setup columns (really important Lambda expressions)
        titleColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTitle()));
        artistColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getArtist().getName()));
        albumColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getAlbum().getTitle()));
        durationColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getFormattedTrackLength()));

        updateSongTable();

        //for artist view
        //for (Artist i: library.getArtists())
            //artistTiles.getChildren().add(vBoxFromArtist(i));


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

        // Label button event handlers
        albumsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                albumsLabelButton.setFont(Font.font("System",FontWeight.BOLD, FontPosture.REGULAR, 18));
                songsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                artistsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                playlistsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));

                albumTiles.getChildren().removeAll(albumTiles.getChildren());
                albumScroll.toFront();

                for (Album i: library.getAlbums()) {
                    i.findAlbumCover();
                    VBox tile = makeImageTile(i.getCoverArt(), i.getTitle());
                    albumTiles.getChildren().add(tile);
                }
            }
        });

        songsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                songTable.toFront();
                albumsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                songsLabelButton.setFont(Font.font("System",FontWeight.BOLD, FontPosture.REGULAR, 18));
                artistsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                playlistsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
            }
        });

        artistsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                artistScroll.toFront();
                albumsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                songsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                artistsLabelButton.setFont(Font.font("System",FontWeight.BOLD, FontPosture.REGULAR, 18));
                playlistsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));

            for (Album i: library.getAlbums())
                System.out.println(i.id +  "-->" + i.getTitle());

            }
        });

        playlistsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                playlistScroll.toFront();
                albumsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                songsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                artistsLabelButton.setFont(Font.font("System",FontWeight.NORMAL, FontPosture.REGULAR, 18));
                playlistsLabelButton.setFont(Font.font("System",FontWeight.BOLD, FontPosture.REGULAR, 18));
            }
        });

    }
    @FXML
    public VBox makeImageTile(Image image, String label){
    //private VBox vBoxFromArtist(Artist i) {
        VBox vBoxout = new VBox();
        vBoxout.prefHeight(200);
        vBoxout.prefWidth(200);
        vBoxout.setStyle("-fx-border-color: #000000;");
        vBoxout.setAlignment(Pos.CENTER);
        vBoxout.setLayoutX(10);
        vBoxout.setLayoutY(10);

        vBoxout.getChildren().add(new TilePane());
        Rectangle rectangle = new Rectangle();
        rectangle.setSize(150,150);
        ImageView picture = new ImageView();
        //Image myImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("album_capas/Abbey_Road.jpg")));
        picture.setImage(image);
        picture.setFitHeight(150);
        picture.setFitWidth(150);
        vBoxout.getChildren().add(picture);

        vBoxout.getChildren().add(new Label(label));
        return vBoxout;
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

}
