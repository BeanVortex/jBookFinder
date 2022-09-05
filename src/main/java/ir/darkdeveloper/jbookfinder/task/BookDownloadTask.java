package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BookDownloadTask extends Task<Void> {

    // Todo: stop thread when scene changed

    private final BookModel bookModel;
    private final VBox operationVbox;
    private final String fileName;
    private static final BookUtils bookUtils = new BookUtils();
    private final Configs configs = Configs.getInstance();

    public BookDownloadTask(BookModel bookModel, VBox operationVbox, String fileName) {
        this.bookModel = bookModel;
        this.operationVbox = operationVbox;
        this.fileName = fileName;
    }

    @Override
    protected Void call() throws Exception {
        var mirror = bookModel.getMirror();

        var urlConnection = new URL(mirror).openConnection();
        var fileSize = urlConnection.getContentLength();
        try (var is = urlConnection.getInputStream();
             var os = Files.newOutputStream(Paths.get(configs.getSaveLocation() + fileName))) {
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
        bookUtils.completeDownload(operationVbox);
    }

    @Override
    protected void failed() {
        operationVbox.getChildren().remove(1);
        operationVbox.getChildren().get(0).setDisable(false);
        operationVbox.getChildren().get(1).setDisable(false);
        var fileName = bookModel.getTitle().replace(' ', '_');
        var fileExt = bookModel.getFileFormat();

        try {
            var file = new File(configs.getSaveLocation() + "/" + fileName + "." + fileExt);
            if (file.exists())
                Files.delete(Paths.get(file.getPath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
