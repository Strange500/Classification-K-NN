package model;

import java.util.ArrayList;
import java.util.List;

public class DataManager<E> {
    private List<E> dataList;

    public DataManager(List<E> dataList) {
        this.dataList = dataList;
    }

    public DataManager() {
        this(new ArrayList<>());
    }

    public List<E> getDataList() {
        return dataList;
    }

    public void setDataList(List<E> dataList) {
        this.dataList = dataList;
    }

    public void addData(E data) {
        dataList.add(data);
    }

    public void removeData(E data) {
        dataList.remove(data);
    }

    public void loadData(String path) {
        // TODO Charger des données depuis un CSV (attendre TP OpenCSV)
    }

    public void classifyData(E data) {
        // TODO
    }
}
