module localendar.localendar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.compiler;
    requires jdk.incubator.vector;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires commons.math3;

    opens localendar to javafx.fxml;
    exports localendar;
    exports localendar.WidgetControllers;
    opens localendar.WidgetControllers to javafx.fxml;
}