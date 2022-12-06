package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


 /** Main View Controller Class
  * This is the main UI/UX controller class, here are all the views
  * for the main application window, including
  * song/album/artist/playlist views
  * */
public class MainView {
    // TODO: REMOVE THIS ENUM IF IT DOESN'T PROVE USEFUL
    /*private enum CurrentView {SongView, AlbumView, ArtistView, PlaylistView, Album, Artist, Playlist};
    private CurrentView state;*/

    public static Library library;

    public MenuItem optionsPreferences;

    public Label songsLabelButton;
    public Label albumsLabelButton;
    public Label artistsLabelButton;
    public Label playlistsLabelButton;

    public TableView<Music> songViewTable;
    public TableColumn<Music, String> songViewTitleColumn;
    public TableColumn<Music, String> songViewArtistColumn;
    public TableColumn<Music, String> songViewAlbumColumn;
    public TableColumn<Music, String> songViewDurationColumn;

    public ScrollPane playlistScroll;
    public ScrollPane artistScroll;
    public ScrollPane albumScroll;
    public TilePane artistTiles;
    public TilePane albumTiles;

    //public HBox pageHBox;
    public VBox pageVBox;
    public Text pageText;
    public TableView<Music> pageTable;
    public TableColumn<Music, String> pageTitleColumn;
    public TableColumn<Music, String> pageDurationColumn;
    public ImageView pageImage;

    public void initialize(){
        // Allow multiple table items to be selected
        setupSongTable();
        setupInnerSongTable();
        setupSongTableContext();
        updateSongTable();

        /* albumTiles.setOnMouseClicked(mouseEvent -> {
            MouseButton button = mouseEvent.getButton();
            if((button==MouseButton.SECONDARY)){
                songContext.show(songViewTable, mouseEvent.getScreenX(), mouseEvent.getScreenY());
            }
        }); */

        setupLeftPanel();
    }

    /** Sets up the left pane button event handlers */
    private void setupLeftPanel(){
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
            }
        });
        playlistsLabelButton.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 1){
                changeToPlaylistView();
            }
        });
    }

    // This context menu is shared across all the song tables
    // Setup event handling for song view context menu
    private void setupSongTableContext(){
        ContextMenu songContext = new ContextMenu();

        MenuItem mi1 = new MenuItem("Delete");
        MenuItem mi2 = new MenuItem("Edit");
        MenuItem mi3 = new MenuItem("Add to");
        MenuItem mi4 = new MenuItem("Go to Album");
        MenuItem mi5 = new MenuItem("Go to Artist");

        songContext.getItems().addAll(mi1, mi2, mi3, mi4, mi5);
        songViewTable.setContextMenu(songContext);

        songViewTable.setOnMousePressed(mouseEvent -> {
            ObservableList<Music> songsSelected = songViewTable.getSelectionModel().getSelectedItems();
            // Perhaps change the conditions to verify if all the selected songs have the same artist/album
            boolean cond = (songsSelected.size() != 1);

            if(mouseEvent.isSecondaryButtonDown()){
                mi4.setDisable(cond);
                mi5.setDisable(cond);
            }
        });

        mi4.setOnAction(mouseEvent -> {
            pageVBox.toFront();
            ObservableList<Music> songsSelected = songViewTable.getSelectionModel().getSelectedItems();
            if(songsSelected.size()>0){
                ArrayList<Music> songs = new ArrayList<>(songsSelected);
                Album i = songs.get(0).getAlbum();
                updateInnerAlbumView(i);
            }
        });
        /*mi5.setOnAction(mouseEvent -> {
            pageVBox.toFront();
            ObservableList<Music> songsSelected = songViewTable.getSelectionModel().getSelectedItems();
            if(songsSelected.size()==1){
                ArrayList<Music> songs = new ArrayList<>(songsSelected);
                Artist i = songs.get(0).getArtist();
                pageImage.setImage(i.getPicture());
                pageText.setText(i.getName());
                //tablePage.
            }
        });*/
    }
    private void setupSongTable(){
        songViewTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        //artistTiles.setStyle("-fx-background-color: #FFFFFF;");

        // Set up the song table columns (really important Lambda expressions)
        songViewTitleColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTitle()));
        songViewArtistColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getArtist().getName()));
        songViewAlbumColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getAlbum().getTitle()));
        songViewDurationColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getFormattedTrackLength()));

        // Set up table event handlers to open selected songs on double click
        songViewTable.setOnMouseClicked(mouseEvent -> {
            if((mouseEvent.getClickCount() == 2) && mouseEvent.getButton() == MouseButton.PRIMARY)
                openSelectedSongs(songViewTable);
        });
        songViewTable.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) // Enter
                openSelectedSongs(songViewTable);
        });
    }
    private void setupInnerSongTable(){
        pageTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pageTitleColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getTitle()));
        pageDurationColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getFormattedTrackLength()));

        pageTable.setOnMouseClicked(mouseEvent -> {
            if((mouseEvent.getClickCount() == 2) && mouseEvent.getButton() == MouseButton.PRIMARY)
                openSelectedSongs(pageTable);
        });
        pageTable.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) // Enter
                openSelectedSongs(pageTable);
        });
    }

    public VBox makeImageTile(Image image, String labelText){
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

        Label label = new Label(labelText);
        label.setMaxWidth(150);

        vBoxout.getChildren().add(label);
        return vBoxout;
    }

    private void changeToSongView(){
        changeView();
        songViewTable.toFront();
        songsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    private void changeToAlbumView(){
        changeView();
        albumScroll.toFront();
        albumsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    private void changeToArtistView(){
        changeView();
        artistScroll.toFront();
        artistsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    private void changeToPlaylistView(){
        changeView();
        playlistScroll.toFront();
        playlistsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    private void changeView() {
        albumsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        songsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        artistsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        playlistsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
    }

    // TODO: INSTEAD OF ALWAYS UPDATING THE VIEW, CHECK FOR CHANGES
    private void updateSongTable(){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(library.getSongs());

        if(songsToAdd.size() > 0)
            songViewTable.setItems(songsToAdd);
    }
    private void updateAlbumTiles(ArrayList<Album> albums){
        albumTiles.getChildren().removeAll(albumTiles.getChildren());

        for (Album i: albums) {
            VBox tile = makeImageTile(i.getCoverArt(), i.getTitle());

            // Maybe add here the handler for each tile
            tile.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 1){
                    updateInnerAlbumView(i);
                    pageVBox.toFront();
                }
            });
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

    private void updateInnerAlbumView(Album album){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(album.getTracklist());

        pageImage.setImage(album.getCoverArt());
        pageText.setText(album.getTitle());

        pageTable.setItems(songsToAdd);
    }
    private void updateInnerArtistView(Artist artist){}
    private void updateInnerPlaylistView(Playlist playlist){}

    public void launchPreferences() throws IOException {
        Stage newStage = new Stage();

        MainGUI.openPreferences(newStage);
    }

/*
    Save and load methods save/load the library
    to the defined savePath as library.json
*/
    public void saveDefaultLibrary() throws Exception{
        String filePath;

        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.startsWith("windows"))
            filePath = Library.settings.getSavePath() + "\\";
        else if (osName.contains("linux"))
            filePath = Library.settings.getSavePath() + "/";
        else // Unsupported OS
            throw new Exception();

        File folder = new File(filePath);
        if(folder.mkdirs())
            System.out.println("Save path already exists!");
        String path = filePath + "library.json";

        library.saveLibrary(path);
    }
    public void loadDefaultLibrary() throws Exception{
        String osName = System.getProperty("os.name").toLowerCase();
        String path;
        try {
            if (osName.startsWith("windows"))
                path = Library.settings.getSavePath() + "\\library.json";
            else if (osName.contains("linux"))
                path = Library.settings.getSavePath() + "/library.json";
            else
                throw new Exception();

            library = Library.loadLibrary(path);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

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

    private void openSelectedSongs(TableView<Music> table){
        ObservableList<Music> songsSelected = table.getSelectionModel().getSelectedItems();
        if(songsSelected.size()>0){
            ArrayList<Music> songsToPlay = new ArrayList<>(songsSelected);
            library.openSongs(songsToPlay);
        }
    }
}