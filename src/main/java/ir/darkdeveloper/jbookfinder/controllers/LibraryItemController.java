package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class LibraryItemController implements FXMLController, ThemeObserver {


    @FXML
    private Button detailsBtn;
    @FXML
    private Button downloadBtn;
    @FXML
    private ProgressIndicator imageProgress;
    @FXML
    private VBox imageBox;
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

    private final BookUtils bookUtils = BookUtils.getInstance();
    private final Configs configs = Configs.getInstance();

    private BookModel bookModel;
    private Stage stage;

    @Override
    public void initialize() {
        updateTheme(configs.getTheme());
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }


    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
        bookUtils.displayData(bookTitle, bookAuthor, bookPublisher, bookFormat,
                bookSize, bookPages, bookYear, bookLanguage, bookModel);
        hideExtraInfo();
        bookUtils.fetchAndSetImageAsync(bookModel.getImageUrl(), bookModel.getTitle(), bookImage, imageBox, imageProgress);
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
    private void showBook() {
        if (bookModel == null)
            return;
        var hostServices = configs.getHostServices();
        hostServices.showDocument(bookModel.getFilePath());
    }


    @FXML
    private void moreDetails() {
        bookUtils.showDetails(bookModel);
    }


    @Override
    public void updateTheme(String theme) {
        List.of(detailsBtn, downloadBtn)
                .forEach(btn -> {
                    if (configs.getTheme().equals("dark")) {
                        if (!btn.getStyleClass().contains("button-light"))
                            btn.getStyleClass().add("button-light");
                        btn.getStyleClass().remove("button-dark");
                    } else {
                        if (!btn.getStyleClass().contains("button-dark"))
                            btn.getStyleClass().add("button-dark");
                        btn.getStyleClass().remove("button-light");
                    }
                });
    }
}
