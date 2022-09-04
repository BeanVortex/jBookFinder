package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.service.ScraperService;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.application.Platform;
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


    private final ScraperService scraperService = new ScraperService();


    @FXML
    private void searchTheBook(ActionEvent e) {
        var stage = SwitchSceneUtil.getStageFromEvent(e);
        searchTheBook(stage);
    }

    public void searchTheBook(Stage stage) {
        var text = fieldSearch.getText();
        if (!text.isBlank()) {
            var trimmedText =text.replaceAll("\s", "");

            var progress = new ProgressIndicator();
            var vbox = new VBox(progress);
            vbox.setAlignment(Pos.CENTER);
            rootVbox.setDisable(true);
            rootPane.getChildren().add(vbox);

            scraperService.fetchBookModels(trimmedText, 1)
                    .whenComplete((bookModels, throwable) -> Platform.runLater(() -> {
                        var booksController = SwitchSceneUtil.
                                switchSceneAndGetController(stage, "BooksController.fxml", BooksController.class);
                        if (booksController == null) {
                            System.out.println("Books controller is null");
                            return;
                        }
                        if (bookModels != null && !bookModels.isEmpty()){
                            booksController.showSearch(bookModels);
                            booksController.resizeListViewByStage(stage);
                        }
                    }));
        }
    }


    @Override
    public void initialize() {
    }
}
