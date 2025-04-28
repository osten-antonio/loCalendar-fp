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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class MainController implements Initializable {
    private Database db = new Database();
    private HashMap<Integer, Category> categories = db.getCategories();

    // Add ChangeListener for search
    @FXML
    private VBox taskArea,

    //  VBox for calendar
    b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,b21,b22,b23,b24,b25,b26,b27,
            b28,b29,b30,b31,b32,b33,b34,b35;

    @FXML
    private AnchorPane root;

    @FXML
    private ComboBox<String> sort_priority,sort_due,sort_time;

    // Month day labels
    @FXML
    private Text monthLabel,
            l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12,l13,l14,l15,l16,l17,l18,l19,l20,l21,l22,l23
            ,l24,l25,l26,l27,l28,l29,l30,l31,l32,l33,l34,l35;

    private LocalDate curDate;

    private List<Text> monthDayLabels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        curDate = LocalDate.now();

        monthDayLabels = new ArrayList<>(Arrays.asList(
                l1, l2, l3, l4, l5, l6, l7, l8, l9, l10,
                l11, l12, l13, l14, l15, l16, l17, l18, l19, l20,
                l21, l22, l23, l24, l25, l26, l27, l28, l29, l30,
                l31, l32, l33, l34, l35
        ));
        monthLabel.setText(curDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy")));

        sort_priority.setItems( FXCollections.observableArrayList("↕    Priority",
                "↓    Highest to Lowest",
                "↑    Lowest to Highest"));

        sort_due.setItems( FXCollections.observableArrayList("\uD83D\uDCC6   Due date",
                "↓     Highest to Lowest",
                "↑     Lowest to Highest"));

        sort_time.setItems( FXCollections.observableArrayList("\uD83D\uDD53 Due time",
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

        populateCalendar();

    }

    private void populateCalendar(){
        YearMonth currentMonth = YearMonth.from(curDate); //  Gets the year and month
        LocalDate firstOfMonth = currentMonth.atDay(1); // Gets the first day of month
        int startDay = firstOfMonth.getDayOfWeek().getValue();

        // Fill leading labels
        YearMonth tempMonth = YearMonth.from(currentMonth.minusMonths(1));
        System.out.println(tempMonth.lengthOfMonth());
        int j = tempMonth.lengthOfMonth();
        for(int i = startDay-2; i>=0;i--){
            monthDayLabels.get(i).setText(String.valueOf(j--));
        }
        j = 1;
        // To prevent stackoverflow, makes sure that its less than 35
        int LengthMonth = Math.min((currentMonth.lengthOfMonth() + startDay - 1), 35);

        for(int i = startDay-1;i<LengthMonth;i++){
            monthDayLabels.get(i).setText(String.valueOf(j++));
        }

        // Fill in trailing label
        j = 1;
        for (int i = startDay - 1 + currentMonth.lengthOfMonth() ; i < 35; i++) {
            monthDayLabels.get(i).setText(String.valueOf(j++));
        }

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
    private void left(){
        curDate = curDate.minusMonths(1);
        monthLabel.setText(curDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy")));
        populateCalendar();
    }
    @FXML
    private void right(){
        curDate = curDate.plusMonths(1);
        monthLabel.setText(curDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy")));
        populateCalendar();
    }

    @FXML
    private void today(){
        curDate = LocalDate.now();
        monthLabel.setText(curDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy")));
        populateCalendar();
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
