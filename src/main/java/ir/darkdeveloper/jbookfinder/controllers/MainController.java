package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.JBookFinder;
import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class MainController implements FXMLController {


    @FXML
    private VBox rootVbox;
    @FXML
    private StackPane rootPane;
    @FXML
    private TextField fieldSearch;

    private final BookUtils bookUtils = BookUtils.getInstance();
    private Stage stage;

    @FXML
    private void searchTheBook(ActionEvent e) {
        searchTheBook(FxUtils.getStageFromEvent(e));
    }

    public void searchTheBook(Stage stage) {
        var text = fieldSearch.getText();
        if (!text.isBlank())
            bookUtils.createSearchUIAndSearch(text, rootPane, rootVbox, stage);

    }


    @FXML
    private void showSettings() {
        var controller = (SettingsController) FxUtils
                .newStageAndReturnController("settings.fxml", "Settings", 450, 500);
        if (controller != null)
            Configs.getThemeSubject().addObserver(controller);

    }

    @Override
    public void initialize() {}

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @FXML
    private void openLibrary(ActionEvent e) {
        var controller = FxUtils.switchSceneAndGetController(e, "library.fxml", "Library", LibraryController.class);
        if (controller != null) {
            controller.setStage(stage);
            controller.resizeListViewByStage();
            Configs.getThemeSubject().addObserver(controller);
        }
    }

    @FXML
    private void showAbout() {
        JBookFinder.showAbout();
    }
}
