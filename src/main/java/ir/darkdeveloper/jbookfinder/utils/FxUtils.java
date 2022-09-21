package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.controllers.FXMLController;
import ir.darkdeveloper.jbookfinder.controllers.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static ir.darkdeveloper.jbookfinder.JBookFinder.getResource;

public class FxUtils {


    public static void switchSceneToMain(Stage stage, String fxmlFilename) {
        try {
            var loader = new FXMLLoader(getResource("fxml/" + fxmlFilename));
            Parent root = loader.load();
            var scene = new Scene(root, stage.getWidth(), stage.getHeight());
            MainController controller = loader.getController();
            controller.setStage(stage);
            scene.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.ENTER))
                    controller.searchTheBook(stage);
            });
            stage.setScene(scene);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
            var loader = new FXMLLoader(getResource("fxml/" + fxmlFilename));
            Parent root = loader.load();
            var scene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
            return tClass.cast(loader.getController());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static FXMLController newStageAndReturnController(String fxmlFilename, double minWidth, double minHeight) {
        try {
            var stage = new Stage();
            var loader = new FXMLLoader(getResource("fxml/" + fxmlFilename));
            Parent root = loader.load();
            FXMLController controller = loader.getController();
            controller.setStage(stage);
            var scene = new Scene(root);
            stage.setScene(scene);
            var logoPath = getResource("images/logo.png");
            if (logoPath != null)
                stage.getIcons().add(new Image(logoPath.toExternalForm()));
            stage.setMinWidth(minWidth);
            stage.setMinHeight(minHeight);
            stage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> getAllNodes(Parent root, Class<T> tClass) {
        var nodes = new ArrayList<T>();
        addAllDescendents(root, nodes, tClass);
        return nodes;
    }

    private static <T> void addAllDescendents(Parent root, ArrayList<T> nodes, Class<T> tClass) {
        for (var node : root.getChildrenUnmodifiable()) {
            if (tClass.isInstance(node))
                nodes.add(tClass.cast(node));
            if (node instanceof Parent p)
                addAllDescendents(p, nodes, tClass);
        }
    }
}

