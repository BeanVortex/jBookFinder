package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class BooksController implements FXMLController {
    @FXML
    private FlowPane booksContainer;

    private final List<String> bookImages = new ArrayList<>();

    @Override
    public void initialize() {

    }

    public void showSearch(List<BookModel> bookModels) {
        for (int i = 0; i < bookModels.size(); i++) {
            try {
                var fxmlLoader = new FXMLLoader(SwitchSceneUtil.getResource("fxml/ListBookItem.fxml"));
                Parent root = fxmlLoader.load();
                BookItemController itemController = fxmlLoader.getController();
                itemController.setBookModel(bookModels.get(i), bookImages, i);
                booksContainer.getChildren().add(root);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void resizeListViewByStage(Stage stage) {
        booksContainer.setPrefHeight(stage.getHeight());
        booksContainer.setPrefWidth(stage.getWidth());
        stage.widthProperty().addListener((obs, old, newVal) -> booksContainer.setPrefWidth((Double) newVal));
        stage.heightProperty().addListener((obs, old, newVal) -> booksContainer.setPrefHeight((Double) newVal));
    }

    @FXML
    private void getBack(ActionEvent e) {
        booksContainer.getChildren().forEach(node ->
                {
                    var imageView = ((ImageView) ((HBox) node).getChildren().get(0));
                    imageView.setImage(null);
                }
        );
//        bookImages.forEach(name ->
//                {
//                    try {
//                        Files.delete(Path.of("src/main/resources/book_images/" + name));
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//        );
        SwitchSceneUtil.switchSceneToMain(e, "MainController.fxml", "main.css");
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
}
