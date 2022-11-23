package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainGUI extends Application {

    public static Library library;
    public boolean firstStart = false; // This will be a preference somewhere

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader;
        SongController songController;
        HelloController helloController;

        if(firstStart) {
            fxmlLoader = new FXMLLoader(getClass().getResource("helloPage.fxml"));
            helloController = fxmlLoader.getController();
            HelloController.library = library;
        }
        else {
            fxmlLoader = new FXMLLoader(getClass().getResource("songView.fxml"));
            songController = fxmlLoader.getController();
            SongController.library = library;
        }

        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setTitle("TURD Music");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        // TODO : create a new library object and open hello-view,
        //  or load previous library from json and start the song view

        try{
            library = Library.loadLibrary("teste.json");
        }catch (Exception e){
            System.out.println("Error while loading library");
            e.printStackTrace();
        }

        launch();
    }
}