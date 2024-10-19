package fr.univlille.s3.S302.model;

import java.io.IOException;
import java.util.*;

import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;

public class DataManager<E extends Data> implements Observable<E> {

    public static final String PATH = "iris.csv";
    public static DataManager<Data> instance = new DataManager<>();
    private List<E> dataList;
    private List<Observer> observers;
    private List<E> UserData;

    /**
     * Constructeur de la classe DataManager
     * 
     * @param dataList la liste des données
     */
    public DataManager(List<E> dataList) {
        this.dataList = dataList;
    }

    /**
     * Constructeur de la classe DataManager
     */
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

    /**
     * @return la liste des données
     */
    public List<E> getDataList() {
        return dataList;
    }

    /**
     * @param dataList la nouvelle liste des données
     */
    public void setDataList(List<E> dataList) {
        this.dataList = dataList;
    }

    /**
     * Ajoute une donnée à la liste des données
     * 
     * @param data la donnée à ajouter
     */
    public void addData(E data) {
        dataList.add(data);
        notifyAllObservers();
    }

    /**
     * Supprime une donnée de la liste des données
     * 
     * @param data la donnée à supprimer
     */
    public void removeData(E data) {
        dataList.remove(data);
    }

    /**
     * @return les attributs des données
     */
    public Set<String> getAttributes() {
        return dataList.get(0).getattributes().keySet();
    }

    /**
     * Charge les données à partir d'un fichier
     * 
     * @param path le chemin du fichier
     */
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

    /**
     * Classifie les données
     * 
     * @param data la donnée à classer
     */
    public void classifyData(E data) {
        // TODO
    }

    /**
     * Ajoute un observateur
     */
    @Override
    public void attach(Observer<E> ob) {
        this.observers.add(ob);
    }

    /**
     * Notifie tous les observateurs et update l'observateur avec l'élément
     */
    @Override
    public void notifyAllObservers(E elt) {
        ArrayList tmp = new ArrayList<>(this.observers);
        for (Object ob : tmp) {
            if (ob instanceof Observer)
                ((Observer<E>) ob).update(this, elt);
        }
    }

    /**
     * Notifie tous les observateurs et update l'observateur
     */
    @Override
    public void notifyAllObservers() {
        ArrayList tmp = new ArrayList<>(this.observers);
        for (Object ob : tmp) {
            if (ob instanceof Observer)
                ((Observer<E>) ob).update(this);
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
        double Att1=map.get(alist.get(1)).doubleValue();
        double Att2=map.get(alist.get(2)).doubleValue();
        double Att3=map.get(alist.get(3)).doubleValue();
        Iris tmpiris=new Iris(Att2,Att3,Att0,Att1,"Unknown");
        this.UserData.add((E)tmpiris);
        notifyAllObservers();
    }
}
