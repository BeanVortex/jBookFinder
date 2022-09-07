package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.List;

public class SettingsController implements FXMLController {

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
        circleTheme.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (circleTheme.getFill().equals(Paint.valueOf("#fff")))
                circleTheme.setFill(Paint.valueOf("#333"));
            else
                circleTheme.setFill(Paint.valueOf("#fff"));
            ioUtils.saveConfigs(null);
            // Todo: apply theme
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

    // Todo: move contents from previous location to the new one
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
