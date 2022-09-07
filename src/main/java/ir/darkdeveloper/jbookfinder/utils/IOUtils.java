package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {

    private static IOUtils ioUtils;

    private final BookUtils bookUtils = BookUtils.getInstance();
    private final Configs configs = Configs.getInstance();

    private IOUtils() {

    }

    public static IOUtils getInstance() {
        if (ioUtils == null)
            ioUtils = new IOUtils();
        return ioUtils;
    }


    public void deleteCachedImages(List<BookModel> notToDeleteBooks) {
        var path = configs.getBookCoverLocation();

        var filesNotToDelete = new ArrayList<String>();
        if (notToDeleteBooks != null)
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

        bookUtils.showNotification("Cleared cache", "Caches Deleted", "Image caches deleted");
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
            System.out.println("created dir: " + dirPath);
        else
            System.out.println("not created dir: " + dirPath);
    }

    public void saveConfigs() {
        try {
            var file = new File(configs.getConfigLocation() + "config.cfg");
            if (!file.exists())
                file.createNewFile();
            var writer = new FileWriter(file);
            writer.append("save_location=").append(configs.getSaveLocation())
                    .append("\n")
                    .append("theme=").append(configs.getTheme());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readConfig() {
        try {
            var file = new File(configs.getConfigLocation() + "config.cfg");
            if (file.exists()){
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

    // Todo: save and read configs using a file

}
