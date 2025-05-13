package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import localendar.Category;
import localendar.Database;
import localendar.Priority;
import localendar.Task;
import javafx.scene.shape.Rectangle;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class TaskItemController {
    @FXML
    private Text taskTitle,dueDate,dueTime,categoryName,priorityText;

    @FXML
    private Rectangle categoryBox, priorityBox;

    @FXML
    private Button editButton, deleteButton;

    @FXML
    private CheckBox taskStatus;

    private Task task;

    private AnchorPane callerRoot;

    private MainController main;

    public void setTask(Task task){
        this.task=task;
        taskStatus.setSelected(task.isStatus());
        setTaskTitle(task.getTitle());
        setDueDate(task.getDueDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        setDueTime(task.getDueTime().toString());
        setCategoryText(task.getCategory().getName(),task.getCategory().getTextColor());
        setCategoryBox(task.getCategory().getColor());
        setPriority(task.getPriority());
    }

    public void setCategoryText(String name, String color){
        String truncatedText = name;
        Text tempText = new Text(name);

        while (tempText.getLayoutBounds().getWidth() > 100 && truncatedText.length() > 3) {
            // If the temp text element's width is more than 100px, remove one character and test if
            // adding ... to the end is still more than 100
            // Done to ensure that the width is constant for different category name
            truncatedText = truncatedText.substring(0, truncatedText.length() - 1);
            tempText.setText(truncatedText + "...");
        }

        categoryName.setText(truncatedText.length() < name.length() ? truncatedText + "..." : truncatedText);
        categoryName.setFill(Color.web(color));
    }

    public void setCategoryBox(String color){
        categoryBox.setFill(Color.web(color));
    }

    public void setTaskTitle(String title){
        taskTitle.setText(title);
    }

    public void setPriority(Priority priority){
        priorityText.setText(priority.name());
        priorityBox.setFill(Color.web(priority.getColor()));
    }

    public void setDueDate(String date){
        dueDate.setText(date);
    }

    public void setDueTime(String time){
        dueTime.setText(time);
    }

    public void setCallerRoot(AnchorPane root){ callerRoot=root; }

    public void setMain(MainController main){ this.main = main;}

    @FXML
    private void openTask(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OpenTask.fxml"));

            Parent openTask = loader.load();
            OpenTaskController controller=loader.getController();
            controller.setTask(task);
            Stage taskWindow = new Stage();
            taskWindow.setTitle(task.getTitle());
            taskWindow.setScene(new Scene(openTask, 692, 411));
            taskWindow.setResizable(false);

            callerRoot.setDisable(true); // Disbales the main window when category screen is opened

            taskWindow.setOnHidden(event -> {
                callerRoot.setDisable(false);  // Makes it enabled again when category is cllosed
            });

            taskWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void delete(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to delete?");
        alert.setContentText("Click OK to proceed, or Cancel to abort.");
        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                Database db = new Database();
                db.deleteTask(task);
                db.closeConnection();
                // TODO loop through every task in the data structure, from your getter function at main
                // TODO if the task matches remove it
                // TODO then main.refreshTaskList(main.YOUR DATA STRUCUTERE GETTER FUNCTION)
                /* Priority queue example
                    PriorityQueue<Task> mainTasks = main.getTasks();
                    mainTasks.removeIf(task -> task.equals(this.task));
                    main.refreshTaskList(mainTasks);

                 */
                // Remove the task from the ArrayList (task data structure)
                ArrayList<Task> taskList = main.getTasks();  // Get the list of tasks from main
                taskList.removeIf(t -> t.equals(this.task));  // Remove the task matching the current task

                // Refresh the task list in the UI
                main.refreshTaskList(taskList);  // Pass the updated list to refresh the UI
            }
        });
    }

    @FXML
    private void edit(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TaskCreation.fxml"));
            Parent taskRoot = loader.load();
            TaskCreateController controler = loader.getController();
            controler.setMain(main);
            controler.setEdit(task);

            Stage taskWindow = new Stage();
            taskWindow.setTitle("Create task");
            taskWindow.setScene(new Scene(taskRoot, 600, 400));
            taskWindow.setResizable(false);

            callerRoot.setDisable(true); // Disbales the main window when category screen is opened

            taskWindow.setOnHidden(event -> {
                callerRoot.setDisable(false);  // Makes it enabled again when category is cllosed
            });

            taskWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void setStatus(){
        Database db= new Database();
        db.updateCheck(task,taskStatus.isSelected());
        db.closeConnection();
    }

}
