package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BookUtils {


    public void downloadBookAndAddProgress(BookModel bookModel, VBox operationVbox) {

        var downTask = downloadBook(bookModel, operationVbox);

        addProgress(operationVbox, downTask);


    }

    private void addProgress(VBox operationVbox, DownloadTask downTask) {
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


        progressBar.progressProperty().bind(downTask.progressProperty());
        progressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
            var percentage = (int) (newValue.doubleValue() * 100);
            progressLabel.setText(percentage + " %");
        });
    }

    private DownloadTask downloadBook(BookModel bookModel, VBox operationVbox) {
        var downTask = new DownloadTask(bookModel, operationVbox);
        var t1 = new Thread(downTask);
        t1.setDaemon(true);
        t1.start();
        return downTask;
    }

    // Todo: stop thread when scene changed
    private static class DownloadTask extends Task<Void> {

        private final BookModel bookModel;
        private final VBox operationVbox;
        private String SAVE_LOCATION = "";

        public DownloadTask(BookModel bookModel, VBox operationVbox) {
            this.bookModel = bookModel;
            this.operationVbox = operationVbox;
        }

        @Override
        protected Void call() throws Exception {
            var mirror = bookModel.getMirror();
            var fileName = bookModel.getTitle().replaceAll("[^A-Za-z0-9()\\[\\]]", "_");
            var fileExt = bookModel.getFileFormat();

            var urlConnection = new URL(mirror).openConnection();
            var fileSize = urlConnection.getContentLength();

            try (var is = urlConnection.getInputStream();
                 var os = Files.newOutputStream(Paths.get(SAVE_LOCATION + fileName + "." + fileExt))) {
                long nRead = 0L;
                var buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) > 0) {
                    os.write(buf, 0, n);
                    nRead += n;
                    updateProgress(nRead, fileSize);
                }
            } catch (IOException e) {
                e.printStackTrace();
                failed();
                return null;
            }
            return null;
        }

        @Override
        protected void succeeded() {
            // Todo: save book data in database
            operationVbox.getChildren().remove(1);
            operationVbox.getChildren().get(0).setDisable(false);
            operationVbox.getChildren().get(1).setDisable(false);
            var downloadBtn = (Button) operationVbox.getChildren().get(0);
            downloadBtn.setText("Open Book");
        }

        @Override
        protected void failed() {
            operationVbox.getChildren().remove(1);
            operationVbox.getChildren().get(0).setDisable(false);
            operationVbox.getChildren().get(1).setDisable(false);
            var fileName = bookModel.getTitle().replace(' ', '_');
            var fileExt = bookModel.getFileFormat();

            try {
                var file = new File(SAVE_LOCATION + "/" + fileName + "." + fileExt);
                if (file.exists())
                    Files.delete(Paths.get(file.getPath()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}

