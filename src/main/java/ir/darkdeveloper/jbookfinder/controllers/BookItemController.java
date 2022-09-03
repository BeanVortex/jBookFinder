package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class BookItemController implements FXMLController {

    @FXML
    private Button downloadBtn;

    @FXML
    private VBox operationVbox;
    @FXML
    private Button moreDetails;
    @FXML
    private Label bookTitle;
    @FXML
    private Label bookAuthor;
    //    @FXML
//    private Label bookPublisher;
    @FXML
    private Label bookFormat;
    @FXML
    private Label bookSize;
    @FXML
    private Label bookPages;
    //    @FXML
//    private Label bookYear;
//    @FXML
//    private Label bookLanguage;
    @FXML
    private ImageView bookImage;

    private BookModel bookModel;

    @Override
    public void initialize() {

    }

    public void setBookModel(BookModel bookModel, List<String> bookImages) {
        this.bookModel = bookModel;
        bookTitle.setText("Title: " + bookModel.getTitle());
        bookAuthor.setText("Author: " + bookModel.getAuthor());
//        bookPublisher.setText("Publisher: " + bookModel.getPublisher());
        bookFormat.setText("Format: " + bookModel.getFileFormat());
        bookSize.setText("Size: " + bookModel.getSize());
        bookPages.setText("Pages: " + bookModel.getPages());
//        bookYear.setText("Year: " + bookModel.getYear());
//        bookLanguage.setText("Language: " + bookModel.getLanguage());

        fetchAndSetImageAsync(bookModel.getImageUrl(), bookImages);
    }


    @FXML
    private void downloadBook(ActionEvent e) {
        if (bookModel == null)
            return;

        if (!downloadBtn.getText().equals("Open Book")){
            var bookUtils = new BookUtils();
            bookUtils.downloadBookAndAddProgress(bookModel, operationVbox);
            return;
        }

        System.out.println("Show Book");
    }


    @FXML
    private void moreDetails(ActionEvent e) {
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
