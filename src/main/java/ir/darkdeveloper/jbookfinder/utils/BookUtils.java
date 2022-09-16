package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.controllers.BooksController;
import ir.darkdeveloper.jbookfinder.controllers.MoreDetailsController;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.task.BookDownloadTask;
import ir.darkdeveloper.jbookfinder.task.ImageFetchTask;
import ir.darkdeveloper.jbookfinder.task.ScraperTask;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ir.darkdeveloper.jbookfinder.utils.FxUtils.getResource;
import static ir.darkdeveloper.jbookfinder.utils.FxUtils.getStageFromEvent;


public class BookUtils {

    private final Configs configs = Configs.getInstance();

    private static BookUtils bookUtils;

    private static final Logger log = Logger.getLogger(BookUtils.class.getName());

    private BookUtils() {

    }

    public static BookUtils getInstance() {
        if (bookUtils == null)
            bookUtils = new BookUtils();
        return bookUtils;
    }

    public void updateThemeForBooks(String theme, FlowPane booksContainer, VBox contentVbox, List<HBox> itemParents) {
        var labels = FxUtils.getAllNodes(booksContainer, Label.class);
        if (theme.equals("light")) {
            booksContainer.setBackground(Background.fill(Paint.valueOf("#fff")));
            contentVbox.setBackground(Background.fill(Paint.valueOf("#fff")));
            itemParents.forEach(parent -> parent.setBackground(Background.fill(Paint.valueOf("#fff"))));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#333")));
        } else {
            booksContainer.setBackground(Background.fill(Paint.valueOf("#333")));
            contentVbox.setBackground(Background.fill(Paint.valueOf("#333")));
            itemParents.forEach(parent -> parent.setBackground(Background.fill(Paint.valueOf("#333"))));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
        }
    }

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
        if (configs.getTheme().equals("dark"))
            progressLabel.setTextFill(Paint.valueOf("#fff"));
        else
            progressLabel.setTextFill(Paint.valueOf("#333"));
        progressBox.getChildren().addAll(progressBar, progressLabel);
        progressBox.setAlignment(Pos.CENTER);

        operationVbox.getChildren().add(1, progressBox);
        operationVbox.getChildren().get(0).setDisable(true);
        if (operationVbox.getChildren().size() == 3) {
            operationVbox.getChildren().get(2).setDisable(true);
        }


        if (downTask != null) {
            progressBar.progressProperty().bind(downTask.progressProperty());
            progressBar.progressProperty().addListener((o, ol, newVal) -> {
                var percentage = (int) (newVal.doubleValue() * 100);
                progressLabel.setText(percentage + " %");
            });
        }
    }

    private BookDownloadTask downloadBook(BookModel bookModel, VBox operationVbox) {

        var fileName = getFileName(bookModel);
        var file = new File(configs.getSaveLocation() + File.separator + fileName);
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
        return title.replaceAll("[^A-Za-z0-9()\\[\\]]", "_") + fileExt;
    }

    public String getFileName(BookModel bookModel) {
        return bookModel.getTitle()
                .replaceAll("[^A-Za-z0-9()\\[\\]]", "_") + "." + bookModel.getFileFormat();
    }

    public void completeDownload(VBox operationVbox) {
        operationVbox.getChildren().remove(1);
        operationVbox.getChildren().get(0).setDisable(false);
        if (operationVbox.getChildren().size() == 2)
            operationVbox.getChildren().get(1).setDisable(false);
        var downloadBtn = (Button) operationVbox.getChildren().get(0);
        downloadBtn.setText("Open Book");
        configs.getFxTray().showInfoMessage("Book download");
    }

    public void setDataForDetails(HBox root, BookModel bookModel) {
        var imageBox = (VBox) root.getChildren().get(0);
        var imageProgress = (ProgressIndicator) imageBox.getChildren().get(0);
        var imageView = (ImageView) imageBox.getChildren().get(1);
        try {
            var imageUrl = bookModel.getImageUrl();
            var title = bookModel.getTitle();
            var file = new File(configs.getBookCoverLocation() + getImageFileName(imageUrl, title));
            var is = new FileInputStream(file);
            imageBox.getChildren().remove(imageProgress);
            imageView.setFitHeight(imageBox.getPrefHeight());
            imageView.setFitWidth(imageBox.getPrefWidth());
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

        if (operationVbox.getChildren().size() == 2) {
            var detailsBtn = operationVbox.getChildren().get(1);
            detailsBtn.setVisible(false);
            detailsBtn.setDisable(true);
        }

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


    public void searchTheBookWithScrapper(Stage stage, String text) {
        var scrapper = new ScraperTask(text, 1);
        scrapper.valueProperty().addListener((obs, old, booksFlux) -> {
            var booksController = FxUtils.
                    switchSceneAndGetController(stage, "books.fxml", BooksController.class);
            if (booksController == null) {
                log.log(Level.WARNING, "Books controller is null");
                return;
            }
            configs.getThemeSubject().addObserver(booksController);
            booksController.setStage(stage);
            booksController.showSearch(booksFlux, text);
            booksController.resizeListViewByStage(stage);
        });

        var thread = new Thread(scrapper);
        thread.setDaemon(true);
        thread.start();
    }


    public void createSearchUI(String text, StackPane stackPane, Parent rootBox, ActionEvent e) {
        createSearchUI(text, stackPane, rootBox, getStageFromEvent(e));
    }

    public void createSearchUI(String text, StackPane stackPane, Parent rootBox, Stage stage) {
        var trimmedText = text.trim();
        var progress = new ProgressIndicator();
        var btnCancel = new Button("Cancel");
        btnCancel.setOnAction(this::cancelSearch);
        var vbox = new VBox(progress, btnCancel);
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);
        rootBox.setDisable(true);
        stackPane.getChildren().add(vbox);
        bookUtils.searchTheBookWithScrapper(stage, trimmedText);
    }


    private void cancelSearch(ActionEvent e) {
    }

    public void showDetails(BookModel bookModel) {
        try {
            var stage = new Stage();
            var fxmlLoader = new FXMLLoader(getResource("fxml/bookItemDetails.fxml"));
            HBox root = fxmlLoader.load();
            MoreDetailsController detailsController = fxmlLoader.getController();
            detailsController.setStage(stage);
            detailsController.setBookModel(bookModel);
            detailsController.initStage();
            configs.getThemeSubject().addObserver(detailsController);
            var scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(800);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

