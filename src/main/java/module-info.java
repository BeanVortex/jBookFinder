module jbookfinder {

    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.logging;
    requires java.base;
    requires reactor.core;
    requires org.reactivestreams;
    requires java.desktop;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.sql.rowset;
    requires org.apache.commons.io;
    requires org.jsoup;


    opens ir.darkdeveloper.jbookfinder to javafx.fxml, javafx.controls, javafx.base, javafx.graphics, java.base;
    opens ir.darkdeveloper.jbookfinder.controllers to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens ir.darkdeveloper.jbookfinder.repo to javafx.fxml, javafx.controls, javafx.base, javafx.graphics, java.sql;
    opens ir.darkdeveloper.jbookfinder.config to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens ir.darkdeveloper.jbookfinder.task to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens ir.darkdeveloper.jbookfinder.utils to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;
    opens ir.darkdeveloper.jbookfinder.model to javafx.fxml, javafx.controls, javafx.base, javafx.graphics;

    exports ir.darkdeveloper.jbookfinder;
    exports ir.darkdeveloper.jbookfinder.repo;
    exports ir.darkdeveloper.jbookfinder.config;
    exports ir.darkdeveloper.jbookfinder.task;
    exports ir.darkdeveloper.jbookfinder.controllers;
    exports ir.darkdeveloper.jbookfinder.utils;
    exports ir.darkdeveloper.jbookfinder.model;
}