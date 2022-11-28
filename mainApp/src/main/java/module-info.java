module com.turdmusic.javafxrefactor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.annotation;
    requires jaudiotagger;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires java.net.http;
    requires java.prefs;
    requires com.google.gson;

    opens com.turdmusic.mainApp to javafx.fxml;
    opens com.turdmusic.mainApp.core to com.fasterxml.jackson.databind;
    exports com.turdmusic.mainApp;
    exports com.turdmusic.mainApp.core to com.fasterxml.jackson.databind;
}