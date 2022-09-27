package ir.darkdeveloper.jbookfinder.task;

import javafx.concurrent.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class UpdateCheckTask extends Task<String> {
    @Override
    protected String call() throws Exception {

        Document doc = null;
        for (int i = 0; i < 3; i++) {
            try {
                doc = Jsoup.connect("https://github.com/DarkDeveloper-arch/jBookFinder/releases")
                        .userAgent("Mozilla")
                        .get();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                Thread.sleep(2000);
            }
        }
        if (doc == null)
            throw new RuntimeException("Not Found");

        var updateBox = doc.select(".Box-body").get(0);
        var updateVersion = updateBox.select("div").get(0).select("span").get(0).text().substring(1);
        var updateDescription = updateBox.select("div.markdown-body").text();


        return updateVersion + "," + updateDescription;
    }
}
