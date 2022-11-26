package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    protected Label label1;
    protected Scene scene;
    public static Library library;

    //public StackPane stackPane;

    public void initialize(){
        // When this controller's scene is loaded into the stage
        // this method is the first being executed
        this.scene = label1.getScene();
    }

    @FXML
    protected void onMouseClicked() throws IOException {
        // Create a new stage (window) and load the file selection scene
        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pathManager.fxml"));

        PathController pathController = fxmlLoader.getController();

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        newStage.setTitle("Select Folders");
        newStage.setScene(scene);

        // Change the new window's modality
        // block input from all other application windows
        // TODO: investigate context menu buttons
        newStage.initModality(Modality.APPLICATION_MODAL);

        newStage.showAndWait();

        if(PathController.addedFolder) {
            System.out.println("Paths have been added");

            // Change to music view
            try{
                fxmlLoader = new FXMLLoader(getClass().getResource("songView.fxml"));
                Stage stage = (Stage) label1.getScene().getWindow();
                Scene newScene = new Scene(fxmlLoader.load());
                stage.setScene(newScene);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onKeyPressed(){


    }
}