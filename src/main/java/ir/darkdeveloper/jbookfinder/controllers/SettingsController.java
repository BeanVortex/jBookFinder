package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class SettingsController implements FXMLController, ThemeObserver {

    @FXML
    private Button btnClear;
    @FXML
    private Button btnChangeDir;
    @FXML
    private Line line1;
    @FXML
    private Line line2;
    @FXML
    private VBox parent;
    @FXML
    private Circle circleTheme;
    @FXML
    private Label labelImageCache;
    @FXML
    private Label labelLocation;

    private final Configs configs = Configs.getInstance();
    private final IOUtils ioUtils = IOUtils.getInstance();
    private final BooksRepo booksRepo = BooksRepo.getInstance();

    private List<BookModel> notToDeleteBooks;
    private Stage stage;
    private List<Label> labels;


    @Override
    public void initialize() {
        labels = FxUtils.getAllNodes(parent, Label.class);
        labelImageCache.setText(String.valueOf(ioUtils.getFolderSize(new File(configs.getBookCoverLocation()))));
        labelLocation.setText(configs.getSaveLocation());

        updateTheme(configs.getTheme());

        circleTheme.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (configs.getTheme().equals("light")) {
                circleTheme.setFill(Paint.valueOf("#fff"));
                circleTheme.setStroke(Paint.valueOf("#333"));
                parent.setBackground(Background.fill(Paint.valueOf("#333")));
                labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
                configs.setTheme("dark");
            } else {
                circleTheme.setFill(Paint.valueOf("#333"));
                circleTheme.setStroke(Paint.valueOf("#fff"));
                parent.setBackground(Background.fill(Paint.valueOf("#fff")));
                labels.forEach(label -> label.setTextFill(Paint.valueOf("#111")));
                configs.setTheme("light");
            }
            ioUtils.saveConfigs(null);
        });

    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        resizeLinesByStage();
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    // should be called in books controller
    public void setNotToDeleteBooks(List<BookModel> notToDeleteBooks) {
        this.notToDeleteBooks = notToDeleteBooks;
    }

    @FXML
    private void clearCache() {
        ioUtils.deleteCachedImages(notToDeleteBooks, stage);
        labelImageCache.setText(String.valueOf(ioUtils.getFolderSize(new File(configs.getBookCoverLocation()))));
    }

    @FXML
    private void changeSaveDir(ActionEvent e) {
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select books save location");
        dirChooser.setInitialDirectory(new File(configs.getSaveLocation()));
        var selectedDir = dirChooser.showDialog(FxUtils.getStageFromEvent(e));
        if (selectedDir != null) {
            var files = selectedDir.listFiles();
            if (files != null && files.length == 0) {
                ioUtils.createSaveLocation();
                ioUtils.saveConfigs(selectedDir.getPath());
                labelLocation.setText(configs.getSaveLocation());
                booksRepo.updateBooksPath(configs.getSaveLocation());
            } else
                configs.getFxTray().showErrorMessage("Directory is not empty");
        }
    }


    public void resizeLinesByStage() {
        stage.widthProperty().addListener((obs, old, newVal) -> parent.setPrefWidth((Double) newVal));
        parent.prefWidthProperty().addListener((obs, old, newValue) -> {
            var padding = parent.getPadding().getRight() + parent.getPadding().getLeft();
            line1.setEndX((Double) newValue - padding);
            line2.setEndX((Double) newValue - padding);
        });
    }

    @Override
    public void updateTheme(String theme) {
        if (configs.getTheme().equals("light")) {
            circleTheme.setFill(Paint.valueOf("#333"));
            parent.setBackground(Background.fill(Paint.valueOf("#fff")));
        } else {
            circleTheme.setFill(Paint.valueOf("#fff"));
            parent.setBackground(Background.fill(Paint.valueOf("#333")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
        }

        List.of(btnChangeDir, btnClear)
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
}
