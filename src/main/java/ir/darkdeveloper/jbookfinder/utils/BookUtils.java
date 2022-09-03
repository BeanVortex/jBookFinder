package ir.darkdeveloper.jbookfinder.utils;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class BookUtils {


    public void downloadBookAndAddProgress(String mirror, VBox operationVbox) {
        var progressBox = new HBox();
        var progressLabel = new Label();
        var progressBar = new ProgressBar();
        progressBox.setSpacing(8);
        progressLabel.setText("0 %");
        progressBox.getChildren().addAll(progressBar, progressLabel);
        progressBox.setAlignment(Pos.CENTER);

        operationVbox.getChildren().add(1, progressBox);
        operationVbox.getChildren().get(0).setDisable(true);
        operationVbox.getChildren().get(2).setDisable(true);

        var downTask = new DownloadTask(mirror, operationVbox);

        progressBar.progressProperty().bind(downTask.progressProperty());
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            var percentage = (int) (newValue.doubleValue() * 100);
            progressLabel.setText(percentage + " %");
        });

        var t1 = new Thread(downTask);
        t1.setDaemon(true);
        t1.start();

    }

    private static class DownloadTask extends Task<Void> {

        private final String mirror;
        private final VBox operationVbox;

        public DownloadTask(String mirror, VBox operationVbox) {
            this.mirror = mirror;
            this.operationVbox = operationVbox;
        }

        @Override
        protected Void call() throws Exception {
            var urlConnection = new URL(mirror).openConnection();
            var fileSize = urlConnection.getContentLength();

            try (var is = urlConnection.getInputStream();
                 var os = Files.newOutputStream(Paths.get(UUID.randomUUID() + ".pdf"))) {
                long nRead = 0L;
                var buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) > 0) {
                    os.write(buf, 0, n);
                    nRead += n;
                    updateProgress(nRead, fileSize);
                }
            }
            return null;
        }

        @Override
        protected void succeeded() {
            operationVbox.getChildren().remove(1);
            operationVbox.getChildren().get(0).setDisable(false);
            operationVbox.getChildren().get(1).setDisable(false);
            var downloadBtn = (Button) operationVbox.getChildren().get(0);
            downloadBtn.setText("Open Book");
        }
    }


}

