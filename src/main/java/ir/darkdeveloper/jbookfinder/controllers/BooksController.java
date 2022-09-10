package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


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

    private List<BookModel> books;
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
//        ObservableList<BookModel> booksList = FXCollections.observableArrayList();
        var booksList = new ArrayList<BookModel>();

        books.subscribe(book -> {
                    try {
                        var fxmlLoader = new FXMLLoader(FxUtils.getResource("fxml/bookItem.fxml"));
                        HBox root = fxmlLoader.load();
                        BookItemController itemController = fxmlLoader.getController();
                        itemParents.add(root);
                        itemController.setBookModel(book);
                        booksContainer.getChildren().add(root);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace,
                () -> this.books = booksList);

        updateTheme(configs.getTheme());
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
            controller.setNotToDeleteBooks(books);
    }

    @FXML
    private void searchTheBook(ActionEvent e) {
        var text = fieldSearch.getText();
        if (!text.isBlank())
            bookUtils.createSearchUI(text, rootPane, rootVbox, e);
    }


    @FXML
    private void clearImageCache() {
        ioUtils.deleteCachedImages(books);
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
