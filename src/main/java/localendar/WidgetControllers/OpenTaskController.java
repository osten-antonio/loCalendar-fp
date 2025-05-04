package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import localendar.Task;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class OpenTaskController implements Initializable {
    @FXML
    private Text categoryText, dueDate, dueTime, rRuleFrequency, rRuleInterval, rRuleEndDate;
    @FXML
    private TextField taskTitle;
    @FXML
    private TextArea taskBody;
    @FXML
    private Rectangle categoryRect;

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

        taskTitle.setText(task.getTitle());
        taskBody.setText(task.getBody());

        dueDate.setText(task.getDueDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        dueTime.setText(task.getDueTime().toString());

        rRuleFrequency.setText(task.getRrule().getFrequency().name());
        rRuleInterval.setText(String.valueOf(task.getRrule().getInterval()));
        rRuleEndDate.setText(task.getRrule().getEndDate().toString());
    }

    @FXML
    private void edit(){
        // TODO: after modifyingg the task create controller ot make edit func
    }
}
