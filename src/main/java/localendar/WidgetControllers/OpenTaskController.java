package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import localendar.Task;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class OpenTaskController implements Initializable {
    @FXML
    private Text categoryText, dueDate, dueTime, rRuleFrequency, rRuleInterval, rRuleEndDate, priorityText;
    @FXML
    private TextField taskTitle;
    @FXML
    private TextArea taskBody;
    @FXML
    private Rectangle categoryRect, priorityRec;
    private MainController main;

    private Task task;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        taskTitle.setDisable(true);
        taskBody.setDisable(true);
    }
    public void setTask(Task task){
        this.task = task;
        categoryText.setText(task.getCategory().getName());
        categoryRect.setFill(Color.web(task.getCategory().getColor()));
        priorityText.setText(task.getPriority().toString());
        priorityRec.setFill(Color.web(task.getPriority().getColor()));

        taskTitle.setText(task.getTitle());
        taskBody.setText(task.getBody());

        dueDate.setText(task.getDueDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        dueTime.setText(task.getDueTime().toString());

        rRuleFrequency.setText(task.getRrule().getFrequency().name());
        rRuleInterval.setText(String.valueOf(task.getRrule().getInterval()));
        rRuleEndDate.setText(task.getRrule().getEndDate().toString());
    }
    public void setMain(MainController main){
        this.main = main;
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

            taskWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}