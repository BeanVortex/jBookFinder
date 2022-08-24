package ir.darkdeveloper.jbookfinder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@Slf4j
@RestController
public class JBookFinderApplication {

    private final ScraperService service;

    public JBookFinderApplication(ScraperService service) {
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(JBookFinderApplication.class, args);
    }

    @GetMapping("/")
    public CompletableFuture<List<BookModel>> getBooks(
            @RequestParam("s") String name,
            @RequestParam(name = "p", required = false) Integer page
    ) {
        return service.fetchBookModels(name, page);
    }

}
