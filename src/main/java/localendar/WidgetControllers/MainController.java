package localendar.WidgetControllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import localendar.Category;
import localendar.Database;
import localendar.Task;


import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;




public class MainController implements Initializable {
    private Database db = new Database();
    private HashMap<Integer, Category> categories = db.getCategories();

    // Add ChangeListener for search
    @FXML
    private VBox taskArea;

    @FXML
    private AnchorPane root;

    @FXML
    private ComboBox<String> sort_priority,sort_due,sort_time;

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

        try{
            ResultSet taskQueryResult = db.getTasks();
            while(taskQueryResult.next()){
                /*
                Do ur data structure thing here, only loading the tasks to ur data structure
                 */
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
        Then run generateTaskItem(Task task) for each of your task item in your data structure
         */



    }
    private void generateTaskItem(Task task){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/TaskItems.fxml"));
            Node item = fxmlLoader.load();
            TaskItemController controller = fxmlLoader.getController();
            controller.setTask(task);

            taskArea.getChildren().add(item);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private Text createItem(String text, boolean options) {
        Text label = new Text(text);

        if (options) {
            label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-fill: #000000;");
        } else {
            label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-fill: #666666;");
        }
        return label;
    }

    private void refreshTaskList(){
        taskArea.getChildren().clear();
        // Repopulate task list, from your data structures
    }

    @FXML
    private void openCreationScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TaskCreation.fxml"));

            Parent taskRoot = loader.load();

            Stage taskWindow = new Stage();
            taskWindow.setTitle("Create task");
            taskWindow.setScene(new Scene(taskRoot, 600, 400));
            taskWindow.setResizable(false);

            root.setDisable(true); // Disbales the main window when category screen is opened

            taskWindow.setOnHidden(event -> {
                root.setDisable(false);  // Makes it enabled again when category is cllosed
            });

            taskWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCategoriesScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategoryWindow.fxml"));

            Parent categoriesRoot = loader.load();

            Stage categoriesWindow = new Stage();
            categoriesWindow.setTitle("Categories");
            categoriesWindow.setScene(new Scene(categoriesRoot, 640, 480));
            categoriesWindow.setResizable(false);

            root.setDisable(true); // Disbales the main window when category screen is opened

            categoriesWindow.setOnHidden(event -> {
                root.setDisable(false);  // Makes it enabled again when category is cllosed
            });

            categoriesWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openTaskCreate(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TaskCreation.fxml"));

            Parent taskCreateRoot = loader.load();

            Stage taskCreateWindow= new Stage();
            taskCreateWindow.setTitle("Create task");
            taskCreateWindow.setScene(new Scene(taskCreateRoot, 600, 400));
            taskCreateWindow.setResizable(false);

            root.setDisable(true); // Disbales the main window when category screen is opened

            taskCreateWindow.setOnHidden(event -> {
                root.setDisable(false);  // Makes it enabled again when category is cllosed
            });

            taskCreateWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void test() {
//        ArrayList<String> testDatabase = Database.test();
//        testbox.getChildren().clear();
//        for (String entry : testDatabase) {
//            Label label = new Label(entry);
//            testbox.getChildren().add(label); // Ignore error this works
//        }
    }
}
