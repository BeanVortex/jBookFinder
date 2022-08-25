package ir.darkdeveloper.jbookfinder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ScraperService {

    public CompletableFuture<List<BookModel>> fetchBookModels(String bookName, Integer page) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        // retry
                        Document doc = null;
                        for (int i = 0; i < 3; i++) {
                            try {
                                doc = Jsoup.connect("https://libgen.rs/search.php")
                                        .data("req", bookName)
                                        .data("page", page.toString())
                                        .userAgent("Mozilla")
                                        .get();
                            }catch (IOException e){
                                // delay request
                                Thread.sleep(3000);
                            }
                        }
                        if (doc == null)
                            throw new RuntimeException("Not Found");

                        var listOfData = doc.select("table.c").get(0).select("tr > td").stream().skip(11).toList();
                        var books = new ArrayList<BookModel>();

                        for (int i = 0; i < listOfData.size(); i += 13) {
                            if (i < listOfData.size() + 10) {
                                var fileFormat = listOfData.get(i + 8).text();
                                if (fileFormat.equals("pdf") || fileFormat.equals("epub") || fileFormat.equals("rar")) {
                                    var book = new BookModel(listOfData.get(i).text(), listOfData.get(i + 1).text(), listOfData.get(i + 2).text(), listOfData.get(i + 3).text(), listOfData.get(i + 4).text(), listOfData.get(i + 5).text(), listOfData.get(i + 6).text(), listOfData.get(i + 7).text(), listOfData.get(i + 8).text(), listOfData.get(i + 9).select("a").attr("href"));
                                    books.add(book);
                                }
                            }
                        }

                        return books;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .thenApplyAsync(this::cleanAndFetchOtherData);
    }

    private List<BookModel> cleanAndFetchOtherData(List<BookModel> books) {
        books.forEach(book -> {
            try {
                var downloadPage = Jsoup.connect(book.getMirror()).userAgent("Mozilla").get();
                var downloadDiv = downloadPage.getElementById("download");
                String link = null;
                if (downloadDiv != null) link = downloadDiv.select("h2 > a").attr("href");

                book.setMirror(link);

                var imageUrl = downloadPage.select("img").attr("src");
                var baseURL = new StringBuilder();
                int slashCount = 0;
                for (int i = 0; i < downloadPage.baseUri().length(); i++) {

                    if (downloadPage.baseUri().charAt(i) == '/') slashCount++;
                    if (slashCount == 3) break;
                    baseURL.append(downloadPage.baseUri().charAt(i));

                }
                book.setImageUrl(baseURL + imageUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return books;
    }
}
