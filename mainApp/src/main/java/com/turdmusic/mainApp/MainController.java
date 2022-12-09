package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// Add multiple options to a single menu item?
// https://stackoverflow.com/questions/69200063/contextmenu-sub-menus-from-a-list-of-strings

 /** Main View Controller Class
  * This is the main UI/UX controller class, here are all the views
  * for the main application window, including
  * song/album/artist/playlist views
  * */
public class MainController {
    // TODO: REMOVE THIS ENUM IF IT DOESN'T PROVE USEFUL
    private enum CurrentView {SongView, AlbumView, ArtistView, PlaylistView, Album, Artist, Playlist};
    private CurrentView state;

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

    public VBox pageBox;
    public Text pageText;
    public TextArea pageArea;
    public ImageView pageImage;

    public TableView<Music> pageTable;
    public TableColumn<Music, Number> pageTrackColumn;
    public TableColumn<Music, String> pageTitleColumn;
    public TableColumn<Music, String> pageArtistColumn;
    public TableColumn<Music, String> pageAlbumColumn;
    public TableColumn<Music, String> pageDurationColumn;

    public void initialize(){
        // Setup both tableView elements
        setupSongTable();
        setupInnerSongTable();

        // Setup context menus for each
        setupTableContext(songViewTable);
        setupTableContext(pageTable);

        updateAll();
        /*// Update the contents for each view
        updateSongTable(library.getSongs());
        updateAlbumTiles(library.getAlbums());
        updateArtistTiles(library.getArtists());*/

        //setupLeftPanel();
    }

    // Setup event handling for the different song tables
    private void setupTableContext(TableView<Music> tableView){
        ContextMenu songContext = new ContextMenu();

        MenuItem mi1 = new MenuItem("Remove from library");
        MenuItem mi2 = new MenuItem("Find missing data");
        Menu mi3 = new Menu("Add to");
        MenuItem mi4 = new MenuItem("Go to Album");
        MenuItem mi5 = new MenuItem("Go to Artist");

        setupTableContextPlaylist(mi3, tableView);

        //songContext.getItems().addAll(mi1, mi2, mi3, mi4, mi5);
        tableView.setContextMenu(songContext);

        // Disable options for multiple song conditions
        tableView.setOnMousePressed(mouseEvent -> {
            ObservableList<Music> songsSelected = tableView.getSelectionModel().getSelectedItems();


            boolean goToAlbum = false, goToArtist = false;
            if(mouseEvent.isSecondaryButtonDown()){
                Album album = songsSelected.get(0).getAlbum();
                Artist artist = songsSelected.get(0).getArtist();

                mi2.setDisable(songsSelected.size() > 1);

                for (int i = 1; i < songsSelected.size(); i++) {
                    if (!songsSelected.get(i).getAlbum().equals(album))
                        goToAlbum = true;
                    if(!songsSelected.get(i).getArtist().equals(artist)){
                        goToArtist = true;
                        break;
                    }
                }
                mi4.setDisable(goToAlbum);
                mi5.setDisable(goToArtist);
            }
        });

        mi4.setOnAction(mouseEvent -> { // Go to album
            pageBox.toFront();
            changeView();
            ObservableList<Music> songsSelected = tableView.getSelectionModel().getSelectedItems();
            if(songsSelected.size()>0){
                ArrayList<Music> songs = new ArrayList<>(songsSelected);
                Album album = songs.get(0).getAlbum();
                updateInnerAlbumView(album);
            }
        });
        mi5.setOnAction(mouseEvent -> { // Go to Artist
            pageBox.toFront();
            changeView();
            ObservableList<Music> songsSelected = tableView.getSelectionModel().getSelectedItems();
            if(songsSelected.size()>0){
                ArrayList<Music> songs = new ArrayList<>(songsSelected);
                Artist artist = songs.get(0).getArtist();
                updateInnerArtistView(artist);
            }
        });

        mi2.setOnAction(mouseEvent -> {
            Music music = tableView.getSelectionModel().getSelectedItems().get(0);
            System.out.println("aaaaa" + music.getTitle());
            try {
                MetaFetchController.results = AcoustidRequester.getMusicInfo(music);

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("metaFetch.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 370, 400);

                stage.setTitle("Select metadata result");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                // Get the result
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No results could be found for the selected song.");
                alert.show();
            }
        });

        songContext.getItems().addAll(mi1, mi2, mi3, mi4, mi5);
    }

    private void setupTableContextPlaylist(Menu menu, TableView tableView) {
        /*MenuItem mi0 = new MenuItem("New Playlist");
        SeparatorMenuItem sptr = new SeparatorMenuItem();
        menu.getItems().addAll(mi0, sptr);
        for (Playlist i: library.getPlaylists()){
            MenuItem mi1 = new MenuItem(i.getTitle());
            menu.getItems().add(mi1);
            mi1.setOnAction(mouseEvent -> {
                ObservableList<Music> songsSelected = tableView.getSelectionModel().getSelectedItems();
                if(songsSelected.size()>0){
                    ArrayList<Music> songs = new ArrayList<>(songsSelected);
                    //Nao esta a funcionar ainda:
                    //i.getTracklist().add(songs);
                }
            });
        }

        MenuItem mi2 = new MenuItem("Playlist 1");
        menu.getItems().add(mi2);*/
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
        pageTrackColumn.setCellValueFactory(param -> new ReadOnlyIntegerWrapper(param.getValue().getTrackNumber()));
        pageArtistColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getArtist().getName()));
        pageAlbumColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getAlbum().getTitle()));

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

    public void changeToSongView(){
        changeView();
        songViewTable.toFront();
        songsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    public void changeToAlbumView(){
        changeView();
        albumScroll.toFront();
        albumsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    public void changeToArtistView(){
        changeView();
        artistScroll.toFront();
        artistsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
    }
    public void changeToPlaylistView(){
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
    public void updateAll(){
        // Update the contents for each view
        updateSongTable(library.getSongs());
        updateAlbumTiles(library.getAlbums());
        updateArtistTiles(library.getArtists());
    }
    public void updateSongTable(ArrayList<Music> music){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(music);

        //songViewTitleColumn.setVisible(false);
        if(songsToAdd.size() > 0)
            songViewTable.setItems(songsToAdd);
    }
    public void updateAlbumTiles(ArrayList<Album> albums){
        albumTiles.getChildren().removeAll(albumTiles.getChildren());

        for (Album i: albums) {
            VBox tile = makeImageTile(i.getCoverArt(), i.getTitle());

            // Maybe add here the handler for each tile
            tile.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 1){
                    updateInnerAlbumView(i);
                    pageBox.toFront();
                    changeView();
                }
            });
            albumTiles.getChildren().add(tile);
        }
    }
    public void updateArtistTiles(ArrayList<Artist> artists){
        artistTiles.getChildren().removeAll(artistTiles.getChildren()); //Clear

        for(Artist i: artists){
            VBox tile = makeImageTile(i.getPicture(), i.getName());

            tile.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getClickCount() == 1){
                    updateInnerArtistView(i);
                    pageBox.toFront();
                }
            });
            artistTiles.getChildren().add(tile);
        }
    }
    public void updatePlaylistTiles(ArrayList<Playlist> playlists){
        // TODO: IMPLEMENT
    }

    private void updateInnerAlbumView(Album album){
        pageDurationColumn.setVisible(true);
        pageTitleColumn.setVisible(true);
        pageAlbumColumn.setVisible(false);
        pageArtistColumn.setVisible(false);
        pageTrackColumn.setVisible(true);

        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(album.getTracklist());

        pageImage.setImage(album.getCoverArt());
        pageText.setText(album.getTitle());

        pageTable.setItems(songsToAdd);
    }
    private void updateInnerArtistView(Artist artist){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(artist.getSongs());

        pageDurationColumn.setVisible(true);
        pageTitleColumn.setVisible(true);
        pageAlbumColumn.setVisible(true);
        pageArtistColumn.setVisible(false);
        pageTrackColumn.setVisible(false);

        pageImage.setImage(artist.getPicture());
        pageText.setText(artist.getName());

        pageTable.setItems(songsToAdd);

    }
    private void updateInnerPlaylistView(Playlist playlist){}

    public void launchPreferences() throws IOException {
        Stage newStage = new Stage();

        MainGUI.openPreferences(newStage);

    }

     public void launchFolder() throws IOException {
         Stage newStage = new Stage();
         MainGUI.openFolderPage(newStage);
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