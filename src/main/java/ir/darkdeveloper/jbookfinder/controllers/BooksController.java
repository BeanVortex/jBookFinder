package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;


public class BooksController implements FXMLController {

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
    private final IOUtils ioUtils = IOUtils.getInstance();
    private final BookUtils bookUtils = BookUtils.getInstance();

    @Override
    public void initialize() {

    }

    public void showSearch(List<BookModel> books, String text) {
        this.books = books;
        fieldSearch.setText(text);
        booksContainer.requestFocus();
        books.forEach(book -> {
            try {
                var fxmlLoader = new FXMLLoader(FxUtils.getResource("fxml/bookItem.fxml"));
                Parent root = fxmlLoader.load();
                BookItemController itemController = fxmlLoader.getController();
                itemController.setBookModel(book);
                booksContainer.getChildren().add(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
}
