package ir.darkdeveloper.jbookfinder.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public interface FXMLController {

    @FXML
    void initialize();

    void setStage(Stage stage);
    Stage getStage();
}
