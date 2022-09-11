package ir.darkdeveloper.jbookfinder.controllers;

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


    @FXML
    private void searchTheBook(ActionEvent e) {
        var text = fieldSearch.getText();
        if (!text.isBlank())
            bookUtils.createSearchUI(text, rootPane, rootVbox, e);

    }

    public void searchTheBook(Stage stage) {
        var text = fieldSearch.getText();
        if (!text.isBlank())
            bookUtils.createSearchUI(text, rootPane, rootVbox, stage);

    }


    @FXML
    private void showSettings() {
        var controller = (SettingsController) FxUtils
                .newStageAndReturnController("settings.fxml", 450, 500);
        if (controller != null) {
            var stage = controller.getStage();
            controller.resizeLinesByStage(stage);
        }
    }

    @Override
    public void initialize() {
    }

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public Stage getStage() {
        return null;
    }
}
