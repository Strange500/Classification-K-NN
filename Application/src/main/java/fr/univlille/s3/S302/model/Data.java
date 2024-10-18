package fr.univlille.s3.S302.model;

import java.util.Map;

public interface Data {

    Map<String, Number> getattributes();

    String getCategory();

    void setCategory(String category);

    Data createObject(Map<String,Number> map);
}
