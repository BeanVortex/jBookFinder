package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.util.List;

public class SettingsController implements FXMLController, ThemeObserver {

    @FXML
    private ComboBox<String> filterCombo;
    @FXML
    private ComboBox<String> resultsCombo;
    @FXML
    private CheckBox downCheck;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnChangeDir;
    @FXML
    private Line line1;
    @FXML
    private Line line2;
    @FXML
    private Line line3;
    @FXML
    private VBox parent;
    @FXML
    private Circle circleTheme;
    @FXML
    private Label labelImageCache;
    @FXML
    private Label labelLocation;


    private List<BookModel> notToDeleteBooks;
    private Stage stage;
    private List<Label> labels;


    @Override
    public void initialize() {
        labels = FxUtils.getAllNodes(parent, Label.class);
        labelImageCache.setText(IOUtils.getFolderSize(new File(Configs.getBookCoverLocation())));
        labelLocation.setText(Configs.getSaveLocation());

        updateTheme(Configs.getTheme());

        resultsCombo.getItems().addAll("25", "50", "100");
        resultsCombo.setValue(Configs.getResultCount());
        filterCombo.getItems().addAll("All files", "pdf,rar,epub");
        filterCombo.setValue(Configs.getFilterResult());


        downCheck.setSelected(Configs.isBackgroundDownload());
        downCheck.setOnAction(event -> {
            Configs.setBackgroundDownload(downCheck.isSelected());
            IOUtils.saveConfigs();
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
        IOUtils.deleteCachedImages(notToDeleteBooks);
        labelImageCache.setText(IOUtils.getFolderSize(new File(Configs.getBookCoverLocation())));
    }

    @FXML
    private void changeSaveDir(ActionEvent e) {
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select books save location");
        dirChooser.setInitialDirectory(new File(Configs.getSaveLocation()));
        var selectedDir = dirChooser.showDialog(FxUtils.getStageFromEvent(e));
        if (selectedDir != null) {
            var files = selectedDir.listFiles();
            if (files != null && files.length == 0) {
                var path = selectedDir.getPath();
                Configs.setSaveLocation(path);
                IOUtils.createSaveLocation();
                IOUtils.saveConfigs();
                IOUtils.moveAndDeletePreviousData(path);
                labelLocation.setText(Configs.getSaveLocation());
                BooksRepo.updateBooksPath(Configs.getSaveLocation());
            } else {
                Notifications.create()
                        .title("Not Empty Dir")
                        .text("Directory must be empty")
                        .showError();
            }
        }
    }


    public void resizeLinesByStage() {
        stage.widthProperty().addListener((obs, old, newVal) -> parent.setPrefWidth((Double) newVal));
        parent.prefWidthProperty().addListener((obs, old, newValue) -> {
            var padding = parent.getPadding().getRight() + parent.getPadding().getLeft();
            line1.setEndX((Double) newValue - padding);
            line2.setEndX((Double) newValue - padding);
            line3.setEndX((Double) newValue - padding);
        });
    }

    @Override
    public void updateTheme(String theme) {
        if (Configs.getTheme().equals("light")) {
            circleTheme.setFill(Paint.valueOf("#333"));
            parent.setBackground(Background.fill(Paint.valueOf("#fff")));
        } else {
            circleTheme.setFill(Paint.valueOf("#fff"));
            parent.setBackground(Background.fill(Paint.valueOf("#333")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
        }

        circleTheme.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (Configs.getTheme().equals("light")) {
                circleTheme.setFill(Paint.valueOf("#fff"));
                circleTheme.setStroke(Paint.valueOf("#333"));
                parent.setBackground(Background.fill(Paint.valueOf("#333")));
                labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
                Configs.setTheme("dark");
            } else {
                circleTheme.setFill(Paint.valueOf("#333"));
                circleTheme.setStroke(Paint.valueOf("#fff"));
                parent.setBackground(Background.fill(Paint.valueOf("#fff")));
                labels.forEach(label -> label.setTextFill(Paint.valueOf("#111")));
                Configs.setTheme("light");
            }
            IOUtils.saveConfigs();
        });

        FxUtils.updateButtonTheme(List.of(btnChangeDir, btnClear));
    }

    @FXML
    private void openGithubPage(ActionEvent e) {
        var hyperlink = (Hyperlink) e.getSource();
        Configs.getHostServices().showDocument(hyperlink.getText());
    }

    @FXML
    private void onResultComboChanged() {
        Configs.setResultCount(resultsCombo.getSelectionModel().getSelectedItem());
        IOUtils.saveConfigs();
    }

    @FXML
    private void onFilterComboChanged() {
        Configs.setFilterResult(filterCombo.getSelectionModel().getSelectedItem());
        IOUtils.saveConfigs();
    }
}
