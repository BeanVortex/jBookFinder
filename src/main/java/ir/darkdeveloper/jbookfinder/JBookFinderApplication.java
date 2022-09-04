package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.utils.BookUtils;
import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;


import java.io.File;

public class JBookFinderApplication extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        createSaveLocation();
        SwitchSceneUtil.switchSceneToMain(stage, "MainController.fxml", "main.css");
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
    }

    private void createSaveLocation() {
        var saveLocation = new BookUtils().getSaveLocation();
        var file = new File(saveLocation);
        if (file.mkdir())
            System.out.println("created dir");
        else
            System.out.println("not created dir");

        saveLocation += "/bookImages/";
        file = new File(saveLocation);
        if (file.mkdir())
            System.out.println("created book image dir");
        else
            System.out.println("not created book image dir");
    }


    @Override
    public void stop() {
        System.out.println("stopped");
    }

}
