package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

import static ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil.getResource;


public class JBookFinderApplication extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        var loader = new FXMLLoader(getResource("fxml/MainController.fxml"));
        Parent root = loader.load();
        var scene = new Scene(root);
        MainController controller = loader.getController();
        scene.getStylesheets().add(getResource("css/main.css").toExternalForm());
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
                controller.searchTheBook(stage);
        });
        stage.setScene(scene);
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
    }


    @Override
    public void stop() {
        System.out.println("stopped");
    }

}
