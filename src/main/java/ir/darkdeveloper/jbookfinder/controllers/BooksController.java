package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.service.ScraperService;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class BooksController implements FXMLController {
    @FXML
    private FlowPane booksContainer;

    private final List<String> bookImages = new ArrayList<>();
    private BookItemController itemController;

    @Override
    public void initialize() {

    }

    public void showSearch(List<BookModel> bookModels) {
        bookModels.forEach(bookModel -> {
                    try {
                        var fxmlLoader = new FXMLLoader(SwitchSceneUtil.getResource("fxml/ListBookItem.fxml"));
                        Parent root = fxmlLoader.load();
                        itemController = fxmlLoader.getController();
                        itemController.setBookModel(bookModel, bookImages);
                        booksContainer.getChildren().add(root);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );

    }

    public void resizeListViewByStage(Stage stage) {
        stage.widthProperty().addListener((obs, old, newVal) -> booksContainer.setPrefWidth((Double) newVal));
        stage.heightProperty().addListener((obs, old, newVal) -> booksContainer.setPrefHeight((Double) newVal));
    }

    @FXML
    private void getBack(ActionEvent e) {
        SwitchSceneUtil.switchScene(e, "MainController.fxml", "main.css");
        booksContainer.getChildren().forEach(node ->
                {
                    var imageView = ((ImageView) ((HBox) node).getChildren().get(0));
                    imageView.setImage(null);
                }
        );
        bookImages.forEach(name ->
                {
                    try {
                        Files.delete(Path.of("src/main/resources/book_images/" + name));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        );
    }
}
