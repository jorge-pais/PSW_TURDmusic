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

    opens com.turdmusic.javafxrefactor to javafx.fxml;
    opens com.turdmusic.javafxrefactor.core to com.fasterxml.jackson.databind;
    exports com.turdmusic.javafxrefactor;
    exports com.turdmusic.javafxrefactor.core to com.fasterxml.jackson.databind;
}