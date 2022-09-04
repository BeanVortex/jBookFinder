package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ImageFetchTask extends Task<File> {

    private final String imageUrl;
    private final ImageView bookImage;
    private final String fileName;
    private static final String SAVE_LOCATION = "/bookImages/";

    private static final BookUtils bookUtils = new BookUtils();
    private File file;

    public ImageFetchTask(String imageUrl, String fileName, ImageView bookImage) {
        this.imageUrl = imageUrl;
        this.bookImage = bookImage;
        this.fileName = fileName;
    }

    @Override
    protected File call() throws Exception {
        if (imageUrl == null)
            return null;
        var imageFile = new File(bookUtils.getSaveLocation() + SAVE_LOCATION + fileName);
        if (imageFile.exists()){
            file = imageFile;
            return imageFile;
        }
        FileUtils.copyURLToFile(
                new URL(imageUrl),
                imageFile
        );
        file = imageFile;
        return imageFile;
    }

    @Override
    protected void succeeded() {
        try {
            var finalFile = file;
            if (file == null)
                finalFile = new File("src/main/resources/images/blank.png");
            var inputStream = new FileInputStream(finalFile);
            var image = new Image(inputStream);
            bookImage.setImage(image);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
