package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class MoreDetailsController implements FXMLController, ThemeObserver {


    @FXML
    private HBox itemBox;
    @FXML
    private Button downloadBtn;
    @FXML
    private VBox operationVbox;


    private Stage stage;
    private BookModel bookModel;

    private final BookUtils bookUtils = BookUtils.getInstance();
    private final Configs configs = Configs.getInstance();

    @Override
    public void initialize() {
        updateTheme(configs.getTheme());
    }



    @FXML
    private void downloadBook() {
        if (bookModel == null)
            return;

        if (!downloadBtn.getText().equals("Open Book")) {
            bookUtils.downloadBookAndAddProgress(bookModel, operationVbox);
            return;
        }

        System.out.println("Show Book");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
    }

    public void initStage(){
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
        var labels = FxUtils.getAllLabels(itemBox);

        if (theme.equals("light")) {
            itemBox.setBackground(Background.fill(Paint.valueOf("#fff")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#333")));
        } else {
            itemBox.setBackground(Background.fill(Paint.valueOf("#333")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
        }
    }
}
