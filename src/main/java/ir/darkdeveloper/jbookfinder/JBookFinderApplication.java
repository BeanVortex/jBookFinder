package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.controllers.SettingsController;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;

public class JBookFinderApplication extends Application {

    private final Configs configs = Configs.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        createSaveLocation();
        SwitchSceneUtil.switchSceneAndGetController(stage, "settings.fxml", SettingsController.class);
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
    }

    private void createSaveLocation() {
        var saveLocation = configs.getSaveLocation();
        var file = new File(saveLocation);
        if (file.mkdir())
            System.out.println("created dir");
        else
            System.out.println("not created dir");

        var bookCoverLocation = configs.getBookCoverLocation();
        file = new File(bookCoverLocation);
        if (file.mkdir())
            System.out.println("created book image dir");
        else
            System.out.println("not created book image dir");
    }

    @Override
    public void stop() {
        System.out.println("stopped");
        Platform.exit();
    }
}
