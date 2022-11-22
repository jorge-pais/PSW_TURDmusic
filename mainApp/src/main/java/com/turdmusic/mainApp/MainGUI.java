package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGUI extends Application {

    public static Library library;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setTitle("TURD Music");
        stage.setScene(scene);
        stage.show();

        //GUIController guiController = new GUIController();
    }

    public static void main(String[] args) {
        // TODO : create a new library object and open hello-view,
        //  or load previous library from json and start the song view

        launch();
    }
}