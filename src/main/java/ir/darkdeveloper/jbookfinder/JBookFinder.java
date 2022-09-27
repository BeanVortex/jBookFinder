package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.AppUtils;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class JBookFinder extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        IOUtils.readConfig();
        IOUtils.createSaveLocation();
        FxUtils.switchSceneToMain(stage, "main.fxml");
        var logoPath = getResource("images/logo.png");
        if (logoPath != null)
            stage.getIcons().add(new Image(logoPath.toExternalForm()));
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());
        BooksRepo.createTable();
        BooksRepo.updateBookExistenceRecords();
        IOUtils.moveUnRecordedFiles();
        Configs.setHostServices(getHostServices());
        AppUtils.checkUpdates(false);
    }


    @Override
    public void stop() {
        System.out.println("stopped");
        Platform.exit();
    }

    public static URL getResource(String path) {
        return JBookFinder.class.getResource(path);
    }


}
