package com.testfx.javafxdemo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class Controller {

    @FXML
    public void onButton1Pressed(){

        //Directory Chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();

        File path = directoryChooser.showDialog(null);

        System.out.println("selected path: " + path.getPath());

    }



}