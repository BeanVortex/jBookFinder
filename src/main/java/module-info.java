module jbookfinder {

    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;
    requires java.logging;
    requires java.sql;
    requires reactor.core;
    requires org.apache.commons.io;
//    requires FXTrayIcon;
    requires org.jsoup;
    requires java.desktop;
    requires jdk.crypto.cryptoki;


    opens ir.darkdeveloper.jbookfinder.controllers to javafx.fxml;
    exports ir.darkdeveloper.jbookfinder;
}