package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import static ir.darkdeveloper.jbookfinder.config.Configs.downloadRetryCount;
import static ir.darkdeveloper.jbookfinder.config.Configs.userAgent;


public class DownloadInChunksTask extends DownloadTask {
    private final int chunks;

    private final List<FileChannel> fileChannels = new ArrayList<>();
    private final List<Path> filePaths = new ArrayList<>();
    private ExecutorService executor;
    private long fileSize;
    private volatile boolean paused;
    private volatile boolean isCalculating;
    private Path filePath;
    private final BookUtils bookUtils = BookUtils.getInstance();
    private static final Logger log = Logger.getLogger(IOUtils.class.getName());

    public DownloadInChunksTask(BookModel bm, String fileName) {
        super(bm, fileName);
        this.chunks = IOUtils.maxChunks();
    }


    @Override
    protected Long call() {
        filePath = Paths.get(Configs.getSaveLocation() + File.separator + fileName);
        var file = filePath.toFile();
        bm.setFilePath(filePath.toString());
        fileSize = bm.getFileSize();
        if (file.exists() && isCompleted(file))
            return fileSize;
        try {
            downloadInChunks();
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
        return fileSize;
    }


    private boolean isCompleted(File file) {
        try {
            var existingFileSize = IOUtils.getFileSize(file);
            if (fileSize != 0 && existingFileSize == fileSize) {
                return true;
            }
        } catch (Exception e) {
            log.severe(e.getLocalizedMessage());
        }
        return false;
    }


    private void downloadInChunks() throws IOException, ExecutionException, InterruptedException {
        calculateSpeedAndProgressChunks(fileSize);
        var futures = prepareParts(new URL(bm.getMirror()), fileSize);
        if (!futures.isEmpty()) {
            isCalculating = true;
            log.info("Downloading : " + bm);
            var futureArr = new CompletableFuture[futures.size()];
            futures.toArray(futureArr);
            CompletableFuture.allOf(futureArr).get();
        }
    }


    private List<CompletableFuture<Void>> prepareParts(URL url, long fileSize) throws IOException {
        var bytesForEach = fileSize / chunks;
        var futures = new ArrayList<CompletableFuture<Void>>();
        var to = bytesForEach;
        var from = 0L;
        var fromContinue = 0L;
        var tempFolderPath = filePath.getParent().toString() + File.separator + ".temp" + File.separator;
        if (!Files.exists(Path.of(tempFolderPath)))
            new File(tempFolderPath).mkdir();
        var lastPartSize = fileSize - ((chunks - 1) * bytesForEach);
        for (int i = 0; i < chunks; i++, from = to, to += bytesForEach) {
            var filePath = tempFolderPath + fileName + "#" + i;
            filePaths.add(Paths.get(filePath));
            var partFile = new File(filePath);
            var existingFileSize = 0L;
            if (!partFile.exists())
                partFile.createNewFile();
            else {
                existingFileSize = IOUtils.getFileSize(partFile);
                if (i + 1 != chunks && existingFileSize == bytesForEach)
                    continue;

                if (i + 1 == chunks && existingFileSize == lastPartSize)
                    continue;
            }

            fromContinue = from + existingFileSize;

            if (i + 1 == chunks && to != fileSize) {
                to = fileSize;
                if (fromContinue == to)
                    break;
            }

            var finalTo = to - 1;
            addFutures(url, existingFileSize, futures, partFile, fromContinue, from, finalTo);
        }
        return futures;
    }

    private void addFutures(URL url, long existingFileSize, ArrayList<CompletableFuture<Void>> futures,
                            File partFile, long fromContinue, long from, long to) {
        CompletableFuture<Void> c = CompletableFuture.supplyAsync(() -> {
            try {
                performDownload(url, fromContinue, from, to, partFile, existingFileSize, 0);
            } catch (IOException | InterruptedException e) {
                if (e instanceof IOException)
                    log.severe(e.getMessage());
                this.pause();
            }
            return null;
        }, executor);
        c.whenComplete((unused, throwable) -> Thread.currentThread().interrupt());
        futures.add(c);
    }


    private void performDownload(URL url, long fromContinue, long from, long to, File partFile,
                                 long existingFileSize, int retries)
            throws InterruptedException, IOException {
        if (retries != downloadRetryCount) {
            try {
                var con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(3000);
                con.setConnectTimeout(3000);
                con.addRequestProperty("Range", "bytes=" + fromContinue + "-" + to);
                if (!bm.isResumable())
                    con.setRequestProperty("User-Agent", userAgent);
                var out = new FileOutputStream(partFile, partFile.exists());
                var fileChannel = out.getChannel();
                fileChannels.add(fileChannel);
                var byteChannel = Channels.newChannel(con.getInputStream());
                fileChannel.transferFrom(byteChannel, existingFileSize, to - fromContinue + 1);
                fileChannels.remove(fileChannel);
                fileChannel.close();
                con.disconnect();
            } catch (SocketTimeoutException | UnknownHostException | SocketException s) {
                if (!paused) {
                    retries++;
                    log.warning("Downloading part " + partFile.getName() + " failed. retry count : " + retries);
                    Thread.sleep(2000);
                    var currFileSize = IOUtils.getFileSize(partFile);
                    performDownload(url, from + currFileSize, from, to, partFile, currFileSize, retries);
                }
            } catch (ClosedChannelException ignore) {
            }

            // when connection has been closed by the server
            var currFileSize = IOUtils.getFileSize(partFile);
            if (!paused && currFileSize != (to - from + 1))
                performDownload(url, from + currFileSize, from, to, partFile, currFileSize, retries);
        }
    }


    private void calculateSpeedAndProgressChunks(long fileSize) {
        executor.submit(() -> {
            Thread.currentThread().setName("calculator: " + Thread.currentThread().getName());
            try {
                while (!isCalculating) Thread.onSpinWait();
                Thread.sleep(1000);
                while (!paused) {
                    var currentFileSize = 0L;
                    for (int i = 0; i < chunks; i++)
                        currentFileSize += Files.size(filePaths.get(i));
                    updateProgress(currentFileSize, fileSize);
                    updateValue(currentFileSize);

                    Thread.sleep(1000);
                }
            } catch (InterruptedException | NoSuchFileException ignore) {
            } catch (IOException e) {
                log.severe(e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void pause() {
        paused = true;
        log.info("Paused download: " + bm);
        try {
            //this will cause execution get out of transferFrom
            for (var channel : fileChannels)
                channel.close();
        } catch (IOException e) {
            log.severe(e.getLocalizedMessage());
        }
    }


    @Override
    protected void failed() {
        log.info("Failed download: " + bm);
        pause();
    }

    @Override
    protected void succeeded() {
        try {
            for (var channel : fileChannels)
                channel.close();
            if (IOUtils.mergeFiles(fileSize, chunks, fileName, filePaths)) {
                log.info("Book successfully downloaded: " + bm);
                var imagePath = Configs.getBookCoverLocation() +
                        bookUtils.getImageFileName(bm.getImageUrl(), bm.getTitle());
                bm.setFilePath(filePath.toString());
                bm.setImagePath(imagePath);
                BooksRepo.insertBook(bm);
                updateProgress(1, 1);
            }


            if (executor != null)
                executor.shutdownNow();
            System.gc();
        } catch (IOException e) {
            log.severe(e.getLocalizedMessage());
        }
    }

    @Override
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

}
