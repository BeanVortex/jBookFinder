package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;


public class BooksController implements FXMLController, ThemeObserver {

    @FXML
    private VBox contentVbox;
    @FXML
    private VBox rootVbox;
    @FXML
    private StackPane rootPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TextField fieldSearch;
    @FXML
    private FlowPane booksContainer;

    private final List<BookModel> booksList = new ArrayList<>();
    private final List<HBox> itemParents = new ArrayList<>();
    private final IOUtils ioUtils = IOUtils.getInstance();
    private final BookUtils bookUtils = BookUtils.getInstance();
    private final Configs configs = Configs.getInstance();

    @Override
    public void initialize() {
    }

    public void showSearch(Flux<BookModel> books, String text) {
        fieldSearch.setText(text);
        booksContainer.requestFocus();
        updateTheme(configs.getTheme());

        var task = new Task<HBox>() {
            @Override
            protected HBox call() {
                books.subscribe(book -> {
                            try {
                                var fxmlLoader = new FXMLLoader(FxUtils.getResource("fxml/bookItem.fxml"));
                                HBox root = fxmlLoader.load();
                                itemParents.add(root);
                                BookItemController itemController = fxmlLoader.getController();
                                itemController.setBookModel(book);
                                booksList.add(book);
                                updateValue(root);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        Throwable::printStackTrace);
                return null;
            }
        };

        task.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                booksContainer.getChildren().add(newValue);
                updateTheme(configs.getTheme());
            }
        });

        var th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public void resizeListViewByStage(Stage stage) {
        booksContainer.setPrefHeight(stage.getHeight());
        booksContainer.setPrefWidth(stage.getWidth());
        stage.widthProperty().addListener((obs, old, newVal) -> booksContainer.setPrefWidth((Double) newVal));
        stage.heightProperty().addListener((obs, old, newVal) -> booksContainer.setPrefHeight((Double) newVal));
    }

    @FXML
    private void getBack() {
        var stage = (Stage) menuBar.getScene().getWindow();
        FxUtils.switchSceneToMain(stage, "main.fxml");
    }

    public void showSettings() {
        var controller = FxUtils.newStageAndReturnController("settings.fxml",
                450, 500, SettingsController.class);
        if (controller != null)
            controller.setNotToDeleteBooks(booksList);
    }

    @FXML
    private void searchTheBook(ActionEvent e) {
        var text = fieldSearch.getText();
        if (!text.isBlank())
            bookUtils.createSearchUI(text, rootPane, rootVbox, e);
    }


    @FXML
    private void clearImageCache() {
        ioUtils.deleteCachedImages(booksList);
    }

    @FXML
    private void newSearch() {
        fieldSearch.setText("");
        fieldSearch.requestFocus();
    }

    @Override
    public void updateTheme(String theme) {
        var labels = FxUtils.getAllLabels(contentVbox);

        if (theme.equals("light")) {
            contentVbox.setBackground(Background.fill(Paint.valueOf("#fff")));
            booksContainer.setBackground(Background.fill(Paint.valueOf("#fff")));
            itemParents.forEach(parent -> parent.setBackground(Background.fill(Paint.valueOf("#fff"))));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#333")));
        } else {
            contentVbox.setBackground(Background.fill(Paint.valueOf("#333")));
            booksContainer.setBackground(Background.fill(Paint.valueOf("#333")));
            itemParents.forEach(parent -> parent.setBackground(Background.fill(Paint.valueOf("#333"))));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
        }

    }
}
