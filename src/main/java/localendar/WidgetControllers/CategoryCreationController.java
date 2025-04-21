package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import localendar.Category;
import localendar.Database;

import java.util.HashMap;

public class CategoryCreationController {
    @FXML
    private TextField categoryName;

    @FXML
    private ColorPicker categoryColor, textColor;

    @FXML
    private Button button;

    private CategoriesController caller;

    private Category getResCategory(){
        Color categoryColorVal = categoryColor.getValue();
        String categoryHex = String.format("#%02X%02X%02X",
                (int) (categoryColorVal.getRed() * 255),
                (int) (categoryColorVal.getGreen() * 255),
                (int) (categoryColorVal.getBlue() * 255));
        Color textColorVal = textColor.getValue();
        String textHex = String.format("#%02X%02X%02X",
                (int) (textColorVal.getRed() * 255),
                (int) (textColorVal.getGreen() * 255),
                (int) (textColorVal.getBlue() * 255));

        return new Category(categoryName.getText(), categoryHex, textHex);
    }

    public void setEdit(Category category){
        button.setText("Edit");
        categoryName.setText(category.getName());
        categoryColor.setValue(Color.web(category.getColor()));
        textColor.setValue(Color.web(category.getTextColor()));
        button.setOnMouseClicked(event->{
            Database db= new Database();

            Category resCategory = getResCategory();

            int res = -1;
            while(res == -1){
                res = db.editCategory(category,resCategory);
                if(res == -1){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText("Duplicate data");
                    alert.setContentText("Category already exists");
                    alert.showAndWait();
                }
            }

            db.closeConnection();
            Stage stage = (Stage) categoryName.getScene().getWindow();
            caller.refreshCategories();
            stage.close();
        });
    }

    public void setCaller(CategoriesController parent){
        caller = parent;
    }

    public void setTaskCreate(ComboBox<String> parent, HashMap<Integer,Category> categories){
        parent.getItems().remove("Create new");
        button.setOnMouseClicked(e->{
            Database db = new Database();
            Category resCategory = getResCategory();

            if(writeIntoDatabase(resCategory,db) != -1) {

                categories.put(db.getRecentCategory(), resCategory);
                parent.getItems().add(resCategory.getName());
                parent.getItems().add("Create new");
                Stage stage = (Stage) categoryName.getScene().getWindow();
                stage.close();
            }
            db.closeConnection();
        });
    }



    @FXML
    private void createCategory(){
        Database db = new Database();
        Category resCategory = getResCategory();

        writeIntoDatabase(resCategory,db);

        caller.generateCategoryItem(resCategory);
        db.closeConnection();
        Stage stage = (Stage) categoryName.getScene().getWindow();  // any node in the window works
        stage.close();
    }

    private int writeIntoDatabase(Category resCategory, Database dbInstance){
        int res = dbInstance.writeCategory(resCategory);
        if(res == -1){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Duplicate data");
            alert.setContentText("Category already exists");
            alert.showAndWait();
        }
        return res;

    }
}
