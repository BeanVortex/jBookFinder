package ir.darkdeveloper.jbookfinder.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LibraryController implements FXMLController {

    @FXML
    private VBox rootVbox;
    @FXML
    private MenuBar menuBar;
    @FXML
    private VBox contentVbox;
    @FXML
    private FlowPane booksContainer;

    @FXML
    private void getBack(ActionEvent actionEvent) {
    }

    @FXML
    private void showSettings(ActionEvent actionEvent) {
    }

    @Override
    public void initialize() {

    }

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public Stage getStage() {
        return null;
    }
}
