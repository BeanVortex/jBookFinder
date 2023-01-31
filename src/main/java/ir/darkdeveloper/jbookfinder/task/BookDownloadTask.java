package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDownloadTask extends Task<Void> {


    private final BookModel bookModel;
    private final VBox operationVbox;
    private final String fileName;
    private final BookUtils bookUtils = BookUtils.getInstance();

    private FileChannel fileChannel;
    private static final Logger log = Logger.getLogger(IOUtils.class.getName());


    public BookDownloadTask(BookModel bookModel, VBox operationVbox, String fileName) {
        this.bookModel = bookModel;
        this.operationVbox = operationVbox;
        this.fileName = fileName;
    }

    @Override
    protected Void call() throws Exception {
        var mirror = bookModel.getMirror();

        var urlConnection = (HttpURLConnection) new URL(mirror).openConnection();
        urlConnection.setConnectTimeout(8000);
        var fileSize = urlConnection.getContentLength();
        var filePath = Paths.get(Configs.getSaveLocation() + File.separator + fileName);
        var file = new File(String.valueOf(filePath));
        fileChannel = new FileOutputStream(file, file.exists()).getChannel();
        var in = urlConnection.getInputStream();

        new Thread(() -> {
            try {
                while (fileChannel.isOpen()) {
                    var currentFileSize = Files.size(filePath);
                    updateProgress(currentFileSize, fileSize);
                    Thread.sleep(100);
                }
                if (file.exists()){
                    var currentFileSize = Files.size(Path.of(file.getPath()));
                    if (currentFileSize == fileSize)
                        updateProgress(currentFileSize, fileSize);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        var readableFileChannel = Channels.newChannel(in);
        fileChannel.transferFrom(readableFileChannel, 0, Long.MAX_VALUE);
        fileChannel.close();

        if (!isCancelled()) {
            var imagePath = Configs.getBookCoverLocation() +
                    bookUtils.getImageFileName(bookModel.getImageUrl(), bookModel.getTitle());
            bookModel.setFilePath(filePath.toString());
            bookModel.setImagePath(imagePath);
            BooksRepo.insertBook(bookModel);
        }

        return null;
    }

    @Override
    protected void succeeded() {
        bookUtils.completeDownload(operationVbox, bookModel.getTitle());
    }

    @Override
    protected void failed() {
        var bookTitle = bookModel.getTitle();
        Platform.runLater(() -> Notifications.create()
                .title("Operation failed")
                .text("Downloading book: " + bookTitle.substring(bookTitle.length() / 2) + " has failed")
                .showError());
        operationVbox.getChildren().remove(1);
        operationVbox.getChildren().get(0).setDisable(false);
        operationVbox.getChildren().get(1).setDisable(false);
        deleteOnCancel();
    }

    @Override
    protected void cancelled() {
        deleteOnCancel();
    }

    private void deleteOnCancel() {
        try {
            if (fileChannel != null)
                fileChannel.close();
            var file = new File(Configs.getSaveLocation() + File.separator + fileName);
            if (file.exists())
                Files.delete(Paths.get(file.getPath()));
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }
}
