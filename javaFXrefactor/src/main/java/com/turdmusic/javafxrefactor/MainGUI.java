package com.turdmusic.javafxrefactor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("TURD Music");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // TODO : create a new library object and open hello-view,
        //  or load previous library from json and start the song view

        launch();
    }
}