package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import com.turdmusic.mainApp.core.models.ImageInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainGUI extends Application {

    public static Library library;
    public static Settings settings;



    @Override
    public void start(Stage stage) throws IOException {
        createStage(stage);
    }

    public static void createStage(Stage stage) throws IOException {
        String sceneToOpen;

        if(settings.getFirstLaunch())
            sceneToOpen = "helloPage.fxml";
        else
            sceneToOpen = "songView.fxml";

        FXMLLoader loader = new FXMLLoader(MainGUI.class.getResource(sceneToOpen));
        Scene scene = new Scene(loader.load());
        stage.setTitle("TURD Music");
        stage.setMinHeight(300);
        stage.setMinWidth(600);
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
        // TODO: investigate context menu buttons
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
                library = Library.loadLibrary("teste.json");

            library.settings = settings; // There's probably a more elegant way of doing this
            setLibrary();
        }catch (Exception e){
            System.out.println("Error while loading library");
            e.printStackTrace();
        }

        launch();
    }

    // Add static library reference to all of JavaFX controllers
    private static void setLibrary(){
        HelloController.library = library;
        PathController.library = library;
        SongController.library = library;
        PreferenceController.library = library;
        ImageInfo.settings = settings;
    }
}