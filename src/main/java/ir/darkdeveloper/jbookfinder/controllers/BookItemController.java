package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import static ir.darkdeveloper.jbookfinder.utils.FxUtils.getResource;

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

    private final ObjectProperty<BookModel> bookModel = new SimpleObjectProperty<>(this, "bookModel", null);
    private final BookUtils bookUtils = BookUtils.getInstance();
    private final Configs configs = Configs.getInstance();

    @Override
    public void initialize() {

    }

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public Stage getStage() {
        return null;
    }

    public BookModel getBookModel() {
        return bookModel.get();
    }

    public ObjectProperty<BookModel> bookModelProperty() {
        return bookModel;
    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel.set(bookModel);
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
        if (bookModel.get() == null)
            return;

        if (!downloadBtn.getText().equals("Open Book")) {
            bookUtils.downloadBookAndAddProgress(bookModel.get(), operationVbox);
            return;
        }

        System.out.println("Show Book");
    }


    @FXML
    private void moreDetails() throws IOException {
        var stage = new Stage();
        var fxmlLoader = new FXMLLoader(getResource("fxml/bookItemDetails.fxml"));
        HBox root = fxmlLoader.load();
        MoreDetailsController detailsController = fxmlLoader.getController();
        detailsController.setStage(stage);
        detailsController.setBookModel(bookModel.get());
        detailsController.initStage();
        configs.getThemeSubject().addObserver(detailsController);
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(800);
        stage.show();
    }

}
