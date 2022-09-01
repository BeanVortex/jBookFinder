package ir.darkdeveloper.jbookfinder;

import ir.darkdeveloper.jbookfinder.utils.SwitchSceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class JBookFinderApplication extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        var springContext = SpringApplication.run(JBookFinderApplication.class);
//        Platform.setImplicitExit(false);
        SwitchSceneUtil.switchScene(stage, "fxml/MainController.fxml", "css/main.css");
        stage.setMinWidth(640);
        stage.setMinHeight(480);
        stage.setTitle("Main Page");
        stage.show();
    }


    @Override
    public void stop() throws Exception {
        System.out.println("stopped");
    }

}
