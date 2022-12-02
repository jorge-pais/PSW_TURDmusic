package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

//
// Song Controller Class
//  This is the main UI/UX controller class, here are all the views for the main
//  application window, including song/album/artist/playlist views

//
// TODO: ADD CONTEXT MENU (RIGHT_CLICK) TO SONGS
// TODO: ADD TOP MENU ITEM BEHAVIOUR
//

public class SongController {
    private enum CurrentView {Songs, Albums, Artists, Playlists};
    private CurrentView state;

    public static Library library;

    public MenuItem optionsPreferences;

    public Label songsLabelButton;
    public Label albumsLabelButton;
    public Label artistsLabelButton;
    public Label playlistsLabelButton;

    public TableView<Music> songTable;
    public TableColumn<Music, String> titleColumn;
    public TableColumn<Music, String> artistColumn;
    public TableColumn<Music, String> albumColumn;
    public TableColumn<Music, String> durationColumn;

    public ScrollPane playlistScroll;
    public ScrollPane artistScroll;
    public ScrollPane albumScroll;
    public TilePane artistTiles;
    public TilePane albumTiles;

    public HBox hBoxPage;
    public VBox vBoxPage;
    public Text textPage;
    public void initialize(){
        // Allow multiple table items to be selected
        songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        artistTiles.setStyle("-fx-background-color: #FFFFFF;");

        // Set up the song table columns (really important Lambda expressions)
        titleColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTitle()));
        artistColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getArtist().getName()));
        albumColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getAlbum().getTitle()));
        durationColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getFormattedTrackLength()));

        updateSongTable();

        //artistTiles.boundsInParentProperty();
        ContextMenu contextMenu = new ContextMenu();
        MenuItem mi1 = new MenuItem("Delete");
        //mi1.setOnAction(e -> System.out.println("Item 1"));
        MenuItem mi2 = new MenuItem("Edit");
        MenuItem mi3 = new MenuItem("Add to");
        MenuItem mi4 = new MenuItem("Go to Album");
        MenuItem mi5 = new MenuItem("Go to Artist");
        //mi2.setOnAction(e -> System.out.println("Item 2"));
        contextMenu.getItems().addAll(mi1, mi2, mi3, mi4, mi5);

        // Set up table event handlers to open selected songs on double click
        songTable.setOnMouseClicked(mouseEvent -> {
            MouseButton button = mouseEvent.getButton();
            if((mouseEvent.getClickCount() == 2) && button==MouseButton.PRIMARY){    // Double click
                openSelectedSongs();
            }
            if((button==MouseButton.SECONDARY)){
                contextMenu.show(songTable, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            }
        });

        mi4.setOnAction(mouseEvent -> {
            hBoxPage.toFront();
        });
        mi5.setOnAction(mouseEvent -> {
            hBoxPage.toFront();
        });
        songTable.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER){ // Enter
                openSelectedSongs();
            }
        });


        // Left pane button event handlers
        songsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                updateSongTable();
                changeToSongView();
            }
        });
        albumsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                updateAlbumTiles(library.getAlbums());
                changeToAlbumView();
            }
        });
        artistsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1) {
                updateArtistTiles(library.getArtists());
                changeToArtistView();

                for (Album i : library.getAlbums())
                    System.out.println(i.id + "-->" + i.getTitle());
            }
        });
        playlistsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                changeToPlaylistView();
            }
        });
    }
    // TODO: FIND WHAT'S MAKING THE TILES WIERD
    // TODO: FIND HOW TO MAKE THE TILEPANE AND SCROLLPANE WORK
    public VBox makeImageTile(Image image, String label){
        VBox vBoxout = new VBox();
        vBoxout.prefHeight(200);
        vBoxout.prefWidth(200);
        vBoxout.setStyle("-fx-border-color: #000000;");
        vBoxout.setAlignment(Pos.CENTER);
        vBoxout.setLayoutX(10);
        vBoxout.setLayoutY(10);

        ImageView picture = new ImageView();
        picture.setImage(image);
        picture.setFitHeight(150);
        picture.setFitWidth(150);
        vBoxout.getChildren().add(picture);

        vBoxout.getChildren().add(new Label(label));
        return vBoxout;
    }

    private void changeToSongView(){
        ChangeView();
        songTable.toFront();
        songsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    private void changeToAlbumView(){
        ChangeView();
        albumScroll.toFront();
        albumsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    private void changeToArtistView(){
        ChangeView();
        artistScroll.toFront();
        artistsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    private void changeToPlaylistView(){
        ChangeView();
        playlistScroll.toFront();
        playlistsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    private void ChangeView() {
        albumsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        songsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        artistsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        playlistsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
    }
    private void updateSongTable(){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(library.getSongs());

        if(songsToAdd.size() > 0)
            songTable.setItems(songsToAdd);
    }
    private void updateAlbumTiles(ArrayList<Album> albums){
        albumTiles.getChildren().removeAll(albumTiles.getChildren());

        for (Album i: albums) {
            i.findAlbumCover();

            VBox tile = makeImageTile(i.getCoverArt(), i.getTitle());
            albumTiles.getChildren().add(tile);
        }
    }
    private void updateArtistTiles(ArrayList<Artist> artists){
        artistTiles.getChildren().removeAll(artistTiles.getChildren()); //Clear

        try{
            for(Artist i: artists){
                VBox tile = makeImageTile(i.getPicture(), i.getName());
                artistTiles.getChildren().add(tile);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updatePlaylistTiles(ArrayList<Playlist> playlists){
        // TODO: IMPLEMENT
    }

    public void launchPreferences() throws IOException {
        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("preferenceView.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        newStage.setScene(scene);
        newStage.setResizable(false);
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.showAndWait();
    }

/*
    Save and load methods save/load the library
    to the defined savePath as library.json
*/
    public void saveDefaultLibrary() throws Exception{
        String filePath;

        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.startsWith("windows"))
            filePath = library.settings.getSavePath() + "\\";
        else if (osName.contains("linux"))
            filePath = library.settings.getSavePath() + "/";
        else // Unsupported OS
            throw new Exception();

        File folder = new File(filePath);
        folder.mkdirs();

        String path = filePath + "library.json";

        library.saveLibrary(path);
    }
    public void loadDefaultLibrary() throws Exception{
        String path = new String(library.settings.getSavePath() + "library.json");
        library = Library.loadLibrary(path);
    }

    public void exportLibrary() throws Exception{
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
    }
    public void importLibrary() throws Exception{
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        library = Library.loadLibrary(file.getPath());
    }


    private void openSelectedSongs(){
        ObservableList<Music> songsSelected = songTable.getSelectionModel().getSelectedItems();
        if(songsSelected.size()>0){
            ArrayList<Music> songsToPlay = new ArrayList<>(songsSelected);
            library.openSongs(songsToPlay);
        }
    }
}
