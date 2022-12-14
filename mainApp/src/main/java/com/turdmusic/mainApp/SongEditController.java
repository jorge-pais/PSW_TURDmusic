package com.turdmusic.mainApp;

import com.turdmusic.mainApp.core.Library;
import com.turdmusic.mainApp.core.Music;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.io.File;

public class SongEditController {

    public TextField titleText;
    public TextField albumText;
    public TextField artistText;
    public TextField trackText;

    public static Library library;
    public static Music music;

    public void initialize(){
        titleText.setText(music.getTitle());
        albumText.setText(music.getAlbum().getTitle());
        artistText.setText(music.getArtist().getName());
        trackText.setText(Integer.toString(music.getTrackNumber()));

        // Make it so that the track number filed only accepts digits
        trackText.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if(newText.matches("\\d*")){
                return change;
            }
            return null;
        }));
    }

    public void pressedOK(){
        if( titleText.getText().length() == 0 ||
            albumText.getText().length() == 0 ||
            artistText.getText().length() == 0 ||
            trackText.getText().length() == 0)
            {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Parameters are wrong");
            alert.show();
        }

        int track = Integer.parseInt(trackText.getText());

        File file = music.getFile();
        library.removeSong(music);

        Music newSong = library.createSongEntry(artistText.getText(), albumText.getText(), titleText.getText(), file, library.getSongs().size(), track);

        library.getSongs().add(newSong);

        pressedCancel();
    }

    public void pressedCancel(){
        ((Stage) titleText.getScene().getWindow()).close();
    }
}
