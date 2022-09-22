package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BookDownloadTask extends Task<Void> {


    private final BookModel bookModel;
    private final VBox operationVbox;
    private final String fileName;
    private final BookUtils bookUtils = BookUtils.getInstance();


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
        var filePath = Paths.get(Configs.getSaveLocation() + File.separator + fileName);
        try (var is = urlConnection.getInputStream();
             var os = Files.newOutputStream(filePath)) {
            long nRead = 0L;
            var buf = new byte[8192];
            int n;
            while ((n = is.read(buf)) > 0) {
                if (isCancelled())
                    break;
                os.write(buf, 0, n);
                nRead += n;
                updateProgress(nRead, fileSize);
            }

            if (isCancelled())
                Files.delete(filePath);
            else {
                var imagePath = Configs.getBookCoverLocation() +
                        bookUtils.getImageFileName(bookModel.getImageUrl(), bookModel.getTitle());
                bookModel.setFilePath(filePath.toString());
                bookModel.setImagePath(imagePath);
                BooksRepo.insertBook(bookModel);
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
        bookUtils.completeDownload(operationVbox, bookModel.getTitle());
    }

    @Override
    protected void failed() {
        operationVbox.getChildren().remove(1);
        operationVbox.getChildren().get(0).setDisable(false);
        operationVbox.getChildren().get(1).setDisable(false);
        var fileName = bookModel.getTitle().replace(' ', '_');
        var fileExt = bookModel.getFileFormat();

        try {
            var file = new File(Configs.getSaveLocation() + File.separator + fileName + "." + fileExt);
            if (file.exists())
                Files.delete(Paths.get(file.getPath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
