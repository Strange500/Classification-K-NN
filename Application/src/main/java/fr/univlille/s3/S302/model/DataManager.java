package fr.univlille.s3.S302.model;

import java.io.FileNotFoundException;
import java.util.*;

import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;

public class DataManager<E extends Data> implements Observable<E> {

    public static final String PATH = "iris.csv";
    public static DataManager<Data> instance = new DataManager<>("/home/strange/IdeaProjects/H2_SAE3.3/Application/src/main/resources/fr/univlille/s3/S302/model/random.csv");
    private List<E> dataList;
    private List<Observer> observers;
    private List<E> UserData;
    private Map<String, String> colorMap;
    private static int idxColor = 0;

    /**
     * Constructeur de la classe DataManager
     * 
     * @param dataList la liste des données
     */
    public DataManager(List<E> dataList) {
        this.dataList = dataList;
        this.observers = new ArrayList<>();
        this.UserData = new ArrayList<>();
    }

    /**
     * Constructeur de la classe DataManager
     */
    public DataManager() {
        this(PATH);
    }

    public DataManager(String path) {
        this(new ArrayList<>());
        this.loadData(path);
    }

    public static void main(String[] args) {
        DataManager<Data> dataManager = new DataManager<>();
        dataManager.loadData(PATH);
        System.out.println(dataManager.getDataList());
    }

    public List<Observer> getObservers() {
        return observers;
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
        notifyAllObservers();
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
            List<? extends Data> tmp = DataLoader.charger(path);
            for (Data f : tmp) {
                dataList.add((E) f);
            }
            notifyAllObservers();

        } catch (FileNotFoundException | NullPointerException e) {
            System.out.println("Fichier non trouvé");
        }
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
        notifyAllObservers();
    }

    public List<E> getUserDataList(){
        return this.UserData;
    }
    public void addData(Map<String,Number> map){
        Data tmp = new FakeData(map);
        this.UserData.add((E)tmp);
        notifyAllObservers();
    }

    // ne fonctionne que si le nombre de catégories est un multiple de 3
    // a refaire
    public void createColor() {
        colorMap = new HashMap<>();
        int nbCategories = getNbCategories();
        if (nbCategories % 3 == 0 && nbCategories > 0) {
            int step = 255 / (nbCategories / 3);
            int r = 255;
            int g = 0;
            int b = 0;
            for (int i = 0; i < nbCategories; i++) {
                colorMap.put("Color" + i, "rgb(" + r + "," + g + "," + b + ")");
                if (r == 255 && g < 255 && b == 0) {
                    g += step;
                } else if (r > 0 && g == 255 && b == 0) {
                    r -= step;
                } else if (r == 0 && g == 255 && b < 255) {
                    b += step;
                } else if (r == 0 && g > 0 && b == 255) {
                    g -= step;
                } else if (r < 255 && g == 0 && b == 255) {
                    r += step;
                } else if (r == 255 && g == 0 && b > 0) {
                    b -= step;
                }
            }
        }
    }

    private int getNbCategories() {
        Set<String> categories = new HashSet<>();
        for (Data d : dataList) {
            categories.add(d.getCategory());
        }
        return categories.size();
    }

    public String nextColor() {
        if (colorMap == null) {
            createColor();
        }
        String color = colorMap.get("Color" + idxColor);
        idxColor = (idxColor + 1) % getNbCategories();
        return color;
    }

    public void categorizeData() {
        for (Data d : UserData) {
            if (d.getCategory().equals("Unknown")) {
                Data nearestData = getNearestData(d);
                d.setCategory(nearestData.getCategory());
            }
        }
        notifyAllObservers();
    }

    public String guessCategory(Map<String, Number> guessAttributes) {
        Data n = new FakeData(guessAttributes);
        Data nearestData = getNearestData(n);
        return nearestData.getCategory();
    }

    public Data getNearestData(Data data) {
        double minDistance = Double.MAX_VALUE;
        Data nearestData = null;
        for (Data d : dataList) {
            double distance = euclideanDistance(data, d);
            if (distance < minDistance) {
                minDistance = distance;
                nearestData = d;
            }
        }
        return nearestData;
    }


    private double euclideanDistance(Data d1, Data d2) {
        double distance = 0;
        Map<String, Number> attributes1 = d1.getattributes();
        Map<String, Number> attributes2 = d2.getattributes();
        for (String attribute : attributes1.keySet()) {
            if (!attributes2.containsKey(attribute)) {
                continue;
            }
            double diff = attributes1.get(attribute).doubleValue() - attributes2.get(attribute).doubleValue();
            distance += diff * diff;
        }
        return Math.sqrt(distance);
    }

    public boolean isUserData(Data d){
        return UserData.contains(d);
    }
}
