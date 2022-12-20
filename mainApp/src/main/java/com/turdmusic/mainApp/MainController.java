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

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*TODO:
* Add on Table Context the option of reproduce music
* Eliminate on Table Context the "go to Album" inside album
and "go to Artist" inside artist
*/
 /** Main View Controller Class
  * This is the main UI/UX controller class, here are all the views
  * for the main application window, including
  * song/album/artist/playlist views
  */
public class MainController {
    private enum CurrentView {SongView, AlbumView, ArtistView, PlaylistView, Album, Artist, Playlist}
    private CurrentView state;

    public static Library library;

    public VBox allPage;

    //Refresh Menu
    public MenuItem allRefreshMenu;
    public MenuItem songRefreshMenu;
    public MenuItem albumRefreshMenu;
    public MenuItem artistRefreshMenu;
    public MenuItem playlistRefreshMenu;

    // Search
    public TextField searchField;
    public Label searchLabel;

    // Button
    public Label songsLabelButton;
    public Label albumsLabelButton;
    public Label artistsLabelButton;
    public Label playlistsLabelButton;

    //Song View
    public TableView<Music> songViewTable;
    public TableColumn<Music, String> songViewTitleColumn;
    public TableColumn<Music, String> songViewArtistColumn;
    public TableColumn<Music, String> songViewAlbumColumn;
    public TableColumn<Music, String> songViewDurationColumn;

    //Album View
    public ScrollPane albumScroll;
    public TilePane albumTiles;
    public TilePane playlistTiles;

    //Artist View
    public ScrollPane artistScroll;
    public TilePane artistTiles;

    //Playlist View
    public ScrollPane playlistScroll;

    //Individual View:  Album, Artist, Playlist
    public VBox pageBox;
    public Text pageTitleText;
    public Label descriptionLabel;
    public ImageView pageImage;
    public Button playButton;

    public TableView<Music> pageTable;
    public TableColumn<Music, Number> pageTrackColumn;
    public TableColumn<Music, String> pageTitleColumn;
    public TableColumn<Music, String> pageArtistColumn;
    public TableColumn<Music, String> pageAlbumColumn;
    public TableColumn<Music, String> pageDurationColumn;

    public void initialize(){
        // Setup both table elements
        setupSongTable();
        setupInnerSongTable();

        // Setup short-menus for each table
        setupTableContext(songViewTable);
        setupTableContext(pageTable);

        // Update the contents for each view
        updateAll();

        changeToSongView();

        setupRefresh();
        setupSearch();
    }

    private void setupSongTable(){
        //Set up the Song View
        songViewTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
            if(keyEvent.getCode() == KeyCode.ENTER)
                openSelectedSongs(songViewTable);
        });
    }
    private void setupInnerSongTable(){
        //Set up the Individual View
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

    // Setup event handling for the different song tables
    private void setupTableContext(TableView<Music> tableView){
        //Create the short-menu
        ContextMenu songContext = new ContextMenu();
        MenuItem mi1 = new MenuItem("Remove from library");
        MenuItem mi2 = new MenuItem("Find missing data");
        MenuItem mi3 = new MenuItem("Edit");
        Menu playlistOptions = new Menu("Add to");
        MenuItem mi4 = new MenuItem("Go to Album");
        MenuItem mi5 = new MenuItem("Go to Artist");

        updatePlaylistContext(playlistOptions, tableView);

        tableView.setContextMenu(songContext);

        // Disable options for multiple selected songs conditions
        tableView.setOnMousePressed(mouseEvent -> {
            ObservableList<Music> songsSelected = tableView.getSelectionModel().getSelectedItems();

            boolean goToAlbum = false, goToArtist = false;
            if(mouseEvent.isSecondaryButtonDown()){
                Album album = songsSelected.get(0).getAlbum();
                Artist artist = songsSelected.get(0).getArtist();

                mi2.setDisable(songsSelected.size() > 1);
                mi3.setDisable(songsSelected.size() > 1);

                //Disable go to Album and go to Artist when multiselect different albums or artists
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

        // Go to album page
        mi4.setOnAction(actionEvent -> {
            ObservableList<Music> songsSelected = tableView.getSelectionModel().getSelectedItems();
            if(songsSelected.size()>0){
                ArrayList<Music> songs = new ArrayList<>(songsSelected);
                Album album = songs.get(0).getAlbum();
                updateInnerAlbumView(album);
                pageBox.toFront();
                changeView();
                state = CurrentView.Album;
            }
        });
        // Go to Artist page
        mi5.setOnAction(actionEvent -> {
            ObservableList<Music> songsSelected = tableView.getSelectionModel().getSelectedItems();
            if(songsSelected.size()>0){
                ArrayList<Music> songs = new ArrayList<>(songsSelected);
                Artist artist = songs.get(0).getArtist();
                updateInnerArtistView(artist);
                pageBox.toFront();
                changeView();
                state = CurrentView.Artist;
            }
        });
        // Find missing data
        mi2.setOnAction(actionEvent -> {
            Music music = tableView.getSelectionModel().getSelectedItems().get(0);
            try {
                MetaFetchController.results = AcoustidRequester.getMusicInfo(music);
                MetaFetchController.song = music;

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("metaFetch.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 370, 400);

                stage.setTitle("Select metadata result");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                updateAll();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "No results could be found for the selected song.");
                alert.show();
            }
        });
        // Edit song
        mi3.setOnAction(actionEvent -> {
            Music music = tableView.getSelectionModel().getSelectedItems().get(0);
            try {
                Stage stage = new Stage();

                SongEditController.music = music;
                MainGUI.openEditMenu(stage);

                updateAll();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        songContext.getItems().addAll(mi1, mi2, playlistOptions, mi3, mi4, mi5);
    }

    private void updatePlaylistContext(Menu menu, TableView<Music> tableView) {
        menu.getItems().removeAll(menu.getItems()); //Clear

        MenuItem mi0 = new MenuItem("New Playlist");
        mi0.setOnAction(actionEvent -> {
            ObservableList<Music> songsToAdd = tableView.getSelectionModel().getSelectedItems();
            ArrayList<Music> music = new ArrayList<>(songsToAdd);

            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setHeaderText("Enter your new playlist's title");
            textInputDialog.showAndWait();

            String name = textInputDialog.getResult();
            if (name != null) {
                Playlist playlist = new Playlist(name, music);
                library.getPlaylists().add(playlist);
                updatePlaylistTiles(library.getPlaylists());

                // Refresh the context menu
                setupTableContext(songViewTable); //Major event handler Spaghetti code
                setupTableContext(pageTable);
            }
        });

        menu.getItems().add(mi0);
        if(library.getPlaylists().size() == 0) return;

        // Add the remaining playlist items
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        menu.getItems().add(separatorMenuItem);

        for(Playlist i: library.getPlaylists()){
            MenuItem item = new MenuItem(i.getTitle());

            item.setOnAction(actionEvent -> {
                ObservableList<Music> songsSelected = tableView.getSelectionModel().getSelectedItems();
                i.getTracklist().addAll(new ArrayList<>(songsSelected));

                updatePlaylistTiles(library.getPlaylists());
            });

            menu.getItems().add(item);
        }
    }

    private VBox makeImageTile(Image image, String labelText){
        VBox vBoxout = new VBox();
        vBoxout.prefHeight(200);
        vBoxout.prefWidth(200);
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
        label.setAlignment(Pos.CENTER);

        vBoxout.getChildren().add(label);
        return vBoxout;
    }

    public void changeToSongView(){
        changeView();
        songViewTable.toFront();
        songsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
        state = CurrentView.SongView;

        searchField.setDisable(false);
    }
    public void changeToAlbumView(){
        changeView();
        albumScroll.toFront();
        albumsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
        state = CurrentView.AlbumView;

        searchField.setDisable(false);
    }
    public void changeToArtistView(){
        changeView();
        artistScroll.toFront();
        artistsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
        state = CurrentView.ArtistView;
        searchField.setDisable(false);
    }
    public void changeToPlaylistView(){
        changeView();
        playlistScroll.toFront();
        playlistsLabelButton.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 18));
        state = CurrentView.PlaylistView;
        searchField.setDisable(false);
    }
    private void changeView() {
        albumsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        songsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        artistsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));
        playlistsLabelButton.setFont(Font.font("System", FontWeight.NORMAL, FontPosture.REGULAR, 18));

        searchField.clear(); // It's not a bug it's a feature
        searchLabel.setText("");
    }

    private void updateSongTable(ArrayList<Music> music){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(music);

        //songViewTitleColumn.setVisible(false);
        if(songsToAdd.size() > 0)
            songViewTable.setItems(songsToAdd);
    }
    private void updateAlbumTiles(ArrayList<Album> albums){
        albumTiles.getChildren().clear();

        for (Album i: albums) {
            VBox tile = makeImageTile(i.getCoverArt(), i.getTitle());

            // Maybe add here the handler for each tile
            tile.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 1){
                    updateInnerAlbumView(i);
                    pageBox.toFront();
                    changeView();
                    state = CurrentView.Album;
                }
            });
            albumTiles.getChildren().add(tile);
        }
    }
    private void updateArtistTiles(ArrayList<Artist> artists){
        artistTiles.getChildren().removeAll(artistTiles.getChildren()); //Clear

        for(Artist i: artists){
            VBox tile = makeImageTile(i.getPicture(), i.getName());

            tile.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getClickCount() == 1){
                    updateInnerArtistView(i);
                    pageBox.toFront();
                    changeView();
                    state = CurrentView.Artist;
                }
            });
            artistTiles.getChildren().add(tile);
        }
    }
    private void updatePlaylistTiles(ArrayList<Playlist> playlists){
        playlistTiles.getChildren().removeAll(playlistTiles.getChildren());

        for (Playlist i: playlists){
            VBox tile = makeImageTile(i.getPlaylistPicture(), i.getTitle());

            ContextMenu contextMenu = new ContextMenu();
            MenuItem item = new MenuItem("Delete playlist");
            MenuItem remove = new MenuItem("Rename playlist");
            contextMenu.getItems().add(item);
            contextMenu.getItems().add(remove);

            changeNamePlaylist(i, remove);
            item.setOnAction(actionEvent -> {
                library.getPlaylists().remove(i);
                updatePlaylistTiles(library.getPlaylists());

                setupTableContext(songViewTable); //Major event handler Spaghetti code
                setupTableContext(pageTable);
            });

            tile.setOnContextMenuRequested(contextMenuEvent -> contextMenu.show(tile, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));

            tile.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getClickCount() == 1 && mouseEvent.getButton() == MouseButton.PRIMARY){
                    updateInnerPlaylistView(i);
                    pageBox.toFront();
                    state = CurrentView.Playlist;
                    changeView();
                }
            });
            playlistTiles.getChildren().add(tile);
        }
    }
    public void updateAll(){
        // Update the contents for each view
        updateSongTable(library.getSongs());
        updateAlbumTiles(library.getAlbums());
        updateArtistTiles(library.getArtists());
        updatePlaylistTiles(library.getPlaylists());
    }

    private void changeNamePlaylist(Playlist playlist, MenuItem remove){
        remove.setOnAction(actionEvent -> {
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setHeaderText("Rename the playlist's title");
            textInputDialog.showAndWait();

            String name = textInputDialog.getResult();
            if (name != null) {
                ArrayList<Music> music = playlist.getTracklist();
                library.getPlaylists().remove(playlist);
                Playlist replace_playlist = new Playlist(name, music);
                library.getPlaylists().add(replace_playlist);

                updatePlaylistTiles(library.getPlaylists());
                // Refresh the context menu
                setupTableContext(songViewTable); //Major event handler Spaghetti code
                setupTableContext(pageTable);
            }
        });
    }
    private void setupRefresh() {
        allRefreshMenu.setOnAction(actionEvent -> updateAll());
        songRefreshMenu.setOnAction(actionEvent -> updateSongTable(library.getSongs()));
        albumRefreshMenu.setOnAction(actionEvent -> updateAlbumTiles(library.getAlbums()));
        artistRefreshMenu.setOnAction(actionEvent -> updateArtistTiles(library.getArtists()));
        playlistRefreshMenu.setOnAction(actionEvent -> updatePlaylistTiles(library.getPlaylists()));

        //Refresh all using the F5 key press
        allPage.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.F5)
                updateAll();
        });
    }

    public void setupSearch(){
        searchField.setOnKeyPressed(keyEvent -> {
            String searchText = searchField.getText();

            if(searchText.isEmpty()) {
                searchLabel.setText("");
                updateAll();
            }
            else
                switch(state) {
                    case SongView:
                        searchSongTable(searchText);
                        break;
                    case AlbumView:
                        searchAlbumTiles(searchText);
                        break;
                    case ArtistView:
                        searchArtistTiles(searchText);
                        break;
                    case Playlist:
                        searchPlaylistTiles(searchText);
                    default:
                        break;
                }
        });
    }
    public void searchSongTable(String searchText){
        ArrayList<Music> results = library.searchSongs(searchText);

        if(results != null) {
            updateSongTable(results);
            searchLabel.setText(results.size() + " matching result were found!");
        }
        else if(state == CurrentView.SongView) {
            updateSongTable(library.getSongs());
            searchLabel.setText("No matching result was found");
        }

    }
    public void searchAlbumTiles(String searchText){
        ArrayList<Album> results = library.searchAlbums(searchText);

        if(results != null) {
            updateAlbumTiles(results);
            searchLabel.setText(results.size() + " matching result were found");
        }
        else if(state == CurrentView.AlbumView)
            searchLabel.setText("No matching result was found");

    }
    public void searchArtistTiles(String searchText){
        ArrayList<Artist> results = library.searchArtists(searchText);

        if(results != null) {
            updateArtistTiles(results);
            searchLabel.setText(results.size() + " matching result were found");
        }
        else if(state == CurrentView.ArtistView)
            searchLabel.setText("No matching result was found");
     }
    public void searchPlaylistTiles(String searchText){
         ArrayList<Playlist> results = library.searchPlaylist(searchText);
         if(results != null){
             updatePlaylistTiles(results);
             searchLabel.setText(results.size() + " matching result were found");
         }
         else if(state == CurrentView.PlaylistView)
             searchLabel.setText("No matching result was found");

     }

    private void updateInnerAlbumView(Album album){
        pageDurationColumn.setVisible(true);
        pageTitleColumn.setVisible(true);
        pageAlbumColumn.setVisible(false);
        pageArtistColumn.setVisible(false);
        pageTrackColumn.setVisible(true);

        // Disable search
        searchField.setDisable(true);

        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(album.getTracklist());

        pageImage.setImage(album.getCoverArt());
        pageTitleText.setText(album.getTitle());

        pageTable.setItems(songsToAdd);

        String description = "Album by " + album.getArtist().getName() + "\n" +
                album.getTracklist().size() + " songs";

        descriptionLabel.setText(description);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Change to default");
        MenuItem item2 = new MenuItem("Add a picture");
        contextMenu.getItems().addAll(item1, item2);
        item2.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if(file != null) {
                try {
                    if (Utils.checkFileExtension(file.getPath(), Utils.fileType.Image))
                        album.setPicture(ImageIO.read(file));
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Some error happened while loading the image!").showAndWait();
                }
                updateInnerAlbumView(album);
                updateAlbumTiles(library.getAlbums());
            }
        });
        item1.setOnAction(actionEvent -> {
            album.setPicture(null);
            updateInnerAlbumView(album);
            updateAlbumTiles(library.getAlbums());
        });
        pageImage.setOnContextMenuRequested(contextMenuEvent -> contextMenu.show(pageImage, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));

        playButton.setOnAction(actionEvent -> library.openSongs(album.getTracklist()));
    }
    private void updateInnerArtistView(Artist artist){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(artist.getSongs());

        pageDurationColumn.setVisible(true);
        pageTitleColumn.setVisible(true);
        pageAlbumColumn.setVisible(true);
        pageArtistColumn.setVisible(false);
        pageTrackColumn.setVisible(false);

        // Disable search
        searchField.setDisable(true);

        pageImage.setImage(artist.getPicture());
        pageTitleText.setText(artist.getName());

        pageTable.setItems(songsToAdd);

        String description = artist.getSongs().size() + " songs \n" +
                artist.getAlbums().size() + " albums";

        descriptionLabel.setText(description);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Change to default");
        MenuItem item2 = new MenuItem("Add a picture");
        contextMenu.getItems().addAll(item1, item2);
        item2.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if(file != null) {
                try {
                    if (Utils.checkFileExtension(file.getPath(), Utils.fileType.Image))
                        artist.setPicture(ImageIO.read(file));
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Some error happened while loading the image!").showAndWait();
                }
                updateInnerArtistView(artist);
                updateArtistTiles(library.getArtists());
            }
        });
        item1.setOnAction(actionEvent -> {
            artist.setPicture(null);
            updateInnerArtistView(artist);
            updateArtistTiles(library.getArtists());
        });
        pageImage.setOnContextMenuRequested(contextMenuEvent -> contextMenu.show(pageImage, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));

        playButton.setOnAction(actionEvent -> library.openSongs(artist.getSongs()));
    }
    private void updateInnerPlaylistView(Playlist playlist){
        ObservableList<Music> songsToAdd = FXCollections.observableArrayList();
        songsToAdd.addAll(playlist.getTracklist());

        pageDurationColumn.setVisible(true);
        pageTitleColumn.setVisible(true);
        pageAlbumColumn.setVisible(true);
        pageArtistColumn.setVisible(true);
        pageTrackColumn.setVisible(false);

        searchField.setDisable(true);

        pageImage.setImage(playlist.getPlaylistPicture());
        pageTitleText.setText(playlist.getTitle());

        pageTable.setItems(songsToAdd);

        String description = playlist.getTracklist().size() + " songs\n" +
                "Created on " + playlist.getDateCreated().toString();

        descriptionLabel.setText(description);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Change to default");
        MenuItem item2 = new MenuItem("Add a picture");
        contextMenu.getItems().addAll(item1, item2);
        item2.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if(file != null) {
                try {
                    if (Utils.checkFileExtension(file.getPath(), Utils.fileType.Image))
                        playlist.setPicture(ImageIO.read(file));
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, "Some error happened while loading the image!").showAndWait();
                }
                updateInnerPlaylistView(playlist);
                updatePlaylistTiles(library.getPlaylists());
            }
        });
        item1.setOnAction(actionEvent -> {
            playlist.setPicture(null);
            updateInnerPlaylistView(playlist);
            updatePlaylistTiles(library.getPlaylists());
        });
        pageImage.setOnContextMenuRequested(contextMenuEvent -> contextMenu.show(pageImage, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY()));

        playButton.setOnAction(actionEvent -> library.openSongs(playlist.getTracklist()));
    }

    public void launchPreferences() throws IOException {
        Stage newStage = new Stage();
        Stage stage = (Stage) allPage.getScene().getWindow();
        MainGUI.openPreferences(newStage);

        //If not exist paths is come back to the heloPage
        if(!MainGUI.existPaths()){
            MainGUI.createMainStage(stage);
        }
    }

    public void launchFolder() throws IOException {
        Stage newStage = new Stage();
        Stage stage = (Stage) allPage.getScene().getWindow();
        MainGUI.openFolderPage(newStage);

        //If not exist paths is come back to the heloPage
        if(!MainGUI.existPaths()){
            MainGUI.createMainStage(stage);
        }
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
            MainGUI.setControllerReferences(library, new Settings());

            updateAll();
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

    public void finishButtonClicked(){
        Stage stage = (Stage) songViewTable.getScene().getWindow();
        stage.close();
    }
}