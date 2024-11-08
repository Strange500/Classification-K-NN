module fr.univlille.s3.S302 {
    requires  javafx.controls;
    requires  javafx.fxml;
    requires  javafx.graphics;
    requires  com.opencsv;
    requires  java.sql;
    requires atlantafx.base;
    requires org.reflections;

    opens fr.univlille.s3.S302.view to javafx.fxml;
    opens fr.univlille.s3.S302.model;
    exports fr.univlille.s3.S302.view;
}