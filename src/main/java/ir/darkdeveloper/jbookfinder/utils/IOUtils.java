package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.model.BookModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {

    private static final BookUtils bookUtils = new BookUtils();

    public void deleteCachedImages(List<BookModel> notToDeleteBooks) {
        var path = bookUtils.getSaveLocation() + "bookImages/";

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

}
