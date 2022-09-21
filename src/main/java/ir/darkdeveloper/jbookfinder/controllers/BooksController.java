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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static ir.darkdeveloper.jbookfinder.JBookFinder.getResource;


public class BooksController implements FXMLController, ThemeObserver {

    @FXML
    private VBox contentVbox;
    @FXML
    private VBox rootVbox;
    @FXML
    private StackPane rootPane;
    @FXML
    private TextField fieldSearch;
    @FXML
    private FlowPane booksContainer;

    private final List<BookModel> booksList = new ArrayList<>();
    private final List<HBox> itemParents = new ArrayList<>();
    private final IOUtils ioUtils = IOUtils.getInstance();
    private final BookUtils bookUtils = BookUtils.getInstance();
    private final Configs configs = Configs.getInstance();

    private Stage stage;

    @Override
    public void initialize() {
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
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
                                var fxmlLoader = new FXMLLoader(getResource("fxml/bookItem.fxml"));
                                HBox root = fxmlLoader.load();
                                itemParents.add(root);
                                BookItemController itemController = fxmlLoader.getController();
                                itemController.setBookModel(book);
                                itemController.setStage(stage);
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
        FxUtils.switchSceneToMain(stage, "main.fxml");
    }

    public void showSettings() {
        var controller = (SettingsController) FxUtils
                .newStageAndReturnController("settings.fxml", "Settings",450, 500);
        if (controller != null) {
            controller.setNotToDeleteBooks(booksList);
            configs.getThemeSubject().addObserver(controller);
        }
    }

    @FXML
    private void searchTheBook(ActionEvent e) {
        var text = fieldSearch.getText();
        if (!text.isBlank())
            bookUtils.createSearchUI(text, rootPane, rootVbox, e);
        updateTheme(configs.getTheme());
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

    @FXML
    private void openLibrary(ActionEvent e) {
        var controller = FxUtils
                .switchSceneAndGetController(e, "library.fxml", "Library", LibraryController.class);
        if (controller != null) {
            controller.setStage(stage);
            controller.resizeListViewByStage();
            configs.getThemeSubject().addObserver(controller);
        }
    }

    @Override
    public void updateTheme(String theme) {
        bookUtils.updateThemeForBooks(theme, booksContainer, contentVbox, itemParents);
        var allButtons = FxUtils.getAllNodes(rootPane, Button.class);
        allButtons.forEach(button -> {
            if (configs.getTheme().equals("dark")) {
                if (!button.getStyleClass().contains("button-light"))
                    button.getStyleClass().add("button-light");
                button.getStyleClass().remove("button-dark");
            } else {
                if (!button.getStyleClass().contains("button-dark"))
                    button.getStyleClass().add("button-dark");
                button.getStyleClass().remove("button-light");
            }
        });
    }
}
