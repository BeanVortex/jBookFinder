package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

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
        circleTheme.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.out.println("clicked"));
    }

    public void setNotToDeleteBooks(List<BookModel> notToDeleteBooks) {
        this.notToDeleteBooks = notToDeleteBooks;
    }

    @FXML
    private void clearCache(ActionEvent e) {
        ioUtils.deleteCachedImages(notToDeleteBooks);
    }

    @FXML
    private void changeSaveDir(ActionEvent e) {

    }
}
