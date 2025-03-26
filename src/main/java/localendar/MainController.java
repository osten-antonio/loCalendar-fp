package localendar;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


public class MainController {
    @FXML
    private VBox testbox;


    private int count=0;

    @FXML
    protected void openCreationScreen(){
        // TODO here
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("test");
        alert.setHeaderText("test");
        alert.showAndWait();
    }

    @FXML
    protected void test() {
        ArrayList<String> testDatabase = Database.test();
        testbox.getChildren().clear();
        for (String entry : testDatabase) {
            Label label = new Label(entry);
            testbox.getChildren().add(label); // Ignore error this works
        }
    }
}