package localendar.WidgetControllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import localendar.Category;
import localendar.Database;

import java.io.IOException;
import java.util.HashMap;

public class CategoriesController {
    @FXML
    private VBox categoryArea;

    @FXML
    private AnchorPane root;

    private HashMap<Integer, Category> categories;


    private MainController main;

    Database db;

    public void setMain(MainController main){
        this.main = main;
        categories=main.getCategories();
        generateAllCategoryItems();
    }

    private void generateAllCategoryItems() {
        categoryArea.getChildren().clear(); // clear existing, if any
        categories.keySet().forEach(key -> generateCategoryItem(categories.get(key)));
    }

    public void generateCategoryItem(Category category){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/CategoryItem.fxml"));
            Node item = fxmlLoader.load();
            CategoriesItemController controller = fxmlLoader.getController();
            controller.setMain(main);
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
            controller.setMain(main);

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
