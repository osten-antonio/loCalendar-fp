package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import localendar.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TaskCreateController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private TextArea taskBody;

    @FXML
    private TextField taskTitle;

    @FXML
    private Spinner<Integer> dueHour, dueMinute;

    @FXML
    private ComboBox<String> categorySelector,prioritySelector;

    @FXML
    private DatePicker dueDate;

    @FXML
    private Button createButton;


    // Recurrence rule
    private LocalDate startDate;
    private Optional<LocalDate> endDate;
    private Frequency freq;
    private Integer interval;


    // Categories
    private HashMap<Integer, Category> categories;

    private MainController main;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        endDate = Optional.empty();
        interval = null;
        freq=null;
        // Set text formatter for the task body, to limit to 5000 character
        // https://stackoverflow.com/questions/36612545/javafx-textarea-limit
        taskBody.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 5000 ? change : null));

        dueHour.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0, // Min
                        23, // Max
                        0 // Default
                )
        );

        dueMinute.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0, // Min
                        59, // Max
                        0 // Default
                )
        );

        categorySelector.setOnAction(event ->{
            if(categorySelector.getValue().equals("Create new")){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategoryCreation.fxml"));

                    Parent categoryRoot = loader.load();
                    CategoryCreationController controller=loader.getController();

                    controller.setTaskCreate(categorySelector,categories);

                    Stage categoryWindow = new Stage();
                    categoryWindow.setTitle("Create category");
                    categoryWindow.setScene(new Scene(categoryRoot, 600, 214));
                    categoryWindow.setResizable(false);


                    root.setDisable(true); // Disbales the main window when category screen is opened

                    categoryWindow.setOnHidden(e -> {
                        root.setDisable(false);  // Makes it enabled again when category is cllosed
                    });

                    categoryWindow.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Database db = new Database();
        categories = db.getCategories();
        db.closeConnection();

        categorySelector.getItems().addAll(
                categories.values().stream()
                        .map(cat -> cat.getName())
                        .toList()
        );

        prioritySelector.getItems().addAll("Low","Medium","High");
        categorySelector.getItems().add("Create new");
        dueDate.setEditable(false);
    }
    public void setFreq(String frequency){
        freq=Frequency.valueOf(frequency.toUpperCase());
    }

    public void setEndDate(LocalDate dateEnd){
        Optional.ofNullable(dateEnd);
    }

    public void setInterval(int interval){
        this.interval= interval;
    }

    public void setMain(MainController main){ this.main = main;}

    @FXML
    private void createTask(){
        boolean valid = !taskTitle.getText().isBlank() && dueDate.getValue()!=null &&
                prioritySelector.getValue() != null;
        if(valid){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String selected = prioritySelector.getValue().toUpperCase();
            Priority selectedPriority = Priority.valueOf(selected);
            int level = selectedPriority.getLevel();
            String sqlFreq = freq == null ? "" : freq.toString();
            String sqlInterval = interval == null ? "" : interval.toString();
            String sqlEndDate = endDate.map(localDate -> localDate.format(formatter)).orElse("");
            Task resTask = new Task(taskTitle.getText(),taskBody.getText(),false,dueDate.getValue(),
                    LocalTime.of(dueHour.getValue(),dueMinute.getValue()), level,
                    String.format("FREQ=%s;INTERVAL=%s;UNTIL=%s",sqlFreq,sqlInterval,sqlEndDate),
                    categories.get(Collections.max(categories.keySet()))
            );
            Database db = new Database();
            db.writeTask(resTask);
            db.closeConnection();

            main.getTasks().add(resTask);
            main.generateTaskItem(resTask);

            main.refreshCache();
            Stage stage = (Stage) taskTitle.getScene().getWindow();
            stage.close();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid data");
            alert.setContentText("Invalid field exists");
            alert.showAndWait();
        }
    }

    public void setEdit(Task task){
        Task prevTask = task.copy();
        createButton.setText("Edit");

        freq=task.getRrule().getFrequency();
        endDate=task.getRrule().getEndDate();
        interval = task.getRrule().getInterval();

        taskBody.setText(task.getBody());
        taskTitle.setText(task.getTitle());

        dueHour.getValueFactory().setValue(task.getDueTime().getHour());
        dueMinute.getValueFactory().setValue(task.getDueTime().getMinute());
        dueDate.setValue(task.getDueDate());
        categorySelector.setValue(task.getCategory().getName());
        prioritySelector.setValue(task.getPriority().toString());

        createButton.setOnMouseClicked(e-> {
            boolean valid = !taskTitle.getText().isBlank() && dueDate.getValue() != null &&
                    !prioritySelector.getValue().isBlank();
            if (valid) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String selected = prioritySelector.getValue().toUpperCase();
                Priority selectedPriority = Priority.valueOf(selected);
                int level = selectedPriority.getLevel();
                String sqlFreq = freq == null ? "" : freq.toString();
                String sqlInterval = interval == null ? "" : interval.toString();
                String sqlEndDate = endDate.map(
                        localDate -> localDate.format(formatter)).orElse("");
                Database db = new Database();
                Task resTask = new Task(taskTitle.getText(), taskBody.getText(), false, dueDate.getValue(),
                        LocalTime.of(dueHour.getValue(), dueMinute.getValue()), level,
                        String.format("FREQ=%s;INTERVAL=%s;UNTIL=%s", sqlFreq, sqlInterval, sqlEndDate),
                        categories.get(Collections.max(categories.keySet()))
                );
                PriorityQueue<Task> mainTaskQueue = main.getTasks();

                for(Task mainTask:main.getTasks()){
                    if (mainTask.equals(prevTask)){
                        mainTaskQueue.remove(mainTask);
                        mainTaskQueue.add(resTask);
                        break;
                    }
                }


                main.refreshCache();
                db.updateTask(task, resTask);
                db.closeConnection();
                main.refreshTaskList(mainTaskQueue);

                Stage stage = (Stage) taskTitle.getScene().getWindow();
                stage.close();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid data");
                alert.setContentText("Invalid field exists");
                alert.showAndWait();
            }

        });



    }

    @FXML
    private void viewRecurrenceRule(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RecurrenceEditor.fxml"));

            Parent rRuleEditorRoot = loader.load();
            RecurrenceEditorController controller=loader.getController();

            controller.setCaller(this);
            String freqString;
            if(freq == null) freqString = "";
            else freqString = freq.toString();

            controller.setValues(freqString,endDate.orElse(null),interval);

            Stage recurrenceWindow = new Stage();
            recurrenceWindow.setTitle("Recurrence rule");
            recurrenceWindow.setScene(new Scene(rRuleEditorRoot, 341, 160));
            recurrenceWindow.setResizable(false);


            root.setDisable(true); // Disbales the main window when category screen is opened

            recurrenceWindow.setOnHidden(event -> {
                root.setDisable(false);  // Makes it enabled again when category is cllosed
            });

            recurrenceWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
