package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.controllers.FXMLController;
import ir.darkdeveloper.jbookfinder.controllers.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SwitchSceneUtil {


    public static void switchSceneToMain(Stage stage, String fxmlFilename, String styleSheetPath) {
        try {
            var loader = new FXMLLoader(getResource("fxml/" + fxmlFilename));
            Parent root = loader.load();
            var scene = new Scene(root);
            MainController controller = loader.getController();
            if (styleSheetPath != null)
                scene.getStylesheets().add(getResource("css/" + styleSheetPath).toExternalForm());
            scene.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.ENTER))
                    controller.searchTheBook(stage);
            });
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static <T extends FXMLController> T getFxmlController(String fxmlFilename, Class<T> tClass) {
        var loader = new FXMLLoader(getResource("fxml/" + fxmlFilename));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        var fxmlController = loader.getController();
        return tClass.cast(fxmlController);
    }


    public static Stage getStageFromEvent(ActionEvent e) {
        if (e.getSource() instanceof Node n
                && n.getScene().getWindow() instanceof Stage stage) {
            return stage;
        }
        return null;
    }

    public static <T extends FXMLController> T switchSceneAndGetController(ActionEvent e, String fxmlFilename, Class<T> tClass) {
        return switchSceneAndGetController(getStageFromEvent(e), fxmlFilename, tClass);
    }

    public static <T extends FXMLController> T switchSceneAndGetController(Stage stage, String fxmlFilename, Class<T> tClass) {
        try {
            var loader = new FXMLLoader(SwitchSceneUtil.getResource("fxml/" + fxmlFilename));
            Parent root = loader.load();
            var scene = new Scene(root);
            stage.setScene(scene);
            return tClass.cast(loader.getController());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static URL getResource(String path) {
        return SwitchSceneUtil.class.getClassLoader().getResource(path);
    }
}
