package fr.univlille.s3.S302.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import fr.univlille.s3.S302.utils.Distance;
import fr.univlille.s3.S302.utils.ModelUtils;
import javafx.util.Pair;

/**
 * Classe permettant de gérer les données et de les manipuler.
 * Elle permet de charger des données, de les ajouter, de les supprimer, de les catégoriser, de les afficher, de les sauvegarder, etc.
 * Elle permet également de gérer les données utilisateurs.
 * Elle est un singleton.
 * @param <E> le type de données à gérer
 */
public class DataManager<E extends Data> extends fr.univlille.s3.S302.utils.Observable {

    public static final String PATH = "iris.csv";
    private static DataManager<Data> instance;
    private List<E> dataList;
    private final List<E> userData;
    private final DataColorManager colorManager;
    private int bestN = 3;

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
        super();
        instance = (DataManager<Data>) this;
        this.dataList = dataList;
        this.userData = new ArrayList<>();
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

    public void reset() {
        this.dataList.clear();
        this.userData.clear();
        notifyAllObservers();
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
                f.initializeAttributes();
                dataList.add((E) f);
            }
            Data.updateDataTypes(dataList.get(0));
            notifyAllObservers();

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Fichier introuvable : " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement des données", e);
        }
    }

    /**
     * Ajoute une donnée à la liste de données.
     * @param data
     */
    public void addData(E data) {
        dataList.add(data);
        notifyAllObservers();
    }

    /**
     * Supprime une donnée de la liste de données.
     * @param data
     */
    public void removeData(E data) {
        dataList.remove(data);
        notifyAllObservers();
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
        notifyAllObservers();
    }

    /**
     * Ajoute une donnée que l'utilisateur a rentrée.
     * @param e
     */
    public void addUserData(E e) {
        userData.add(e);
        notifyAllObservers();
    }

    /**
     * Ajoute des données contenues dans une map.
     * @param map
     */
    public void addUserData(Map<String, Number> map) {
        Data tmp = new FakeData(map, dataList.get(0).getCategoryField());
        userData.add((E) tmp);
        notifyAllObservers();
    }

    /**
     * Categorise les données utilisateurs selon la distance souhaitée.
     * @param distanceSouhaitee
     */
    public void categorizeData(Distance distanceSouhaitee) {
        for (Data d : userData) {
            if (d.getCategory().equals("Unknown")) {
                List<Data> nearestData = getNearestDatas(d, distanceSouhaitee, bestN);
                Map<String, Integer> categories = new HashMap<>();
                for (Data data : nearestData) {
                    categories.put(data.getCategory(), categories.getOrDefault(data.getCategory(), 0) + 1);
                }
                d.setCategory(Collections.max(categories.entrySet(), Map.Entry.comparingByValue()).getKey());
            }
        }
        notifyAllObservers();
    }

    /**
     * Devine la catégorie d'une donnée selon les attributs donnés et la distance souhaitée.
     * @param guessAttributes
     * @param distanceSouhaitee
     * @return
     */
    public String guessCategory(Map<String, Number> guessAttributes, Distance distanceSouhaitee) {
        Data n = new FakeData(guessAttributes, dataList.get(0).getCategoryField());
        List<Data> nearestData = getNearestDatas(n, distanceSouhaitee, bestN);
        Map<String, Integer> categories = new HashMap<>();
        for (Data d : nearestData) {
            categories.put(d.getCategory(), categories.getOrDefault(d.getCategory(), 0) + 1);
        }
        return Collections.max(categories.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    /**
     * Cherche les données les n plus proche de la donnée donnée en paramètre.
     * @param data la donnée
     * @param distanceSouhaitee la distance souhaitée
     * @param nbVoisin le nombre de voisins a considerer
     * @return une liste de données
     */
    public List<Data> getNearestDatas(Data data, Distance distanceSouhaitee, int nbVoisin) {
        List<Data> nearestData = new ArrayList<>();
        List<Data> tmp = new ArrayList<>(dataList);

        for (int i = 0; i < nbVoisin; i++) {
            Data nearest = tmp.get(0);
            double minDistance = Data.distance(data, nearest, distanceSouhaitee);
            for (Data d : tmp) {
                double distance = Data.distance(data, d, distanceSouhaitee);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = d;
                }
            }
            nearestData.add(nearest);
            tmp.remove(nearest);
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
        colorManager.nextColor(getNbCategories());
    }
    public double getBestN(Distance d, String path, String targetField) throws FileNotFoundException {
        try {
            List<E> listetest= (List<E>) DataLoader.charger(path);
            for (Data da : listetest) {
                da.initializeAttributes();
            }
            for (Data da : listetest) {
                da.setCategoryField(targetField);
            }
            Pair<Integer,Double> p = ModelUtils.Robustesse((DataManager<Data>) this, (List<Data>) listetest,d);
            this.bestN = p.getKey();
            return p.getValue();
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement des données", e);
        }
    }

    public int getBestN() {
        return bestN;
    }
}