package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.service.ScraperService;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
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
        var text = fieldSearch.getText();
        if (!text.isBlank()) {
            var trimmedText = StringUtils.trimWhitespace(text);
            var stage = SwitchSceneUtil.getStageFromEvent(e);

            var progress = new ProgressIndicator();
            var vbox = new VBox(progress);
            vbox.setAlignment(Pos.CENTER);
            rootVbox.setDisable(true);
            rootPane.getChildren().add(vbox);

            scraperService.fetchBookModels(trimmedText, 1)
                    .whenComplete((bookModels, throwable) -> Platform.runLater(() -> {
                        var booksController = SwitchSceneUtil.
                                switchSceneAndGetController(e, "BooksController.fxml", BooksController.class);
                        if (booksController == null) {
                            log.error("Books controller is null");
                            return;
                        }
                        booksController.showSearch(bookModels);
                        booksController.resizeListViewByStage(stage);
                    }));
        }
    }


    @Override
    public void initialize() {
    }
}
