module fr.univlille.s3.S302 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.swing;
    requires com.opencsv;
    requires java.sql;
    requires atlantafx.base;
    requires org.reflections;
    requires com.google.common;

    opens fr.univlille.s3.S302 to javafx.fxml;
    opens fr.univlille.s3.S302.model;
    opens fr.univlille.s3.S302.controller to javafx.fxml;
    exports fr.univlille.s3.S302.view;
    exports fr.univlille.s3.S302.controller;
    exports fr.univlille.s3.S302.model.data;
}