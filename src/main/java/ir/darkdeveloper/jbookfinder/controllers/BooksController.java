package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.AppUtils;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
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
    private Hyperlink prevPage;
    @FXML
    private Hyperlink nextPage;
    @FXML
    private ScrollPane scroll;
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
    private final BookUtils bookUtils = BookUtils.getInstance();

    private Stage stage;
    private int pageNumber;
    private String searchText;

    @Override
    public void initialize() {
        final double SPEED = 0.003;
        scroll.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * SPEED;
            scroll.setVvalue(scroll.getVvalue() - deltaY);
        });
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
        searchText = text;
        fieldSearch.setText(text);
        booksContainer.requestFocus();
        updateTheme(Configs.getTheme());

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
                updateTheme(Configs.getTheme());
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
                .newStageAndReturnController("settings.fxml", "Settings", 450, 500);
        if (controller != null) {
            controller.setNotToDeleteBooks(booksList);
            Configs.getThemeSubject().addObserver(controller);
        }
    }

    @FXML
    private void searchTheBook() {
        var text = fieldSearch.getText();
        if (!text.isBlank())
            bookUtils.createSearchUIAndSearch(text, rootPane, rootVbox, stage, 1);
        updateTheme(Configs.getTheme());
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
            Configs.getThemeSubject().addObserver(controller);
        }
    }

    @Override
    public void updateTheme(String theme) {
        FxUtils.updateThemeForBooks(theme, booksContainer, contentVbox, itemParents);
        var allButtons = FxUtils.getAllNodes(rootPane, Button.class);
        FxUtils.updateButtonTheme(allButtons);
    }

    @FXML
    private void showAbout() {
        AppUtils.showAbout();
    }

    @FXML
    private void goPrevPage() {
        if (pageNumber == 1)
            return;
        pageNumber--;
        bookUtils.createSearchUIAndSearch(searchText, rootPane, rootVbox, stage, pageNumber);
    }

    @FXML
    private void goNextPage() {
        pageNumber++;
        bookUtils.createSearchUIAndSearch(searchText, rootPane, rootVbox, stage, pageNumber);
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
