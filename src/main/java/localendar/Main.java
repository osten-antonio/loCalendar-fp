package localendar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Font.loadFont(getClass().getResourceAsStream("/fonts/fontawesome-webfont.ttf"), 16);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/mainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
//        // Temporary
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {}
        }));

        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {}
        }));

        Logger.getLogger("javafx.fxml").setLevel(Level.SEVERE);
        launch();
    }
}