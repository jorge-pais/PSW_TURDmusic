package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainGUI extends Application {

    public static Library library;
    public static Settings settings;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader;

        if(settings.getFirstLaunch())
            fxmlLoader = new FXMLLoader(getClass().getResource("helloPage.fxml"));
        else
            fxmlLoader = new FXMLLoader(getClass().getResource("songView.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TURD Music");
        stage.setScene(scene);
        stage.show();

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
    }
}