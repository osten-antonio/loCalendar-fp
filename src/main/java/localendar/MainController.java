package localendar;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.text.Text;




public class MainController implements Initializable {
    @FXML
    private VBox testbox;

    @FXML
    private ComboBox<String> sort_priority;

    @FXML
    private ComboBox<String> sort_due;

    @FXML
    private ComboBox<String> sort_time;

    private int count = 0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sort_priority.setItems( FXCollections.observableArrayList("↕    Priority",
                "↓    Highest to Lowest",
                "↑    Lowest to Highest"));

        sort_due.setItems(FXCollections.observableArrayList("\uD83D\uDCC6   Due date",
                "↓     Highest to Lowest",
                "↑     Lowest to Highest"));

        sort_time.setItems(FXCollections.observableArrayList("\uD83D\uDD53 Due time",
                "↓   Highest to Lowest",
                "↑   Lowest to Highest"));
    }

    private Text createItem(String text, boolean options) {
        // Returns a horizontal block with the text with icon
        Text label = new Text(text);

        if (options) {
            label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-fill: #000000;");
        } else {
            label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-fill: #666666;");
        }
        return label;
    }



    @FXML
    protected void openCreationScreen() {
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
