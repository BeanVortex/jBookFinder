package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class LibraryItemController implements FXMLController, ThemeObserver {


    @FXML
    private VBox operationVbox;
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
    private final BooksRepo repo = BooksRepo.getInstance();

    private BookModel bookModel;
    private Stage stage;
    private LibraryController libraryController;

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
        var filePath = bookModel.getFilePath();
        if (new File(filePath).exists()) {
            var hostServices = configs.getHostServices();
            hostServices.showDocument(filePath);
        } else {
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Book not found");
            alert.setHeaderText("Book file does not exist");
            alert.setContentText("Would you like to download it again?\ncancel to delete the record");
            var buttonTypeOpt = alert.showAndWait();
            buttonTypeOpt.ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    bookUtils.downloadBookAndAddProgress(bookModel, operationVbox);
                } else if (buttonType == ButtonType.CANCEL) {
                    repo.deleteBook(bookModel.getId());
                    libraryController.initialize();
                }
            });
        }
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

    public void setLibController(LibraryController libraryController) {
        this.libraryController = libraryController;
    }
}
