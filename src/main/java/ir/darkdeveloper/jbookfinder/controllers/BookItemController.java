package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BookItemController implements FXMLController {

    @FXML
    private ProgressIndicator imageProgress;
    @FXML
    private VBox imageBox;
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
    private Stage stage;

    private final BookUtils bookUtils = BookUtils.getInstance();
    private final Configs configs = Configs.getInstance();

    @Override
    public void initialize() {

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
    private void downloadBook() {
        if (bookModel == null)
            return;

        if (!downloadBtn.getText().equals("Open Book")) {
            bookUtils.downloadBookAndAddProgress(stage, bookModel, operationVbox);
            return;
        }

        var hostServices = configs.getHostServices();
        hostServices.showDocument(bookModel.getFilePath());
    }


    @FXML
    public void moreDetails() {
        bookUtils.showDetails(bookModel);
    }

}
