package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import localendar.Priority;
import localendar.Task;
import javafx.scene.shape.Rectangle;

import java.time.format.DateTimeFormatter;

public class TaskItemController {
    @FXML
    private Text taskTitle,dueDate,dueTime,categoryName,priorityText;

    @FXML
    private Rectangle categoryBox, priorityBox;

    @FXML
    private Button editButton, deleteButton;

    public void setTask(Task task){
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


}
