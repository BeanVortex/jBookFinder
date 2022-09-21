package ir.darkdeveloper.jbookfinder;

//import com.dustinredmond.fxtrayicon.FXTrayIcon;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class JBookFinder extends Application {

    // Todo: add an option to download with leaving books controller(setting option)
    // Todo: refactorings


    private final IOUtils ioUtils = IOUtils.getInstance();
    private final BooksRepo booksRepo = BooksRepo.getInstance();
    private final Configs configs = Configs.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        ioUtils.readConfig();
        ioUtils.createSaveLocation();
        FxUtils.switchSceneToMain(stage, "main.fxml");
        var logoPath = getResource("images/logo.png");
        if (logoPath != null)
            stage.getIcons().add(new Image(logoPath.toExternalForm()));
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());
        booksRepo.createTable();
        booksRepo.updateBookExistenceRecords();
        ioUtils.moveUnRecordedFiles();
        configs.setHostServices(

                getHostServices());
    }


    @Override
    public void stop() {
        System.out.println("stopped");
        Platform.exit();
//        if (configs.getFxTray() != null)
//            configs.getFxTray().hide();
    }

    public static URL getResource(String path) {
        return JBookFinder.class.getResource(path);
    }

}
