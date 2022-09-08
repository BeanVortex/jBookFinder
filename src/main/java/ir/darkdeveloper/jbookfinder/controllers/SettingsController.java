package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsController implements FXMLController {

    @FXML
    private VBox parent;
    @FXML
    private Circle circleTheme;
    @FXML
    private Label labelImageCache;
    @FXML
    private Label labelLocation;

    private final Configs configs = Configs.getInstance();
    private final IOUtils ioUtils = IOUtils.getInstance();

    private List<BookModel> notToDeleteBooks;

    @Override
    public void initialize() {
        labelImageCache.setText(String.valueOf(ioUtils.getFolderSize(new File(configs.getBookCoverLocation()))));
        labelLocation.setText(configs.getSaveLocation());
        var labels = FxUtils.getAllLabels(parent);

        if (configs.getTheme().equals("light")) {
            circleTheme.setFill(Paint.valueOf("#333"));
            parent.setBackground(Background.fill(Paint.valueOf("#fff")));
        } else {
            circleTheme.setFill(Paint.valueOf("#fff"));
            parent.setBackground(Background.fill(Paint.valueOf("#333")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
        }

        circleTheme.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (configs.getTheme().equals("light")) {
                circleTheme.setFill(Paint.valueOf("#fff"));
                circleTheme.setStroke(Paint.valueOf("#333"));
                parent.setBackground(Background.fill(Paint.valueOf("#333")));
                labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
                configs.setTheme("dark");
            } else {
                circleTheme.setFill(Paint.valueOf("#333"));
                circleTheme.setStroke(Paint.valueOf("#fff"));
                parent.setBackground(Background.fill(Paint.valueOf("#fff")));
                labels.forEach(label -> label.setTextFill(Paint.valueOf("#111")));
                configs.setTheme("light");
            }
            ioUtils.saveConfigs(null);
        });

    }

    // should be called in books controller
    public void setNotToDeleteBooks(List<BookModel> notToDeleteBooks) {
        this.notToDeleteBooks = notToDeleteBooks;
    }

    @FXML
    private void clearCache() {
        ioUtils.deleteCachedImages(notToDeleteBooks);
        labelImageCache.setText(String.valueOf(ioUtils.getFolderSize(new File(configs.getBookCoverLocation()))));
    }

    @FXML
    private void changeSaveDir(ActionEvent e) {
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select books save location");
        dirChooser.setInitialDirectory(new File(configs.getSaveLocation()));
        var selectedDir = dirChooser.showDialog(FxUtils.getStageFromEvent(e));
        if (selectedDir != null) {
            var files = selectedDir.listFiles();
            if (files != null && files.length == 0) {
                ioUtils.createSaveLocation();
                ioUtils.saveConfigs(selectedDir.getPath());
                labelLocation.setText(configs.getSaveLocation());
            } else
                FxUtils.showNotification("notif", "Directory is not empty", "Directory must be empty");
        }
    }


}
