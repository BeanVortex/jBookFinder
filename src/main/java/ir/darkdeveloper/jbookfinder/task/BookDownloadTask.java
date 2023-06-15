package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;
import javafx.application.Platform;
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
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDownloadTask extends DownloadTask {


    private final VBox operationVbox;
    private final String fileName;
    private final BookUtils bookUtils = BookUtils.getInstance();

    private FileChannel fileChannel;
    private ExecutorService executor;
    private volatile boolean paused;
    private Path filePath;
    private long fileSize;

    private static final Logger log = Logger.getLogger(IOUtils.class.getName());


    public BookDownloadTask(BookModel bm, VBox operationVbox, String fileName) {
        super(bm, fileName);
        this.operationVbox = operationVbox;
        this.fileName = fileName;
    }

    @Override
    protected Long call() throws Exception {
        var mirror = bm.getMirror();

        var urlConnection = (HttpURLConnection) new URL(mirror).openConnection();
        urlConnection.setConnectTimeout(8000);
        fileSize = urlConnection.getContentLength();
        filePath = Paths.get(Configs.getSaveLocation() + File.separator + fileName);
        var file = new File(String.valueOf(filePath));
        var out = new FileOutputStream(file, file.exists());
        fileChannel = out.getChannel();
        var in = urlConnection.getInputStream();

        initProgress();

        var readableFileChannel = Channels.newChannel(in);
        fileChannel.transferFrom(readableFileChannel, 0, Long.MAX_VALUE);
        out.close();
        fileChannel.close();
        return fileSize;
    }

    @Override
    protected void succeeded() {
        if (!isCancelled()) {
            var imagePath = Configs.getBookCoverLocation() +
                    bookUtils.getImageFileName(bm.getImageUrl(), bm.getTitle());
            bm.setFilePath(filePath.toString());
            bm.setImagePath(imagePath);
            BooksRepo.insertBook(bm);
        }
        executor.shutdownNow();
    }

    @Override
    protected void failed() {
        paused = true;
        var bookTitle = bm.getTitle();
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
            var file = new File(filePath.toString());
            if (file.exists())
                Files.delete(Paths.get(file.getPath()));
        } catch (IOException e) {
            log.log(Level.WARNING, e.getMessage());
        }
    }

    private void initProgress() {
        executor.submit(() -> {
            Thread.currentThread().setName("calculator: " + Thread.currentThread().getName());
            try {
                while (!paused) {
                    var currentFileSize = Files.size(filePath);
                    updateProgress(currentFileSize, fileSize);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException | NoSuchFileException ignore) {
            } catch (IOException e) {
                log.severe(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void pause() {
       failed();
    }
}
