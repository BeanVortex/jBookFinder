package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.controllers.FXMLController;
import ir.darkdeveloper.jbookfinder.controllers.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FxUtils {


    public static void switchSceneToMain(Stage stage, String fxmlFilename) {
        try {
            var loader = new FXMLLoader(getResource("fxml/" + fxmlFilename));
            Parent root = loader.load();
            var scene = new Scene(root);
            MainController controller = loader.getController();
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
            var loader = new FXMLLoader(FxUtils.getResource("fxml/" + fxmlFilename));
            Parent root = loader.load();
            var scene = new Scene(root);
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
            stage.setMinWidth(minWidth);
            stage.setMinHeight(minHeight);
            stage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL getResource(String path) {
        return FxUtils.class.getClassLoader().getResource(path);
    }

    public static void showNotification(String tooltip, String caption, String text) {
        var tray = SystemTray.getSystemTray();
        var trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(getResource("images/blank.png")), tooltip);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO);
        trayIcon.addActionListener(e -> tray.remove(trayIcon));
        tray.remove(trayIcon);
    }

    public static List<Label> getAllLabels(Parent root) {
        var labels = new ArrayList<Label>();
        addAllDescendents(root, labels);
        return labels;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Label> labels) {
        for (var node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Label label)
                labels.add(label);
            if (node instanceof Parent p)
                addAllDescendents(p, labels);
        }
    }

}

