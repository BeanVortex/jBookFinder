package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.JBookFinder;
import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.controllers.AboutController;
import ir.darkdeveloper.jbookfinder.task.UpdateCheckTask;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

public class AppUtils {

    public static void showAbout() {
        var controller = (AboutController) FxUtils
                .newStageAndReturnController("about.fxml", "About", 450, 350);
        if (controller != null)
            Configs.getThemeSubject().addObserver(controller);
    }

    public static void checkUpdates(boolean fromAbout) {
        var updateChecker = new UpdateCheckTask();
        var thread = new Thread(updateChecker);
        thread.setDaemon(true);
        thread.start();

        updateChecker.valueProperty().addListener((obs, old, newVal) -> {
            var version = newVal.split(",")[0];
            var description = newVal.split(",")[1];
            if (!Configs.version.equals(version)) {
                var alert = new Alert(Alert.AlertType.INFORMATION,
                        "", ButtonType.YES, ButtonType.NO);
                var alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                var logoPath = JBookFinder.getResource("images/logo.png");
                if (logoPath != null)
                    alertStage.getIcons().add(new Image(logoPath.toExternalForm()));
                alert.setTitle("Update Notifier");
                alert.setHeaderText("New update available: " + version);
                alert.setContentText(description + "\nWould you like to download the new update?");
                var buttonType = alert.showAndWait();
                buttonType.ifPresent(type -> {
                    if (type == ButtonType.YES)
                        Configs.getHostServices()
                                .showDocument("https://github.com/DarkDeveloper-arch/jBookFinder/releases/tag/v" + version);
                    else if (type == ButtonType.NO)
                        alert.close();
                });
            }else {
                if (fromAbout)
                    Notifications.create()
                            .title("Checked for updates")
                            .text("No updates available")
                            .showInformation();
            }
        });
    }

}
