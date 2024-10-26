package fr.univlille.s3.S302.model;

import java.util.Map;

public class FakeData implements Data{
    private final Map<String, Number> attributes;
    private String category = "Unknown";
    public FakeData(Map<String, Number> attr) {
        attributes = attr;

    }

    @Override
    public Map<String, Number> getattributes() {
        return attributes;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public Data createObject(Map<String, Number> map) {
        return null;
    }
}
