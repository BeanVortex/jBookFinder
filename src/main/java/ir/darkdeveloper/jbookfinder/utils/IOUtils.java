package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.config.Configs;
import ir.darkdeveloper.jbookfinder.model.BookModel;
import org.apache.commons.io.FileUtils;

import java.io.File;
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
        if (sizeInMB < 1){
            var sizeInKB = (float) sizeInBytes / 1_000;
            return sizeInKB + " KB";
        }else
            return sizeInMB + " MB";
    }


    public void createSaveLocation() {
        var saveLocation = configs.getSaveLocation();
        var file = new File(saveLocation);
        if (file.mkdir())
            System.out.println("created dir");
        else
            System.out.println("not created dir");

        var bookCoverLocation = configs.getBookCoverLocation();
        file = new File(bookCoverLocation);
        if (file.mkdir())
            System.out.println("created book image dir");
        else
            System.out.println("not created book image dir");
    }

}
