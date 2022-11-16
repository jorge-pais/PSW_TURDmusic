module com.testfx.javafxdemo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.testfx.javafxdemo to javafx.fxml;
    exports com.testfx.javafxdemo;
}