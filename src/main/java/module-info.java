module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi;

    opens org.openjfx to javafx.fxml;
    exports org.openjfx;
}
