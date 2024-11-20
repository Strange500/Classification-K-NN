package fr.univlille.s3.S302.model;

import java.io.FileNotFoundException;
import java.util.*;

import fr.univlille.s3.S302.utils.Distance;
import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;

/**
 * Classe permettant de gérer les données et de les manipuler.
 * Elle permet de charger des données, de les ajouter, de les supprimer, de les catégoriser, de les afficher, de les sauvegarder, etc.
 * Elle permet également de gérer les données utilisateurs.
 * Elle est un singleton.
 * @param <E> le type de données à gérer
 */
public class DataManager<E extends Data> implements Observable<E> {

    public static final String PATH = "iris.csv";
    private static DataManager<Data> instance;
    private List<E> dataList;
    private final List<E> userData;
    private final DataObserverManager<E> observerManager;
    private final DataColorManager colorManager;

    /**
     * Retourne l'instance de DataManager.
     * @return l'instance de DataManager
     */
    public static DataManager<Data> getInstance() {
        if (instance == null) {
            instance = new DataManager<>();
        }
        return instance;
    }

    /**
     * Constructeur de DataManager.
     * @param dataList la liste de données à gérer
     */
    private DataManager(List<E> dataList) {
        instance = (DataManager<Data>) this;
        this.dataList = dataList;
        this.userData = new ArrayList<>();
        this.observerManager = new DataObserverManager<>();
        this.colorManager = new DataColorManager();
    }

    /**
     * Constructeur de DataManager avec un fichier à charger par défaut.
     */
    private DataManager() {
        this(PATH);
    }

    /**
     * Constructeur de DataManager.
     * @param path le chemin du fichier à charger
     */
    private DataManager(String path) {
        this(new ArrayList<>());
        this.loadData(path);
    }

    /**
     * Charge les données du fichier spécifié.
     * @param path
     */
    public void loadData(String path) {
        try {
            dataList = new ArrayList<>();
            List<? extends Data> tmp = DataLoader.charger(path);
            for (Data f : tmp) {
                f.makeData();
                dataList.add((E) f);
            }
            Data.updateDataTypes(dataList.get(0));
            observerManager.notifyAllObservers();

        } catch (FileNotFoundException e) {
            System.out.println("Fichier non trouvé");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ajoute une donnée à la liste de données.
     * @param data
     */
    public void addData(E data) {
        dataList.add(data);
        observerManager.notifyAllObservers();
    }

    /**
     * Supprime une donnée de la liste de données.
     * @param data
     */
    public void removeData(E data) {
        dataList.remove(data);
        observerManager.notifyAllObservers();
    }

    /**
     * Change le champ de catégorie des données.
     * @param newCategoryField
     */
    public void changeCategoryField(String newCategoryField) {
        for (Data d : dataList) {
            d.setCategoryField(newCategoryField);
        }
        for (Data d : userData) {
            d.setCategoryField(newCategoryField);
        }
        observerManager.notifyAllObservers();
    }

    /**
     * Ajoute une donnée que l'utilisateur a rentrée.
     * @param e
     */
    public void addUserData(E e) {
        userData.add(e);
        observerManager.notifyAllObservers();
    }

    /**
     * Ajoute des données contenues dans une map.
     * @param map
     */
    public void addData(Map<String, Number> map) {
        Data tmp = new FakeData(map, dataList.get(0).getCategoryField());
        userData.add((E) tmp);
        observerManager.notifyAllObservers();
    }

    /**
     * Categorise les données utilisateurs selon la distance souhaitée.
     * @param distanceSouhaitee
     */
    public void categorizeData(Distance distanceSouhaitee) {
        for (Data d : userData) {
            if (d.getCategory().equals("Unknown")) {
                Data nearestData = getNearestData(d, distanceSouhaitee);
                d.setCategory(nearestData);
            }
        }
        observerManager.notifyAllObservers();
    }

    /**
     * Devine la catégorie d'une donnée selon les attributs donnés et la distance souhaitée.
     * @param guessAttributes
     * @param distanceSouhaitee
     * @return
     */
    public String guessCategory(Map<String, Number> guessAttributes, Distance distanceSouhaitee) {
        Data n = new FakeData(guessAttributes, dataList.get(0).getCategoryField());
        Data nearestData = getNearestData(n, distanceSouhaitee);
        return nearestData.getCategory();
    }

    /**
     * Renvoie la donnée la plus proche de la donnée donnée en paramètre.
     * @param data
     * @param distanceSouhaitee
     * @return
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
     * @param d
     * @return un boolean indiquant si la donnée donnée en paramètre est une donnée ajoutée par l'utilisateur
     */
    public boolean isUserData(Data d) {
        return userData.contains(d);
    }

    /**
     * @return un Set avec les attributs des données
     */
    public Set<String> getAttributes() {
        if (dataList.isEmpty()) {
            return new HashSet<>();
        }
        return dataList.get(0).getAttributes().keySet();
    }

    /**
     * @param attribute
     * @return un Set avec les valeurs possibles pour l'attribut donné en paramètre
     */
    public Set<String> getAvailableValues(String attribute) {
        Set<String> values = new HashSet<>();
        try {
            for (Data d : dataList) {
                values.add(d.getAttributes().get(attribute).toString());
            }
        } catch (NullPointerException e) {
            System.err.println("Un élément n'a pas d'attribut " + attribute + " ou dataList contient au moins un élément null");
            return new HashSet<>();
        }
        return values;
    }

    /**
     * @return le nombre de catégories différentes dans les données
     */
    private int getNbCategories() {
        Set<String> categories = new HashSet<>();
        for (Data d : dataList) {
            categories.add(d.getCategory());
        }
        return categories.size();
    }

    @Override
    public void attach(Observer<E> ob) {
        observerManager.attach(ob);
    }

    @Override
    public void notifyAllObservers(E elt) {
        observerManager.notifyAllObservers(elt);
    }

    @Override
    public void notifyAllObservers() {
        observerManager.notifyAllObservers();
    }

    /**
     * @return la liste de données
     */
    public List<E> getDataList() {
        return dataList;
    }

    /**
     * @return la liste de données utilisateurs
     */
    public List<E> getUserDataList() {
        return userData;
    }

    public static double valueOf(String attribute, String value) {
        return Data.valueOf(attribute, value);
    }

    public String nextColor() {
        return colorManager.nextColor(getNbCategories());
    }

    public void createColor() {
        colorManager.createColor(getNbCategories());
    }
}