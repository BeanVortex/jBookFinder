package ir.darkdeveloper.jbookfinder.utils;

import ir.darkdeveloper.jbookfinder.model.BookModel;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil.getResource;

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


        var tray = SystemTray.getSystemTray();

        var trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage(getResource("images/blank.png")), "Cleared cache");
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        trayIcon.displayMessage("Caches Deleted", "Image caches deleted", TrayIcon.MessageType.INFO);
        trayIcon.addActionListener(e -> tray.remove(trayIcon));
    }

}
