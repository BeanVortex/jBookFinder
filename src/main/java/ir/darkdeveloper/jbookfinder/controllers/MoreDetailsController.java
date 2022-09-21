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
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.File;

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
    private final Configs configs = Configs.getInstance();
    private final BooksRepo repo = BooksRepo.getInstance();

    @Override
    public void initialize() {
        updateTheme(configs.getTheme());
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

        bookModel = repo.findByBookId(bookModel.getBookId());
        var hostServices = configs.getHostServices();
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
        bookUtils.setDataForDetails(itemBox, bookModel);
        var vBox = (VBox) itemBox.getChildren().get(1);
        vBox.setPrefWidth(800);
        stage.heightProperty().addListener((o, ol, newVal) -> vBox.setPrefHeight((Double) newVal));
        stage.widthProperty().addListener((o, ol, newVal) -> vBox.setPrefWidth((Double) newVal));
    }

    @Override
    public void updateTheme(String theme) {
        var labels = FxUtils.getAllNodes(itemBox, Label.class);

        if (theme.equals("light")) {
            itemBox.setBackground(Background.fill(Paint.valueOf("#fff")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#333")));
            if (!downloadBtn.getStyleClass().contains("button-dark"))
                downloadBtn.getStyleClass().add("button-dark");
            downloadBtn.getStyleClass().remove("button-light");
        } else {
            itemBox.setBackground(Background.fill(Paint.valueOf("#333")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
            if (!downloadBtn.getStyleClass().contains("button-light"))
                downloadBtn.getStyleClass().add("button-light");
            downloadBtn.getStyleClass().remove("button-dark");
        }
    }

    public void setFromLibrary(boolean fromLibrary) {
        this.fromLibrary = fromLibrary;
        if (fromLibrary)
            downloadBtn.setText("Open Book");
    }
}
