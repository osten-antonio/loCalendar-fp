package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class RecurrenceEditorController implements Initializable {

    @FXML
    private ComboBox<String> freqBox;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Spinner<Integer> intervalSpin;

    @FXML
    private Button setButton;

    private TaskCreateController caller;

    public void setValues(String freq, LocalDate endDate, Integer interval){
        if(interval != null){
            intervalSpin.getValueFactory().setValue(interval);
        }
        if(!freq.isBlank()){
            freqBox.setValue(freq.charAt(0) + freq.substring(1).toLowerCase());
        }
        endDatePicker.setValue(endDate);

    }

    public void setCaller(TaskCreateController controller){
        caller=controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        freqBox.getItems().addAll("Daily", "Monthly", "Weekly", "Yearly");
        intervalSpin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                365,
                1
        ));
        endDatePicker.setEditable(false);
    }

    private boolean changed = false;

    public void setForFilter(ArrayList<String> rRuleFilter){
        setButton.setOnMouseClicked(e->{
            if(freqBox.getValue() != null && !freqBox.getValue().isBlank()){
                rRuleFilter.add(String.format("FREQ=%s;INTERVAL=%d;UNTIL=%s",
                        freqBox.getValue().toUpperCase(),intervalSpin.getValue(),
                        endDatePicker.getValue() == null?"":endDatePicker.getValue().toString()));
                changed=true;
                ((Stage) setButton.getScene().getWindow()).close();
            }else{
                Alert alert = new Alert(AlertType.WARNING, "Please select a frequency.", ButtonType.OK);
                alert.setTitle("Input Error");
                alert.setHeaderText("Missing Information");
                alert.showAndWait();
            }

        });
    }
    public String getFilter(){
        return String.format("%s | %d | %s",freqBox.getValue().toUpperCase(),intervalSpin.getValue(),
                endDatePicker.getValue() == null?"":endDatePicker.getValue().toString());
    }

    public boolean isChanged() {
        return changed;
    }

    @FXML
    private void setRecurrence(){
        if(freqBox.getValue() == null || freqBox.getValue().isBlank()){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid data");
            alert.setContentText("Frequency is required");
            alert.showAndWait();
            return;
        }
        caller.setFreq(freqBox.getValue());
        caller.setEndDate(endDatePicker.getValue());
        caller.setInterval(intervalSpin.getValue());

        Stage stage = (Stage) freqBox.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void clear(){
        freqBox.setValue("");
        intervalSpin.getValueFactory().setValue(1);
        endDatePicker.setValue(null);
    }
}
