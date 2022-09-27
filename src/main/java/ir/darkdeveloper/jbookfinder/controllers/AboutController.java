package ir.darkdeveloper.jbookfinder.controllers;

import ir.darkdeveloper.jbookfinder.JBookFinder;
import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.config.ThemeObserver;
import ir.darkdeveloper.jbookfinder.utils.AppUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.List;

public class AboutController implements FXMLController, ThemeObserver {

    @FXML
    private VBox parent;
    @FXML
    private ImageView logoImg;
    @FXML
    private Label versionLbl;
    @FXML
    private Button updateBtn;

    private Stage stage;
    private List<Label> labels;


    @Override
    public void initialize() {
        labels = FxUtils.getAllNodes(parent, Label.class);
        var image = new Image(JBookFinder.getResource("images/logo.png").toExternalForm());
        logoImg.setImage(image);
        versionLbl.setText("v" + Configs.version);
        updateTheme(Configs.getTheme());
    }


    @FXML
    private void openGithubPage(ActionEvent e) {
        var hyperlink = (Hyperlink) e.getSource();
        Configs.getHostServices().showDocument(hyperlink.getText());
    }




    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void updateTheme(String theme) {
        if (Configs.getTheme().equals("light")) {
            parent.setBackground(Background.fill(Paint.valueOf("#fff")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#222")));
        } else {
            parent.setBackground(Background.fill(Paint.valueOf("#333")));
            labels.forEach(label -> label.setTextFill(Paint.valueOf("#fff")));
        }
        FxUtils.updateButtonTheme(List.of(updateBtn));
    }

    @FXML
    private void checkForUpdates() {
        AppUtils.checkUpdates(true);
    }
}
