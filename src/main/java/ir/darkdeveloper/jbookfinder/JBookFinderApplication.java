package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;


//@SpringBootApplication
public class JBookFinderApplication extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
//        var springContext = SpringApplication.run(JBookFinderApplication.class);
//        Platform.setImplicitExit(false);
        SwitchSceneUtil.switchScene(stage, "MainController.fxml", "main.css");
        stage.setMinWidth(850);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
    }


    @Override
    public void stop() {
        System.out.println("stopped");
    }

}
