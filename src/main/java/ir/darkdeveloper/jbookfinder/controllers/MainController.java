package ir.darkdeveloper.jbookfinder.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.springframework.util.StringUtils;

public class MainController {

    @FXML
    private TextField fieldSearch;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnBooks;

//    private final ScraperService scraperService;


    @FXML
    private void searchTheBook(ActionEvent e) {
        var text = fieldSearch.getText();
        if (!text.isBlank()) {
            var trimmedText = StringUtils.trimWhitespace(text);
        }
    }


}
