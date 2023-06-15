package ir.darkdeveloper.jbookfinder.task;

import ir.darkdeveloper.jbookfinder.model.BookModel;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;

public abstract class DownloadTask extends Task<Long> {
    protected BookModel bm;
    protected String fileName;

    public DownloadTask(BookModel bm, String fileName) {
        this.bm = bm;
        this.fileName = fileName;
    }

    public abstract void setExecutor(ExecutorService executor);

    public abstract boolean isPaused();
    public abstract void pause();

}
