package fr.univlille.s3.S302.model;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javafx.stage.FileChooser;

public class DataManager<E extends Data> implements Observable<E> {
    public static final String PATH = "iris.csv";
    public static DataManager<Data> instance = new DataManager<>();
    private List<E> dataList;
    private List<Observer> observers;

    private List<E> UserData;

    public DataManager(List<E> dataList) {
        this.dataList = dataList;
    }

    public DataManager() {
        this(new ArrayList<>());
        this.observers = new ArrayList<>();
        this.loadData(PATH);
        this.UserData = new ArrayList<>();
    }

    public static void main(String[] args) {
        DataManager<FormatDonneeBrut> dataManager = new DataManager<>();
        dataManager.loadData(PATH);
        System.out.println(dataManager.getDataList());
    }

    public List<E> getDataList() {
        return dataList;
    }

    public void setDataList(List<E> dataList) {
        this.dataList = dataList;
    }

    public void addData(E data) {
        dataList.add(data);
        notifyAllObservers();
    }

    public void removeData(E data) {
        dataList.remove(data);
    }

    public Set<String> getAttributes() {
        return dataList.get(0).getattributes().keySet();
    }

    public void loadData(String path) {
        try {
            dataList = new ArrayList<>();
            List<FormatDonneeBrut> tmp = DataLoader.charger(path);
            for (FormatDonneeBrut f : tmp) {
                dataList.add((E) FormatDonneeBrut.createObject(f));
            }
            notifyAllObservers();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void classifyData(E data) {
        // TODO
    }

    @Override
    public void attach(Observer<E> ob) {
        this.observers.add(ob);
    }

    @Override
    public void notifyAllObservers(E elt) {
        ArrayList tmp = new ArrayList<>(this.observers);
        for (Object ob : tmp) {
            if (ob instanceof Observer) ((Observer<E>) ob).update(this, elt);
        }
    }

    @Override
    public void notifyAllObservers() {
        ArrayList tmp = new ArrayList<>(this.observers);
        for (Object ob : tmp) {
            if (ob instanceof Observer) ((Observer<E>) ob).update(this);
        }
    }

    public void AddUserData(E e){
        UserData.add(e);
    }

    public List<E> getUserDataList(){
        return this.UserData;
    }
    public void addData(Map<String,Number> map){
        ArrayList<String> alist=new ArrayList<>(map.keySet());
        alist.sort(Comparator.naturalOrder());
        double Att0=map.get(alist.get(0)).doubleValue();
        double Att1=map.get(alist.get(0)).doubleValue();
        double Att2=map.get(alist.get(0)).doubleValue();
        double Att3=map.get(alist.get(0)).doubleValue();
        Iris tmpiris=new Iris(Att0,Att1,Att2,Att3,"Unknow");
        this.UserData.add((E)tmpiris);
        notifyAllObservers();
    }
}
