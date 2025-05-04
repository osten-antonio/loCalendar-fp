package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import localendar.Task;

import java.time.format.DateTimeFormatter;

public class CalendarTaskItemController {
    @FXML
    private AnchorPane root;
    @FXML
    private Text time, name, categoryName;
    @FXML
    private Rectangle categorySquare;
    private Task task;
    AnchorPane callerRoot;
    public void setTask(Task task){
        this.task = task;
        if(task.getTitle().length() > 7) name.setText(task.getTitle().substring(0,5)+"...");
        else name.setText(task.getTitle());
        categorySquare.setFill(Color.web(task.getCategory().getColor()));
        time.setText(task.getDueTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        categoryName.setText(task.getCategory().getName());
    }
    public void setCaller(AnchorPane caller){
        this.callerRoot = caller;

    }

    @FXML
    private void openTask(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OpenTask.fxml"));

            Parent openTask = loader.load();

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

}
