package ir.darkdeveloper.jbookfinder.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.shape.Polygon;

public class MainController {
    @FXML
    private Polygon polygon;
    private double y;
    private double x;

    public void left(javafx.event.ActionEvent e) {
        System.out.println("MainController.left");
        polygon.setTranslateX(x-=10);
    }

    public void up(javafx.event.ActionEvent e) {
        System.out.println("MainController.up");
        polygon.setTranslateY(y-=10);
    }

    public void right(javafx.event.ActionEvent e) {
        System.out.println("MainController.right");
        polygon.setTranslateX(x+=10);
    }

    public void down(ActionEvent e) {
        System.out.println("MainController.down");
        polygon.setTranslateY(y+=10);
    }


}
