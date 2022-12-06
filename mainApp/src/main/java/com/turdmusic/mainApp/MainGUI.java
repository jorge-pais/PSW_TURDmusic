package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import com.turdmusic.mainApp.core.models.ImageInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/** Contains the main program entry point, along with some GUI methods
 * utilized by other controller objects
 * */
public class MainGUI extends Application {

    public static Library library;
    public static Settings settings;

    @Override
    public void start(Stage stage) throws IOException {
        createStage(stage);
    }

    // TODO: alter the function name
    public static void createStage(Stage stage) throws IOException {
        String sceneToOpen;

        if(settings.getFirstLaunch())
            sceneToOpen = "helloPage.fxml";
        else
            sceneToOpen = "songView.fxml";

        FXMLLoader loader = new FXMLLoader(MainGUI.class.getResource(sceneToOpen));
        Scene scene = new Scene(loader.load());
        stage.setTitle("TURD Music");
        stage.setMinHeight(600);
        stage.setMinWidth(900);
        stage.setScene(scene);
        stage.show();
    }

    public static void openPathManager(Stage newStage) throws IOException {
        FXMLLoader loaderPathManager = new FXMLLoader(MainGUI.class.getResource("pathManager.fxml"));
        Scene scene = new Scene(loaderPathManager.load(), 600, 400);
        newStage.setTitle("Select Folders");
        newStage.setScene(scene);

        // Change the new window's modality
        // block input from all other application windows
        newStage.initModality(Modality.APPLICATION_MODAL);

        newStage.showAndWait();
    }

    public static void openPreferences(Stage newStage) throws IOException {
        FXMLLoader loaderPreferencesView = new FXMLLoader(MainGUI.class.getResource("preferenceView.fxml"));
        Scene scene = new Scene(loaderPreferencesView.load());
        newStage.setScene(scene);
        newStage.setResizable(false);

        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.showAndWait();
    }

    public static void closePathManager(Stage newStage) {
        newStage.close();
    }

    public static void closePreferenceController(Stage newStage) {
        newStage.close();
    }
    public static void main(String[] args) {

        // Load the settings object containing the preferences
        settings = new Settings();

        // Test from the first launch
        settings.setFirstLaunch(true);

        try{
            if(settings.getFirstLaunch())
                library = new Library();
            else // TODO: update this to utilize library path from preferences
                library = Library.loadLibrary(settings.getSavePath() + "/library.json");

            Library.settings = settings; // There's probably a more elegant way of doing this
            setLibrary(library);
        }catch (Exception e){
            System.out.println("Error while loading library");
            e.printStackTrace();
        }

        launch();
    }

    // Add static library reference to all of JavaFX controllers
    private static void setLibrary(Library library){
        HelloController.library = library;
        FolderSelection.library = library;
        MainView.library = library;
        PreferenceController.library = library;
        ImageInfo.settings = settings;
    }
}