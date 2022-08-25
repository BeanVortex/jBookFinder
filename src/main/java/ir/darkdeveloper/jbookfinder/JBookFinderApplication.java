package ir.darkdeveloper.jbookfinder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@Slf4j
@RestController
@RequiredArgsConstructor
public class JBookFinderApplication {

    private final ScraperService service;


    public static void main(String[] args) throws TelegramApiException {
        var app = SpringApplication.run(JBookFinderApplication.class, args);
//        app.setWebApplicationType(WebApplicationType.NONE);
//        var run = app.run(args);
        var botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(app.getBean(TelBot.class));

    }

    @GetMapping("/")
    public CompletableFuture<List<BookModel>> getBooks(
            @RequestParam("s") String name,
            @RequestParam(name = "p", required = false) Integer page
    ) {
        return service.fetchBookModels(name, page);
    }

}
