package ir.darkdeveloper.jbookfinder.utils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SwitchSceneUtil {

    public static void switchScene(ActionEvent e, String fxmlFilename, String styleSheetPath) {
        try {
            var stage = getStageFromEvent(e);
            var root = (Parent) FXMLLoader.load(getResource(fxmlFilename));
            var scene = new Scene(root);
            scene.getStylesheets().add(getResource(styleSheetPath).toExternalForm());
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void switchScene(Stage stage, String fxmlFilename, String styleSheetPath) {
        try {
            var root = (Parent) FXMLLoader.load(getResource(fxmlFilename));
            var scene = new Scene(root);
            scene.getStylesheets().add(getResource(styleSheetPath).toExternalForm());
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Stage getStageFromEvent(ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }


    public static URL getResource(String path) {
        return SwitchSceneUtil.class.getClassLoader().getResource(path);
    }
}
