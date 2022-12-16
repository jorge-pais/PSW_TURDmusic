package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import com.turdmusic.mainApp.core.models.ImageInfo;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/** Contains the main program entry point, along with some GUI methods
 * utilized by other controller objects
 * */
public class MainGUI extends Application {

    public static Library library;
    public static Settings settings;

    @Override
    public void start(Stage stage) throws IOException {
        createMainStage(stage);
    }

    // TODO: alter the function name
    public static void createMainStage(Stage stage) throws IOException {
        String sceneToOpen;

        if(settings.getFirstLaunch())
            sceneToOpen = "helloPage.fxml";
        else
            sceneToOpen = "mainPage.fxml";

        FXMLLoader loader = new FXMLLoader(MainGUI.class.getResource(sceneToOpen));
        Scene scene = new Scene(loader.load());
        stage.setTitle("TURD Music");
        stage.getIcons().add(getAppIcon());
        stage.setMinHeight(600);
        stage.setMinWidth(900);
        stage.setScene(scene);
        stage.show();
    }

    private static Image getAppIcon() {
        InputStream imageStream = MainGUI.class.getResourceAsStream("/com/turdmusic/mainApp/icons/logo.png");
        assert imageStream != null;
        Image image = new Image(imageStream);
        return image;
    }

    public static void openFolderPage(Stage stage, Stage newStage) throws IOException {
        FXMLLoader loaderPathManager = new FXMLLoader(MainGUI.class.getResource("folderPage.fxml"));
        String name = "Select Folders";
        createNewStage(newStage, loaderPathManager, name);
        ObservableList<String> items = FXCollections.observableArrayList();
        items.setAll(library.getLibraryPaths());
        if((items.isEmpty())) {
            // Mark the first launch here
            Library.settings.setFirstLaunch(true);
            System.out.println("Paths have been added");
        }
        else {
            Library.settings.setFirstLaunch(false);
        }
        // Change the page
        if(stage!=null) {
            try {
                createMainStage(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openPreferences(Stage newStage) throws IOException {
        FXMLLoader loaderPreferencesView = new FXMLLoader(MainGUI.class.getResource("preferenceView.fxml"));
        String name = "Preferences";
        createNewStage(newStage, loaderPreferencesView, name);
    }

    private static void createNewStage(Stage newStage, FXMLLoader loaderPathManager, String name) throws IOException {
        Scene scene = new Scene(loaderPathManager.load());
        newStage.setResizable(false);
        newStage.setTitle(name);
        newStage.getIcons().add(getAppIcon());
        newStage.setScene(scene);

        // Change the new window's modality
        // block input from all other application windows
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.showAndWait();
    }

    public static void openEditMenu(Stage newStage) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("songEdit.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 302, 214);
        newStage.setScene(scene);
        newStage.setResizable(false);

        newStage.setTitle("Edit song entry");
        newStage.getIcons().add(getAppIcon());
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.showAndWait();
    }

    public static void main(String[] args) {

        // Load the settings object containing the preferences
        settings = new Settings();

        // Test from the first launch
        //settings.setFirstLaunch(true);

        try{
            if(settings.getFirstLaunch())
                library = new Library();
            else // TODO: update this to utilize library path from preferences
                library = Library.loadLibrary(settings.getSavePath() + "/library.json");

        }catch (Exception e){
            library = new Library();
            settings.setFirstLaunch(true);
            System.out.println("Error while loading library, reverting to default settings");
            //e.printStackTrace();
        }
        setControllerReferences(library, settings);

        launch();
    }

    // Add static library reference to all of JavaFX controllers
    private static void setControllerReferences(Library library, Settings settings){
        HelloController.library = library;
        FolderController.library = library;
        MainController.library = library;
        PreferenceController.library = library;
        PreferenceController.settings = settings;
        Library.settings = settings;
        ImageInfo.settings = settings;
        AcoustidRequester.settings = settings;
        SongEditController.library = library;
    }
}