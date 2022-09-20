package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import ir.darkdeveloper.jbookfinder.repo.BooksRepo;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IOUtils {

    private static IOUtils ioUtils;

    private final BookUtils bookUtils = BookUtils.getInstance();
    private final Configs configs = Configs.getInstance();
    private final BooksRepo repo = BooksRepo.getInstance();
    private static final Logger log = Logger.getLogger(IOUtils.class.getName());

    private IOUtils() {

    }

    public static IOUtils getInstance() {
        if (ioUtils == null)
            ioUtils = new IOUtils();
        return ioUtils;
    }


    public void deleteCachedImages(List<BookModel> notToDeleteBooks) {
        var path = configs.getBookCoverLocation();
        if (notToDeleteBooks == null)
            notToDeleteBooks = new ArrayList<>();
        notToDeleteBooks.addAll(repo.getBooks());

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

//        configs.getFxTray().showInfoMessage("Image caches deleted");
    }


    public String getFolderSize(File folder) {
        var sizeInBytes = FileUtils.sizeOfDirectory(folder);
        var sizeInMB = (float) sizeInBytes / 1_000_000;
        if (sizeInMB < 1) {
            var sizeInKB = (float) sizeInBytes / 1_000;
            return sizeInKB + " KB";
        } else
            return sizeInMB + " MB";
    }


    public void createSaveLocation() {
        var saveLocation = configs.getSaveLocation();
        var bookCoverLocation = configs.getBookCoverLocation();
        var configLocation = configs.getConfigLocation();
        mkdir(saveLocation);
        mkdir(bookCoverLocation);
        mkdir(configLocation);
    }

    private void mkdir(String dirPath) {
        var file = new File(dirPath);
        if (file.mkdir())
            log.info("created dir: " + dirPath);
        else
            log.info("not created dir: " + dirPath);
    }

    public void saveConfigs(String savePath) {
        try {
            var prevSaveLocation = configs.getSaveLocation();
            var prevBookCoverLocation = configs.getBookCoverLocation();
            if (savePath != null)
                configs.setSaveLocation(savePath);

            var file = new File(configs.getConfigLocation() + "config.cfg");
            if (!file.exists())
                file.createNewFile();

            var writer = new FileWriter(file);
            writer.append("save_location=").append(configs.getSaveLocation())
                    .append("\n")
                    .append("theme=").append(configs.getTheme());
            writer.flush();
            writer.close();
            moveAndDeletePreviousData(prevBookCoverLocation, configs.getBookCoverLocation());
            moveAndDeletePreviousData(prevSaveLocation, configs.getSaveLocation());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readConfig() {
        try {
            var file = new File(configs.getConfigLocation() + "config.cfg");
            if (file.exists()) {
                var reader = new BufferedReader(new FileReader(file));
                String cfg;
                while ((cfg = reader.readLine()) != null) {
                    var key = cfg.split("=")[0];
                    var value = cfg.split("=")[1];
                    switch (key) {
                        case "save_location" -> configs.setSaveLocation(value);
                        case "theme" -> configs.setTheme(value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveUnRecordedFiles() throws IOException {
        var saveDir = new File(configs.getSaveLocation());
        var files = saveDir.listFiles();
        if (files != null) {
            for (var file : files) {
                if (file.isFile()) {
                    var fileName = file.getName();
                    var title = fileName.substring(0, fileName.lastIndexOf('.'));
                    if (!repo.doesBookExist(title)) {
                        var unrecordedDir = new File(configs.getSaveLocation() + File.separator + "unrecorded_books");
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

    private void moveAndDeletePreviousData(String prevSaveLocation, String nextSaveLocation) {
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
