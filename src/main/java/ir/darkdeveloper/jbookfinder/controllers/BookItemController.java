package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil.getResource;

public class BookItemController implements FXMLController {

    @FXML
    private Button downloadBtn;
    @FXML
    private VBox operationVbox;
    @FXML
    private Label bookTitle;
    @FXML
    private Label bookAuthor;
    @FXML
    private Label bookPublisher;
    @FXML
    private Label bookFormat;
    @FXML
    private Label bookSize;
    @FXML
    private Label bookPages;
    @FXML
    private Label bookYear;
    @FXML
    private Label bookLanguage;
    @FXML
    private ImageView bookImage;

    private BookModel bookModel;

    private String cachedImage;

    @Override
    public void initialize() {

    }

    public void setBookModel(BookModel bookModel, List<String> bookImages) {
        this.bookModel = bookModel;
        displayData(bookTitle, bookAuthor, bookPublisher, bookFormat,
                bookSize, bookPages, bookYear, bookLanguage, bookModel);
        hideExtraInfo();
        fetchAndSetImageAsync(bookModel.getImageUrl(), bookImages);
    }

    private void hideExtraInfo() {
        bookPublisher.setVisible(false);
        bookPublisher.managedProperty().bind(bookPublisher.visibleProperty());
        bookYear.setVisible(false);
        bookYear.managedProperty().bind(bookYear.visibleProperty());
        bookLanguage.setVisible(false);
        bookLanguage.managedProperty().bind(bookLanguage.visibleProperty());
    }


    @FXML
    private void downloadBook() {
        if (bookModel == null)
            return;

        if (!downloadBtn.getText().equals("Open Book")) {
            var bookUtils = new BookUtils();
            bookUtils.downloadBookAndAddProgress(bookModel, operationVbox);
            return;
        }

        System.out.println("Show Book");
    }


    @FXML
    private void moreDetails() throws IOException {
        var stage = new Stage();
        HBox root = FXMLLoader.load(getResource("fxml/ListBookItem.fxml"));
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(800);
        stage.setMinWidth(root.getMinWidth());
        stage.setMinHeight(root.getMinHeight());
        setDataForDetails(root);
        stage.show();
        var vBox = (VBox) root.getChildren().get(1);
        vBox.setPrefWidth(800);
        stage.heightProperty().addListener((o, ol, newVal) -> vBox.setPrefHeight((Double) newVal));
        stage.widthProperty().addListener((o, ol, newVal) -> vBox.setPrefWidth((Double) newVal));
    }

    private void setDataForDetails(HBox root) {
        var imageView = (ImageView) root.getChildren().get(0);
        try {
            var file = new File(cachedImage);
            var is = new FileInputStream(file);
            imageView.setImage(new Image(is));
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
        }

        var vBox = (VBox) root.getChildren().get(1);
        var bookTitle = (Label) vBox.getChildren().get(0);
        var bookAuthor = (Label) vBox.getChildren().get(1);
        var bookPublisher = (Label) vBox.getChildren().get(2);
        var bookFormat = (Label) vBox.getChildren().get(3);
        var bookSize = (Label) vBox.getChildren().get(4);
        var bookPages = (Label) vBox.getChildren().get(5);
        var bookYear = (Label) vBox.getChildren().get(6);
        var bookLanguage = (Label) vBox.getChildren().get(7);
        var operationVbox = (VBox) vBox.getChildren().get(8);
        var downloadBtn = (Button) operationVbox.getChildren().get(0);
        var detailsBtn = operationVbox.getChildren().get(1);
        detailsBtn.setVisible(false);
        downloadBtn.setVisible(false);
        detailsBtn.setDisable(true);
        downloadBtn.setDisable(true);

        displayData(bookTitle, bookAuthor, bookPublisher,
                bookFormat, bookSize, bookPages, bookYear,
                bookLanguage, bookModel);

    }

    private void displayData(Label bookTitle, Label bookAuthor, Label bookPublisher, Label bookFormat, Label bookSize,
                             Label bookPages, Label bookYear, Label bookLanguage, BookModel bookModel) {
        bookTitle.setText("Title: " + bookModel.getTitle());
        bookAuthor.setText("Author: " + bookModel.getAuthor());
        bookPublisher.setText("Publisher: " + bookModel.getPublisher());
        bookFormat.setText("Format: " + bookModel.getFileFormat());
        bookSize.setText("Size: " + bookModel.getSize());
        bookPages.setText("Pages: " + bookModel.getPages());
        bookYear.setText("Year: " + bookModel.getYear());
        bookLanguage.setText("Language: " + bookModel.getLanguage());
    }


    private void fetchAndSetImageAsync(String imageUrl, List<String> bookImages) {
        Supplier<File> downloadSup = () -> {
            if (imageUrl == null)
                return null;
            var imageFile = new File("src/main/resources/book_images/" + UUID.randomUUID() + ".jpg");
            try {
                FileUtils.copyURLToFile(
                        new URL(imageUrl),
                        imageFile
                );
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            cachedImage = imageFile.getPath();
            return imageFile;
        };

        CompletableFuture.supplyAsync(downloadSup)
                .whenComplete((file, throwable) -> {
                    try {
                        var finalFile = file;
                        if (file == null)
                            finalFile = new File("src/main/resources/images/blank.png");
                        bookImages.add(finalFile.getName());
                        var inputStream = new FileInputStream(finalFile);
                        var image = new Image(inputStream);
                        bookImage.setImage(image);
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
