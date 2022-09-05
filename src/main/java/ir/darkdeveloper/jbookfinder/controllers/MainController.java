package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController implements FXMLController {


    @FXML
    private StackPane rootPane;
    @FXML
    private VBox rootVbox;
    @FXML
    private TextField fieldSearch;

    private static final BookUtils bookUtils = new BookUtils();


    @FXML
    private void searchTheBook(ActionEvent e) {
        var stage = SwitchSceneUtil.getStageFromEvent(e);
        searchTheBook(stage);
    }



    public void searchTheBook(Stage stage) {
        var text = fieldSearch.getText();
        if (!text.isBlank()) {
            var trimmedText = text.replaceAll("\s", "");
            var progress = new ProgressIndicator();
            var vbox = new VBox(progress);
            vbox.setAlignment(Pos.CENTER);
            rootVbox.setDisable(true);
            rootPane.getChildren().add(vbox);
            bookUtils.searchTheBookWithScrapper(stage, trimmedText);
        }
    }


    @Override
    public void initialize() {
    }
}
