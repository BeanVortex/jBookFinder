package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.controllers.MainController;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
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
        ioUtils.readConfig();
        FxUtils.switchSceneToMain(stage, "main.fxml");
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());
    }


    @Override
    public void stop() {
        System.out.println("stopped");
        Platform.exit();
    }
}
