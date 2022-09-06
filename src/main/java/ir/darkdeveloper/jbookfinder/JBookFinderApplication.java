package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.controllers.SettingsController;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JBookFinderApplication extends Application {

    private final IOUtils ioUtils = IOUtils.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ioUtils.createSaveLocation();
        SwitchSceneUtil.switchSceneAndGetController(stage, "settings.fxml", SettingsController.class);
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
    }


    @Override
    public void stop() {
        System.out.println("stopped");
        Platform.exit();
    }
}
