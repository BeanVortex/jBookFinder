package ir.darkdeveloper.jbookfinder;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;


//@SpringBootApplication
@Slf4j
//@RestController
public class JBookFinderApplication extends Application {


    public static void main(String[] args) {
//        var app = SpringApplication.run(JBookFinderApplication.class, args);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getResource("MainController.fxml"));
        var scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(getResource("css/main.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Main Page");
        stage.show();
    }

    private URL getResource(String path) {
        return getClass().getClassLoader().getResource(path);
    }

}
