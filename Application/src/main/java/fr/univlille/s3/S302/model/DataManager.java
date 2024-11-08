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
    public static DataManager<Data> instance = new DataManager<>();
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
    private DataManager(List<E> dataList) {
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
            notifyAllObservers();

        } catch (FileNotFoundException | NullPointerException e) {
            System.out.println("Fichier non trouvé");
        }
    }

    public void changecategory(String newCategory){
        for (Data d : dataList) {
            d.setCategory(newCategory);
        }
        for (Data d : UserData) {
            d.setCategory(newCategory);
        }
        notifyAllObservers();
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
        }else {
            for (int i = 0; i < nbCategories; i++) {
                colorMap.put("Color" + i, "rgb(" + (int) (Math.random() * 255) + "," + (int) (Math.random() * 255) + "," + (int) (Math.random() * 255) + ")");
            }
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
                d.setCategory(nearestData.getCategory());
            }
        }
        notifyAllObservers();
    }

    /**
     * Devine la catégorie d'une donnée à partir de ses attributs
     * @param guessAttributes
     * @return la catégorie
     */
    public String guessCategory(Map<String, Number> guessAttributes, Distance distanceSouhaitee) {
        Data n = new FakeData(guessAttributes);
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
            double distance = distanceSouhaitee.distance(data, d);
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
