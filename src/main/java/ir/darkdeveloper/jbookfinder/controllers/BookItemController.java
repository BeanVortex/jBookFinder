package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BookItemController implements FXMLController {

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
    private Button downloadBook;
    @FXML
    private ImageView bookImage;

    @Override
    public void initialize() {

    }

    public void setBookModel(BookModel bookModel, List<String> bookImages) {
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


    private void fetchAndSetImageAsync(String imageUrl, List<String> bookImages) {
        CompletableFuture.supplyAsync(() -> {
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
                    }
                    return imageFile;
                })
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
