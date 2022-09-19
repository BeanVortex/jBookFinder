package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class LibraryItemController implements FXMLController, ThemeObserver {


    @FXML
    private ImageView deleteBtn;
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
        var imagePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource("icons/close.png"))
                .toExternalForm();
        deleteBtn.setImage(new Image(imagePath));
        deleteBtn.setFitHeight(22);
        deleteBtn.setFitWidth(22);

        deleteBookAction();

    }

    private void deleteBookAction() {

        deleteBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            var alert = new Alert(Alert.AlertType.CONFIRMATION);
            var alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            var logoPath = FxUtils.class.getClassLoader().getResource("images/logo.png");
            if (logoPath != null)
                alertStage.getIcons().add(new Image(logoPath.toExternalForm()));
            alert.setTitle("Book deleting");
            alert.setHeaderText("You are deleting the book");
            alert.setContentText("Are you sure about this?");
            var buttonTypeOpt = alert.showAndWait();
            buttonTypeOpt.ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    try {
                        repo.deleteBook(bookModel.getId());
                        Files.delete(Paths.get(bookModel.getFilePath()));
                        libraryController.initialize();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (buttonType == ButtonType.CANCEL)
                    alert.close();
            });
        });
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
            var alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            var logoPath = FxUtils.class.getClassLoader().getResource("images/logo.png");
            if (logoPath != null)
                alertStage.getIcons().add(new Image(logoPath.toExternalForm()));
            alert.setTitle("Book not found");
            alert.setHeaderText("Book file does not exist");
            alert.setContentText("Would you like to download it again?\ncancel to delete the record");
            var buttonTypeOpt = alert.showAndWait();
            buttonTypeOpt.ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK)
                    bookUtils.downloadBookAndAddProgress(bookModel, operationVbox, stage);
                else if (buttonType == ButtonType.CANCEL) {
                    repo.deleteBook(bookModel.getId());
                    libraryController.initialize();
                }
            });
        }
    }


    @FXML
    private void moreDetails() {
        bookUtils.showDetails(bookModel, true);
    }


    @Override
    public void updateTheme(String theme) {
        List.of(detailsBtn, downloadBtn)
                .forEach(btn -> {
                    if (configs.getTheme().equals("dark")) {
                        btn.getStyleClass().remove("button-dark");
                        if (!btn.getStyleClass().contains("button-light"))
                            btn.getStyleClass().add("button-light");
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
