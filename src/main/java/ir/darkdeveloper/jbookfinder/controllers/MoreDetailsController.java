package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static ir.darkdeveloper.jbookfinder.JBookFinder.getResource;

public class MoreDetailsController implements FXMLController, ThemeObserver {


    @FXML
    private HBox itemBox;
    @FXML
    private Button downloadBtn;
    @FXML
    private VBox operationVbox;


    private Stage stage;
    private BookModel bookModel;
    private boolean fromLibrary;

    private final BookUtils bookUtils = BookUtils.getInstance();

    @Override
    public void initialize() {
        updateTheme(Configs.getTheme());
    }


    @FXML
    private void downloadBook() {
        if (bookModel == null)
            return;

        if (fromLibrary) {
            var filePath = bookModel.getFilePath();
            if (!new File(filePath).exists()) {
                var alert = new Alert(Alert.AlertType.ERROR);
                var alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                var logoPath = getResource("images/logo.png");
                if (logoPath != null)
                    alertStage.getIcons().add(new Image(logoPath.toExternalForm()));
                alert.setTitle("Book not found");
                alert.setHeaderText("Book file does not exist");
                alert.show();
                return;
            }
        }

        if (!downloadBtn.getText().equals("Open Book")) {
            bookUtils.downloadBookAndAddProgress(bookModel, operationVbox, stage);
            return;
        }

        bookModel = BooksRepo.findByBookId(bookModel.getBookId());
        var hostServices = Configs.getHostServices();
        if (bookModel != null)
            hostServices.showDocument(bookModel.getFilePath());

    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return null;
    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
    }

    public void initStage() {
        stage.setMinWidth(itemBox.getMinWidth());
        stage.setMinHeight(itemBox.getMinHeight());
        setDataForDetails(itemBox, bookModel);
        var vBox = (VBox) itemBox.getChildren().get(1);
        vBox.setPrefWidth(800);
        stage.heightProperty().addListener((o, ol, newVal) -> vBox.setPrefHeight((Double) newVal));
        stage.widthProperty().addListener((o, ol, newVal) -> vBox.setPrefWidth((Double) newVal));
    }

    @Override
    public void updateTheme(String theme) {
        var labels = FxUtils.getAllNodes(itemBox, Label.class);
        FxUtils.updateButtonTheme(List.of(downloadBtn));

        if (theme.equals("light")) {
            itemBox.setBackground(Background.fill(Paint.valueOf("#fff")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#333")));
        } else {
            itemBox.setBackground(Background.fill(Paint.valueOf("#333")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
        }
    }

    public void setFromLibrary(boolean fromLibrary) {
        this.fromLibrary = fromLibrary;
        if (fromLibrary)
            downloadBtn.setText("Open Book");
    }


    public void setDataForDetails(HBox root, BookModel bookModel) {
        var imageBox = (VBox) root.getChildren().get(0);
        var imageProgress = (ProgressIndicator) imageBox.getChildren().get(0);
        var imageView = (ImageView) imageBox.getChildren().get(1);
        try {
            var imageUrl = bookModel.getImageUrl();
            var title = bookModel.getTitle();
            var file = new File(Configs.getBookCoverLocation() + bookUtils.getImageFileName(imageUrl, title));
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

        bookUtils.displayData(bookTitle, bookAuthor, bookPublisher,
                bookFormat, bookSize, bookPages, bookYear,
                bookLanguage, bookModel);

    }

}
