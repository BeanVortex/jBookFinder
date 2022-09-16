package ir.darkdeveloper.jbookfinder;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.FxUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class JBookFinderApplication extends Application {

    private final IOUtils ioUtils = IOUtils.getInstance();
    private final BooksRepo booksRepo = BooksRepo.getInstance();
    private final Configs configs = Configs.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ioUtils.readConfig();
        ioUtils.createSaveLocation();
        FxUtils.switchSceneToMain(stage, "main.fxml");
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
        stage.setOnCloseRequest(event -> Platform.exit());
        Platform.runLater(() -> {
            var imagePath = FxUtils.class.getClassLoader().getResource("images/icon.jpg");
            if (imagePath != null) {
                configs.setFxTray(new FXTrayIcon(stage, imagePath));
                var tray = configs.getFxTray();
                tray.show();
                tray.setTrayIconTooltip("JBookFinder");
                tray.addExitItem(true);
            }
        });
        booksRepo.createTable();
        booksRepo.updateBookExistenceRecords();
        configs.setHostServices(getHostServices());
    }


    @Override
    public void stop() {
        System.out.println("stopped");
        Platform.exit();
    }
}
