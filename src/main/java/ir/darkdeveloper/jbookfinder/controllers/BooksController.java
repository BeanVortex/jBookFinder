package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;


public class BooksController implements FXMLController {

    @FXML
    private StackPane rootPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TextField fieldSearch;
    @FXML
    private FlowPane booksContainer;

    private List<BookModel> books;
    private static final IOUtils IO_UTILS = new IOUtils();
    private static final BookUtils bookUtils = new BookUtils();

    @Override
    public void initialize() {

    }

    public void showSearch(List<BookModel> books, String text) {
        this.books = books;
        fieldSearch.setText(text);
        booksContainer.requestFocus();
        books.forEach(book -> {
            try {
                var fxmlLoader = new FXMLLoader(SwitchSceneUtil.getResource("fxml/ListBookItem.fxml"));
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
        SwitchSceneUtil.switchSceneToMain(stage, "MainController.fxml", "main.css");
    }

    public void showSettings() {
        var stage = new Stage();
        var l = new Label("f");
        var scene = new Scene(l);
        stage.setScene(scene);
        stage.setHeight(200);
        stage.setWidth(300);
        stage.show();
    }

    @FXML
    private void searchTheBook(ActionEvent e) {
        var text = fieldSearch.getText();
        if (!text.isBlank()) {
            var trimmedText = text.replaceAll("\s", "");
            var progress = new ProgressIndicator();
            var vbox = new VBox(progress);
            vbox.setAlignment(Pos.CENTER);
            rootPane.setDisable(true);
            rootPane.getChildren().add(vbox);
            bookUtils.searchTheBookWithScrapper(SwitchSceneUtil.getStageFromEvent(e), trimmedText);
        }
    }

    @FXML
    private void clearImageCache() {
        IO_UTILS.deleteCachedImages(books);
    }

    @FXML
    private void newSearch() {
        fieldSearch.setText("");
        fieldSearch.requestFocus();
    }
}
