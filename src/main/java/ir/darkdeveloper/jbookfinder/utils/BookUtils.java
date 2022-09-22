package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.controllers.BooksController;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.task.BookDownloadTask;
import ir.darkdeveloper.jbookfinder.task.ImageFetchTask;
import ir.darkdeveloper.jbookfinder.task.ScraperTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ir.darkdeveloper.jbookfinder.JBookFinder.getResource;
import static ir.darkdeveloper.jbookfinder.utils.FxUtils.getStageFromEvent;


public class BookUtils {


    private static BookUtils bookUtils;

    private static final Logger log = Logger.getLogger(BookUtils.class.getName());

    private BookUtils() {
    }

    public static BookUtils getInstance() {
        if (bookUtils == null)
            bookUtils = new BookUtils();
        return bookUtils;
    }


    public void downloadBookAndAddProgress(BookModel bookModel, VBox operationVbox, Stage stage) {
        var fileName = getFileName(bookModel);
        var file = new File(Configs.getSaveLocation() + File.separator + fileName);
        if (file.exists()) {
            addProgressAndCancel(operationVbox, null);
            completeDownload(operationVbox, bookModel.getTitle());
        } else {
            var downTask = new BookDownloadTask(bookModel, operationVbox, fileName);
            addProgressAndCancel(operationVbox, downTask);
            stage.sceneProperty().addListener((obs, old, newV) -> downTask.cancel());
            var taskT = new Thread(downTask);
            taskT.setDaemon(true);
            taskT.start();
        }
    }

    private void addProgressAndCancel(VBox operationVbox, BookDownloadTask downTask) {
        try {
            var loader = new FXMLLoader(getResource("fxml/download_ui.fxml"));
            HBox progressBox = loader.load();
            var progressBar = (ProgressBar) progressBox.getChildren().get(0);
            var progressLabel = (Label) progressBox.getChildren().get(1);
            var imageView = (ImageView) progressBox.getChildren().get(2);
            var imagePath = getResource("icons/close.png").toExternalForm();
            imageView.setImage(new Image(imagePath));
            imageView.setFitWidth(24);
            imageView.setFitHeight(24);
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                downTask.cancel(true);
                operationVbox.getChildren().get(0).setDisable(false);
                if (operationVbox.getChildren().size() == 3)
                    operationVbox.getChildren().get(2).setDisable(false);
                operationVbox.getChildren().remove(progressBox);
            });

            progressLabel.setText("0 %");
            if (Configs.getTheme().equals("dark"))
                progressLabel.setTextFill(Paint.valueOf("#fff"));
            else
                progressLabel.setTextFill(Paint.valueOf("#333"));

            operationVbox.getChildren().add(1, progressBox);
            operationVbox.getChildren().get(0).setDisable(true);
            if (operationVbox.getChildren().size() == 3)
                operationVbox.getChildren().get(2).setDisable(true);

            if (downTask != null) {
                progressBar.progressProperty().bind(downTask.progressProperty());
                progressBar.progressProperty().addListener((o, ol, newVal) -> {
                    var percentage = (int) (newVal.doubleValue() * 100);
                    progressLabel.setText(percentage + " %");
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fetchAndSetImageAsync(String imageUrl, String title, ImageView bookImage,
                                      VBox imageBox, ProgressIndicator imageProgress) {
        var fileName = getImageFileName(imageUrl, title);
        var fetchTask = new ImageFetchTask(imageUrl, fileName, bookImage, imageBox, imageProgress);
        var taskT = new Thread(fetchTask);
        taskT.setDaemon(true);
        taskT.start();
    }

    public String getImageFileName(String imageUrl, String title) {
        var fileExt = imageUrl.substring(imageUrl.lastIndexOf('.'));
        return title.replaceAll("[~\\-{}'&%$!^():/\"\\[\\]]", "_") + fileExt;
    }

    public String getFileName(BookModel bookModel) {
        return bookModel.getTitle()
                .replaceAll("[~\\-{}'&%$!^():/\"\\[\\]]", "_") + "." + bookModel.getFileFormat();
    }

    public void completeDownload(VBox operationVbox, String bookTitle) {
        Platform.runLater(
                () -> Notifications.create()
                        .title("Operation complete")
                        .text("Book " + bookTitle.substring(bookTitle.length() / 2) + " downloaded successfully")
                        .showInformation()
        );
        operationVbox.getChildren().remove(1);
        operationVbox.getChildren().get(0).setDisable(false);
        if (operationVbox.getChildren().size() == 2)
            operationVbox.getChildren().get(1).setDisable(false);
        var downloadBtn = (Button) operationVbox.getChildren().get(0);
        downloadBtn.setText("Open Book");
    }

    public void displayData(Label bookTitle, Label bookAuthor, Label bookPublisher, Label bookFormat, Label bookSize,
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


    public void createSearchUIAndSearch(String text, StackPane stackPane, Parent rootBox, ActionEvent e) {
        createSearchUIAndSearch(text, stackPane, rootBox, getStageFromEvent(e));
    }

    public void createSearchUIAndSearch(String text, StackPane stackPane, Parent rootBox, Stage stage) {
        var trimmedText = text.trim();
        var progress = new ProgressIndicator();
        var btnCancel = new Button("Cancel");
        var vbox = new VBox(progress, btnCancel);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        rootBox.setDisable(true);
        stackPane.getChildren().add(vbox);


        var scrapper = new ScraperTask(trimmedText, 1);
        scrapper.valueProperty().addListener((obs, old, booksFlux) -> {
            var booksController = FxUtils.
                    switchSceneAndGetController(stage, "books.fxml", "Book Search", BooksController.class);
            if (booksController == null) {
                log.log(Level.WARNING, "Books controller is null");
                return;
            }
            Configs.getThemeSubject().addObserver(booksController);
            booksController.setStage(stage);
            booksController.showSearch(booksFlux, text);
            booksController.resizeListViewByStage(stage);
        });

        btnCancel.setOnAction(event -> {
            scrapper.cancel(true);
            stackPane.getChildren().remove(vbox);
            rootBox.setDisable(false);
        });

        var thread = new Thread(scrapper);
        thread.setDaemon(true);
        thread.start();
    }

}

