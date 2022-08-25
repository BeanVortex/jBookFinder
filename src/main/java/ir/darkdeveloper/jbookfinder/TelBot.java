package ir.darkdeveloper.jbookfinder;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelBot extends TelegramLongPollingBot {

    private final ScraperService scraperService;

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage().getText();
            if (message.contains("\\dl_")) {

                var bookId = message.split("_")[1];

            } else {
                scraperService.fetchBookModels(message, 1)
                        .thenAccept(this::sendBooksListMessage);
            }

        } else if (update.hasCallbackQuery()) {

        }

    }

    private void sendBooksListMessage(List<BookModel> bookModels) {

//        var

        var message = new SendMessage();


    }

    private void sendBook(Update update, BookModel book) {
        try {
            var response = new SendPhoto();
            var imageFile = new File("src/main/resources/book_images/" + UUID.randomUUID() + ".jpg");
            FileUtils.copyURLToFile(
                    new URL(book.getImageUrl()),
                    imageFile
            );
            response.setChatId(update.getMessage().getChatId());
            response.setPhoto(new InputFile()
                    .setMedia(imageFile));
            response.setCaption(book.getTitle());

            var markupInline = new InlineKeyboardMarkup();
            var columns = new ArrayList<List<InlineKeyboardButton>>();
            var rows = new ArrayList<InlineKeyboardButton>();
            var downloadBtn = new InlineKeyboardButton();

            downloadBtn.setText("Download Book");
            downloadBtn.setUrl(book.getMirror());
            rows.add(downloadBtn);
            columns.add(rows);
            markupInline.setKeyboard(columns);
            response.setReplyMarkup(markupInline);

            execute(response);
            imageFile.deleteOnExit();
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }

}
