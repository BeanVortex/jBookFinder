package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.task.BookDownloadTask;
import ir.darkdeveloper.jbookfinder.task.ImageFetchTask;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil.getResource;

@Getter
@Setter
public class BookUtils {
    private String saveLocation = System.getProperty("user.home") + "/Downloads/JBookFinder/";


    public void downloadBookAndAddProgress(BookModel bookModel, VBox operationVbox) {
        var downTask = downloadBook(bookModel, operationVbox);
        if (downTask != null)
            addProgress(operationVbox, downTask);
    }

    private void addProgress(VBox operationVbox, BookDownloadTask downTask) {
        var progressBox = new HBox();
        var progressLabel = new Label();
        var progressBar = new ProgressBar();
        progressBox.setSpacing(8);
        progressLabel.setText("0 %");
        progressBox.getChildren().addAll(progressBar, progressLabel);
        progressBox.setAlignment(Pos.CENTER);

        operationVbox.getChildren().add(1, progressBox);
        operationVbox.getChildren().get(0).setDisable(true);
        operationVbox.getChildren().get(2).setDisable(true);


        if (downTask != null) {
            progressBar.progressProperty().bind(downTask.progressProperty());
            progressBar.progressProperty().addListener((o, ol, newVal) -> {
                var percentage = (int) (newVal.doubleValue() * 100);
                progressLabel.setText(percentage + " %");
            });
        }
    }

    private BookDownloadTask downloadBook(BookModel bookModel, VBox operationVbox) {
        var fileName = bookModel.getTitle()
                .replaceAll("[^A-Za-z0-9()\\[\\]]", "_") + "." + bookModel.getFileFormat();
        var file = new File(saveLocation + fileName);
        if (file.exists()) {
            addProgress(operationVbox, null);
            completeDownload(operationVbox);
            return null;
        } else {
            var downTask = new BookDownloadTask(bookModel, operationVbox, fileName);
            var taskT = new Thread(downTask);
            taskT.setDaemon(true);
            taskT.start();
            return downTask;
        }
    }


    // Todo: save by book name
    public void fetchAndSetImageAsync(String imageUrl, String title, List<String> bookImages, ImageView bookImage) {
        var fileExt = imageUrl.substring(imageUrl.lastIndexOf('.'));
        var fileName = title.replaceAll("[^A-Za-z0-9()\\[\\]]", "_") + fileExt;
        var fetchTask = new ImageFetchTask(imageUrl, fileName, bookImages, bookImage);
        var taskT = new Thread(fetchTask);
        taskT.setDaemon(true);
        taskT.start();
    }


    public void completeDownload(VBox operationVbox) {
        operationVbox.getChildren().remove(1);
        operationVbox.getChildren().get(0).setDisable(false);
        operationVbox.getChildren().get(1).setDisable(false);
        var downloadBtn = (Button) operationVbox.getChildren().get(0);
        downloadBtn.setText("Open Book");

        var tray = SystemTray.getSystemTray();
        var trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(getResource("images/blank.png")), "notif");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        trayIcon.displayMessage("Book Downloaded", "I downloaded the book", TrayIcon.MessageType.INFO);
    }

    public void setDataForDetails(HBox root, BookModel bookModel, List<String> bookImages, int iterationNum) {
        var imageView = (ImageView) root.getChildren().get(0);
        try {
            var file = new File(saveLocation + "bookImages/" + bookImages.get(iterationNum));
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
}

