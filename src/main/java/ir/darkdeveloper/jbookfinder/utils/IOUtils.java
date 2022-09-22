package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import org.apache.commons.io.FileUtils;
import org.controlsfx.control.Notifications;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static ir.darkdeveloper.jbookfinder.config.Configs.*;

public class IOUtils {

    private static final BookUtils bookUtils = BookUtils.getInstance();
    private static final Logger log = Logger.getLogger(IOUtils.class.getName());



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

    /**
     * @param savePath new location for saving books, pass null for other setting updates
     * */
    public static void saveConfigs(String savePath) {
        try {
            var prevSaveLocation = getSaveLocation();
            var prevBookCoverLocation = getBookCoverLocation();
            var prevUnrecordedLocation = getUnrecordedLocation();
            if (savePath != null)
                setSaveLocation(savePath);

            var file = new File(getConfigLocation() + "config.cfg");
            if (!file.exists())
                file.createNewFile();

            var writer = new FileWriter(file);
            writer.append("save_location=").append(getSaveLocation())
                    .append("\n")
                    .append("theme=").append(getTheme())
                    .append("\n")
                    .append("background_download=").append(String.valueOf(isBackgroundDownload()));
            writer.flush();
            writer.close();
            moveAndDeletePreviousData(prevBookCoverLocation, getBookCoverLocation());
            moveAndDeletePreviousData(prevUnrecordedLocation, getUnrecordedLocation());
            moveAndDeletePreviousData(prevSaveLocation, getSaveLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static void moveAndDeletePreviousData(String prevSaveLocation, String nextSaveLocation) {
        if (prevSaveLocation.equals(nextSaveLocation))
            return;
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

}
