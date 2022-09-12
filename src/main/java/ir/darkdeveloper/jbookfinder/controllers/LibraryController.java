package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LibraryController implements FXMLController, ThemeObserver {

    @FXML
    private MenuBar menuBar;
    @FXML
    private VBox contentVbox;
    @FXML
    private FlowPane booksContainer;

    private Stage stage;

    private final List<HBox> itemParents = new ArrayList<>();
    private final BooksRepo booksRepo = BooksRepo.getInstance();
    private final Configs configs = Configs.getInstance();
    private final BookUtils bookUtils = BookUtils.getInstance();
    private List<BookModel> booksList;

    @FXML
    private void getBack() {
        var stage = (Stage) menuBar.getScene().getWindow();
        FxUtils.switchSceneToMain(stage, "main.fxml");
    }

    @FXML
    private void showSettings() {
        var controller = (SettingsController) FxUtils
                .newStageAndReturnController("settings.fxml", 450, 500);
        if (controller != null)
            controller.setNotToDeleteBooks(booksList);
    }

    @Override
    public void initialize() {
        var fetchedBooks = booksRepo.getBooks();
        booksList = new ArrayList<>(fetchedBooks);
        fetchedBooks.forEach(book -> {
            try {
                var fxmlLoader = new FXMLLoader(FxUtils.getResource("fxml/bookItemLibrary.fxml"));
                HBox root = fxmlLoader.load();
                itemParents.add(root);
                LibraryItemController itemController = fxmlLoader.getController();
                itemController.setBookModel(book);
                booksContainer.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        updateTheme(configs.getTheme());
    }

    public void resizeListViewByStage() {
        booksContainer.setPrefHeight(stage.getHeight());
        booksContainer.setPrefWidth(stage.getWidth());
        stage.widthProperty().addListener((obs, old, newVal) -> booksContainer.setPrefWidth((Double) newVal));
        stage.heightProperty().addListener((obs, old, newVal) -> booksContainer.setPrefHeight((Double) newVal));
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void updateTheme(String theme) {
        bookUtils.updateThemeForBooks(theme, booksContainer, contentVbox, itemParents);
    }
}
