package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGUI extends Application {

    public static Library library;
    public static boolean firstLaunch = true; // This will be a preference somewhere

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader;
        SongController songController;
        HelloController helloController;

        if(firstLaunch) {
            fxmlLoader = new FXMLLoader(getClass().getResource("helloPage.fxml"));
            helloController = fxmlLoader.getController();
        }
        else {
            fxmlLoader = new FXMLLoader(getClass().getResource("songView.fxml"));
            songController = fxmlLoader.getController();
        }

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TURD Music");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        // TODO : create a new library object and open hello-view,
        //  or load previous library from json and start the song view

        try{
            if(firstLaunch)
                library = new Library();
            else
                // Change to default path, perhaps change it within the actual method
                library = Library.loadLibrary("teste.json");
            setLibrary();
        }catch (Exception e){
            System.out.println("Error while loading library");
            e.printStackTrace();
        }

        launch();
    }

    private static void setLibrary(){
        HelloController.library = library;
        PathController.library = library;
        SongController.library = library;
    }
}