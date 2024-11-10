package fr.univlille.s3.S302.model;

import java.io.FileNotFoundException;
import java.util.*;

import fr.univlille.s3.S302.utils.Distance;
import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;

/**
 * Classe pour la gestion des données
 */
public class DataManager<E extends Data> implements Observable<E> {

    public static final String PATH = "iris.csv";
    private static DataManager<Data> instance ;
    private List<E> dataList;
    private List<Observer> observers;
    private List<E> UserData;
    private Map<String, String> colorMap;
    private static int idxColor = 0;

    public static DataManager<Data> getInstance() {
        if (instance == null) {
            instance = new DataManager<>();
        }
        return instance;
    }



    /**
     * Constructeur de la classe DataManager
     * 
     * @param dataList la liste des données
     */
    private DataManager(List<E> dataList) {
        instance = (DataManager<Data>) this;
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
    }

    public double valueOf(String attribute, String value) {
        return Data.valueOf(attribute, value);
    }

    /**
     * @return la liste des observateurs
     */
    public List<Observer> getObservers() {
        return observers;
    }

    /**
     * @return la liste des données
     */
    public List<E> getDataList() {
        return new ArrayList<>(dataList);
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
        if (dataList.isEmpty()) {
            return new HashSet<>();
        }
        return dataList.get(0).getAttributes().keySet();
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
                f.makeData();
                dataList.add((E) f);
            }
            //System.out.println(this.dataList);
            Data.updateDataTypes(dataList.get(0));
            notifyAllObservers();

        } catch (FileNotFoundException e) {
            System.out.println("Fichier non trouvé");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void changeCategoryField(String newcategoryField){
        for (Data d : dataList) {
            d.setCategoryField(newcategoryField);
        }
        for (Data d : UserData) {
            d.setCategoryField(newcategoryField);
        }
        notifyAllObservers();
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

    /**
     * Ajoute une donnée utilisateur
     */
    public void AddUserData(E e){
        UserData.add(e);
        notifyAllObservers();
    }

    /**
     * @return la liste des données utilisateur
     */
    public List<E> getUserDataList(){
        return this.UserData;
    }

    /**
     * Ajoute une liste de données utilisateur
     */
    public void addData(Map<String,Number> map){
        Data tmp = new FakeData(map, dataList.get(0).getCategoryField());
        this.UserData.add((E)tmp);
        notifyAllObservers();
    }

    // ne fonctionne que si le nombre de catégories est un multiple de 3
    // a refaire
    public void createColor() {
        colorMap = new HashMap<>();
        int nbCategories = getNbCategories();
        for (int i = 0; i < nbCategories; i++) {
            colorMap.put("Color" + i, "rgb(" + (int) (Math.random() * 255) + "," + (int) (Math.random() * 255) + "," + (int) (Math.random() * 255) + ")");
        }

    }

    /**
     * @return la liste des catégories
     */
    private int getNbCategories() {
        Set<String> categories = new HashSet<>();
        for (Data d : dataList) {
            categories.add(d.getCategory());
        }
        return categories.size();
    }

    /**
     * @return la couleur suivante
     */
    public String nextColor() {
        if (colorMap == null || colorMap.size() != getNbCategories()) {
            createColor();
        }
        String color = colorMap.get("Color" + idxColor);
        idxColor = (idxColor + 1) % getNbCategories();
        return color;
    }

    /**
     * Catégorise les données
     */
    public void categorizeData(Distance distanceSouhaitee) {
        for (Data d : UserData) {
            if (d.getCategory().equals("Unknown")) {
                Data nearestData = getNearestData(d, distanceSouhaitee);
                d.setCategory(nearestData);
            }
        }
        notifyAllObservers();
    }

    public Set<String> getAvailableValues(String attribute){
        Set<String> values = new HashSet<>();
        try{
            for (Data d : dataList) {
                values.add(d.getAttributes().get(attribute).toString());
            }
        } catch (NullPointerException e) {
            System.err.println("soit Un element n'a pas d'attribut " + attribute + " soit datalist contient au moins un elements null");
            return new HashSet<>();
        }
        return values;
    }

    /**
     * Devine la catégorie d'une donnée à partir de ses attributs
     * @param guessAttributes
     * @return la catégorie
     */
    public String guessCategory(Map<String, Number> guessAttributes, Distance distanceSouhaitee) {
        Data n = new FakeData(guessAttributes, dataList.get(0).getCategoryField());
        Data nearestData = getNearestData(n, distanceSouhaitee);
        return nearestData.getCategory();
    }

    /**
     * @param data la donnée
     * @return la donnée la plus proche
     */
    public Data getNearestData(Data data, Distance distanceSouhaitee) {
        double minDistance = Double.MAX_VALUE;
        Data nearestData = null;
        for (Data d : dataList) {
            double distance = Data.distance(data, d, distanceSouhaitee);
            if (distance < minDistance) {
                minDistance = distance;
                nearestData = d;
            }
        }
        return nearestData;
    }

    /**
     * @param d la donnée
     * @return vrai si la donnée est une donnée utilisateur
     */
    public boolean isUserData(Data d){
        return UserData.contains(d);
    }
}
