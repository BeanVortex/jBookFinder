package ir.darkdeveloper.jbookfinder;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class JBookFinderApplication extends Application {

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
        var logoPath = FxUtils.class.getClassLoader().getResource("images/logo.png");
        stage.setMinWidth(850);
        if (logoPath != null) {
            stage.getIcons().add(new Image(logoPath.toExternalForm()));
            Platform.runLater(() -> {
                if (configs.getFxTray() == null) {
                    configs.setFxTray(new FXTrayIcon(stage, logoPath));
                    var tray = configs.getFxTray();
                    tray.show();
                    tray.setTrayIconTooltip("JBookFinder");
                    tray.addExitItem("Exit App", e -> {
                        Platform.exit();
                        tray.hide();
                    });
                }
            });
        }
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());
        booksRepo.createTable();
        booksRepo.updateBookExistenceRecords();
        ioUtils.moveUnRecordedFiles();
        configs.setHostServices(getHostServices());
    }


    @Override
    public void stop() {
        System.out.println("stopped");
        Platform.exit();
        if (configs.getFxTray() != null)
            configs.getFxTray().hide();
    }
}
