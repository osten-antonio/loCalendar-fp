package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import localendar.Category;
import localendar.Database;

import java.util.HashMap;
import java.util.Map;

public class CategoriesItemController {
    @FXML
    private Text categoryColorLabel,categoryName;

    @FXML
    private Rectangle categoryColorBox;

    @FXML
    private AnchorPane root;

    private CategoriesController callerController;

    private HashMap<Integer, Category> categoryHashMap;

    private Category category;

    private MainController main;


    public void setCategory(Category category){
        this.category=category;
        setCategoryName(category.getName());
        setCategoryColorBox(category.getColor());
        setCategoryColorLabel(category.getTextColor());
    }
    public void setCategoryName(String name){
        categoryName.setText(name);
    }


    public void setCategoryColorLabel(String color){
        categoryColorLabel.setText(color);
        categoryColorLabel.setFill(Color.web(color));
    }

    public void setCategoryColorBox(String color){
        categoryColorBox.setFill(Color.web(color));
    }

    public void setCallerController(CategoriesController caller){
        callerController = caller;
    }

    public void setMain(MainController main){
        this.main = main;
        categoryHashMap = main.getCategories();
    }



    @FXML
    private void edit(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategoryCreation.fxml"));

            Parent categoriesRoot = loader.load();
            CategoryCreationController controller = loader.getController();
            controller.setEdit(category);
            controller.setCaller(callerController);
            Stage categoriesWindow = new Stage();
            categoriesWindow.setTitle("Categories");
            categoriesWindow.setScene(new Scene(categoriesRoot, 600, 214));
            categoriesWindow.setResizable(false);

            root.getParent().setDisable(true); // Disbales the main window when category screen is opened

            categoriesWindow.setOnHidden(event -> {
                root.getParent().setDisable(false);  // Makes it enabled again when category is cllosed
            });

            categoriesWindow.show();
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
                db.deleteCategory(category);
                db.closeConnection();
                callerController.refreshCategories();
            }
        });
        for (Map.Entry<Integer, Category> entry : categoryHashMap.entrySet()) {
            if (entry.getValue().equals(category)) {
                categoryHashMap.remove(entry.getKey());
            }
        }
        // TODO loop through every task in the data structure, if the category of that task matches this.category
        // TODO change it to categoryHashMap.get(1)
        // TODO then pass to main.refreshTaskList(main.YOUR DATA STRUCUTERE GETTER FUNCTION)
    }

}
