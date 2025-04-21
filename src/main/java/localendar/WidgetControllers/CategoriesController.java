package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import localendar.Category;
import localendar.Database;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class CategoriesController implements Initializable {
    @FXML
    private VBox categoryArea;

    @FXML
    private AnchorPane root;

    private HashMap<Integer, Category> categories;

    Database db;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new Database();
        categories=db.getCategories(true);
        categories.keySet().forEach(key ->
            generateCategoryItem(categories.get(key))
        );

    }

    public void generateCategoryItem(Category category){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/CategoryItem.fxml"));
            Node item = fxmlLoader.load();
            CategoriesItemController controller = fxmlLoader.getController();
            controller.setCallerController(this);
            controller.setCategory(category);

            categoryArea.getChildren().add(item);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void openCreationScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategoryCreation.fxml"));

            Parent categoriesRoot = loader.load();
            CategoryCreationController controller = loader.getController();
            controller.setCaller(this);

            Stage categoriesWindow = new Stage();
            categoriesWindow.setTitle("Categories");
            categoriesWindow.setScene(new Scene(categoriesRoot, 600, 214));
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

    public void refreshCategories(){
        categoryArea.getChildren().clear();
        categories=db.getCategories(true);
        categories.keySet().forEach(key -> {
            generateCategoryItem(categories.get(key));
        });
    }
}
