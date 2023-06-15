package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import org.apache.commons.io.FileUtils;
import org.controlsfx.control.Notifications;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static ir.darkdeveloper.jbookfinder.config.Configs.*;

public class IOUtils {

    private static final BookUtils bookUtils = BookUtils.getInstance();
    private static final Logger log = Logger.getLogger(IOUtils.class.getName());


    public static int maxChunks() {
        return Runtime.getRuntime().availableProcessors() * 2;
    }

    public static void deleteCachedImages(List<BookModel> notToDeleteBooks) {
        var path = getBookCoverLocation();
        if (notToDeleteBooks == null)
            notToDeleteBooks = new ArrayList<>();
        var fetchedBooks = BooksRepo.getBooks();
        if (fetchedBooks != null)
            notToDeleteBooks.addAll(fetchedBooks);


        var filesNotToDelete = new ArrayList<String>();
        notToDeleteBooks.forEach(book -> {
            var imageFileName = bookUtils.getImageFileName(book.getImageUrl(), book.getTitle());
            filesNotToDelete.add(imageFileName);
        });

        var dir = new File(path);
        var files = dir.listFiles();
        if (files != null)
            for (var file : files)
                if (!file.isDirectory() && !filesNotToDelete.contains(file.getName()))
                    file.delete();

        Notifications.create()
                .title("Operation complete")
                .text("Image caches deleted")
                .showInformation();
    }


    public static String getFolderSize(File folder) {
        var sizeInBytes = FileUtils.sizeOfDirectory(folder);
        var sizeInMB = (float) sizeInBytes / 1_000_000;
        if (sizeInMB < 1) {
            var sizeInKB = (float) sizeInBytes / 1_000;
            return sizeInKB + " KB";
        } else
            return sizeInMB + " MB";
    }


    public static void createSaveLocation() {
        var saveLocation = getSaveLocation();
        var bookCoverLocation = getBookCoverLocation();
        var configLocation = getConfigLocation();
        mkdir(saveLocation);
        mkdir(bookCoverLocation);
        mkdir(configLocation);
    }

    private static void mkdir(String dirPath) {
        var file = new File(dirPath);
        if (file.mkdir())
            log.info("created dir: " + dirPath);
        else
            log.info("not created dir: " + dirPath);
    }

    public static void saveConfigs() {
        try {
            var file = new File(getConfigLocation() + "config.cfg");
            if (!file.exists())
                file.createNewFile();

            var writer = new FileWriter(file);
            writer.append("save_location=").append(getSaveLocation())
                    .append("\n")
                    .append("theme=").append(getTheme())
                    .append("\n")
                    .append("background_download=").append(String.valueOf(isBackgroundDownload()))
                    .append("\n")
                    .append("result_count=").append(getResultCount())
                    .append("\n")
                    .append("filter_result=").append(getFilterResult());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getFileSize(File file) throws IOException {
        return getFileSize(Path.of(file.getPath()));
    }

    public static long getFileSize(Path path) throws IOException {
        return Files.size(path);
    }

    public static void readConfig() {
        try {
            var file = new File(getConfigLocation() + "config.cfg");
            if (file.exists()) {
                var reader = new BufferedReader(new FileReader(file));
                String cfg;
                while ((cfg = reader.readLine()) != null) {
                    var key = cfg.split("=")[0];
                    var value = cfg.split("=")[1];
                    switch (key) {
                        case "save_location" -> setSaveLocation(value);
                        case "theme" -> setTheme(value);
                        case "background_download" -> setBackgroundDownload(Boolean.parseBoolean(value));
                        case "result_count" -> setResultCount(value);
                        case "filter_result" -> setFilterResult(value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void moveUnRecordedFiles() throws IOException {
        var saveDir = new File(getSaveLocation());
        var files = saveDir.listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isFile()) {
                    var fileName = file.getName();
                    var title = fileName.substring(0, fileName.lastIndexOf('.'));
                    if (!BooksRepo.doesBookExist(title)) {
                        var unrecordedDir = new File(getSaveLocation() + File.separator + getUnRecordedDirName());
                        unrecordedDir.mkdir();
                        var destFile = new File(unrecordedDir.getPath() + File.separator + file.getName());
                        if (!file.renameTo(destFile))
                            if (destFile.exists())
                                Files.delete(file.toPath());
                    }
                }
            }
        }
    }


    public static void moveAndDeletePreviousData(String savePath) {
        if (savePath != null) {
            var prevSaveLocation = getSaveLocation();
            var prevBookCoverLocation = getBookCoverLocation();
            var prevUnrecordedLocation = getUnrecordedLocation();
            setSaveLocation(savePath);
            moveAndDeletePreviousData(prevBookCoverLocation, getBookCoverLocation());
            moveAndDeletePreviousData(prevUnrecordedLocation, getUnrecordedLocation());
            moveAndDeletePreviousData(prevSaveLocation, getSaveLocation());
        }
    }


    private static void moveAndDeletePreviousData(String prevSaveLocation, String nextSaveLocation) {
        var nextDir = new File(nextSaveLocation);
        if (!nextDir.exists())
            nextDir.mkdir();
        var prevDir = new File(prevSaveLocation);
        var files = prevDir.listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isFile())
                    file.renameTo(new File(nextSaveLocation + File.separator + file.getName()));

            }
        }
        prevDir.delete();
    }

    public static HttpURLConnection connect(String uri, int connectTimeout, int readTimeout) {
        try {
            if (uri.isBlank())
                throw new IllegalArgumentException("URL is blank");
            var url = new URL(uri);
            var conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestProperty("User-Agent", userAgent);
            return conn;
        } catch (IOException e) {
            var msg = "Connection or read timeout. Connect to the internet or check the url";
            log.severe(msg);
            Notifications.create()
                    .title("Bad Connection")
                    .text(msg)
                    .showError();
            throw new RuntimeException(msg);
        }
    }

    public static boolean canResume(HttpURLConnection connection) {
        var rangeSupport = connection.getHeaderField("Accept-Ranges");
        return rangeSupport != null && !rangeSupport.equals("none");
    }

    public static boolean mergeFiles(long fileSize, int chunks, String fileName, List<Path> filePaths) throws IOException {
        var currentFileSize = 0L;
        for (int i = 0; i < chunks; i++)
            currentFileSize += Files.size(filePaths.get(i));
        if (filePaths.stream().allMatch(path -> path.toFile().exists())
                && currentFileSize == fileSize) {
            var firstFile = filePaths.get(0).toFile();
            for (int i = 1; i < chunks; i++) {
                var nextFile = filePaths.get(i).toFile();
                try (
                        var in = new FileInputStream(nextFile);
                        var out = new FileOutputStream(firstFile, firstFile.exists());
                        var inputChannel = in.getChannel();
                        var outputChannel = out.getChannel()
                ) {
                    var buffer = ByteBuffer.allocateDirect(1048576);
                    while (inputChannel.read(buffer) != -1) {
                        buffer.flip();
                        outputChannel.write(buffer);
                        buffer.clear();
                    }
                }
                if (nextFile.exists())
                    nextFile.delete();
            }
            var pathToMove = filePaths.get(0).getParent().getParent() + File.separator + fileName;
            return firstFile.renameTo(new File(pathToMove));
        }
        return false;
    }


}
