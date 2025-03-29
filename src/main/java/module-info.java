module localendar.localendar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires com.dlsc.formsfx;
    requires java.desktop;

    opens localendar to javafx.fxml;
    exports localendar;
}