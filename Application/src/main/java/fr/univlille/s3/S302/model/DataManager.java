package fr.univlille.s3.S302.model;

import java.io.FileNotFoundException;
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

    private final Map<String, Pair<Number, Number>> minMax = new HashMap<>();

    private static final int DEFAULT_NB_VOISIN = 3;
    private static DataManager<Data> instance;

    private final List<E> userData;
    private final DataColorManager colorManager;

    private List<E> dataList;
    private int bestNbVoisin = DEFAULT_NB_VOISIN;

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
     * Constructeur de DataManager.
     * @param path le chemin du fichier à charger
     */
    private DataManager(String path) {
        this(new ArrayList<>());
        this.loadData(path);
    }

    /**
     * Constructeur de DataManager avec un fichier à charger par défaut.
     */
    private DataManager() {
        this(PATH);
    }
    /**
     * @return la liste de données utilisateurs
     */
    public List<E> getUserDataList() {
        return userData;
    }
    /**
     * @return la liste de données
     */
    public List<E> getDataList() {
        return dataList;
    }

    public int getBestNbVoisin() {
        return bestNbVoisin;
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

    public Number getMax(String attribute) {
        return minMax.get(attribute).getValue();
    }

    public Number getMin(String attribute) {
        return minMax.get(attribute).getKey();
    }

    public void updateMinMax(String attribute, Number nb) {
        if (!minMax.containsKey(attribute)) {
            minMax.put(attribute, new Pair<>(nb, nb));
        } else {
            if (nb.doubleValue() > minMax.get(attribute).getValue().doubleValue()) {
                minMax.put(attribute, new Pair<>(minMax.get(attribute).getKey(), nb));
            }
            if (nb.doubleValue() < minMax.get(attribute).getKey().doubleValue()) {
                minMax.put(attribute, new Pair<>(nb, minMax.get(attribute).getValue()));
            }
        }
    }

    /**
     * Ajoute une donnée à la liste de données.
     */
    public void addData(E data) {
        dataList.add(data);
        notifyAllObservers();
    }

    /**
     * Supprime une donnée de la liste de données.
     */
    public void removeData(E data) {
        dataList.remove(data);
        notifyAllObservers();
    }

    /**
     * Ajoute une donnée que l'utilisateur a rentrée.
     * @param e La map correspondant aux attributs du point que l'on veut ajouter
     */
    public void addUserData(E e) {
        userData.add(e);
        notifyAllObservers();
    }

    /**
     * Ajoute des données contenues dans une map.
     * @param map La map correspondant aux attributs du point que l'on veut ajouter
     */
    @SuppressWarnings("unchecked")
    public void addUserData(Map<String, Number> map) {
        Data tmp = new FakeData(map, dataList.get(0).getCategoryField());
        userData.add((E) tmp);
        notifyAllObservers();
    }

    /**
     * @param data La data dont on veut determiner si elle est une data creé par l'utilisateur
     * @return un boolean indiquant si la donnée en paramètre est une donnée ajoutée par l'utilisateur
     */
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean isUserData(Data data) {
        return userData.contains(data);
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

    public void reset() {
        this.dataList.clear();
        this.userData.clear();
        notifyAllObservers();
    }

    /**
     * Charge les données du fichier spécifié dans DataManager
     * @param path chemin vers le fichier à charger
     */
    @SuppressWarnings("unchecked")
    public void loadData(String path) {
        try {
            dataList = new ArrayList<>();
            List<? extends Data> tmp = DataLoader.charger(path);
            for (Data f : tmp) {
                f.makeData();
                for (String key : f.getAttributes().keySet()) {
                    updateMinMax(key, f.getAttributes().get(key));
                }
                dataList.add((E) f);
            }
            Data.updateDataTypes(dataList.get(0));
            notifyAllObservers();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Change le champ de catégorie des données.
     * @param newCategoryField Le champ a utilisé commme catégorie
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
     * Categorise les données utilisateurs selon la distance souhaitée.
     * @param distanceSouhaitee La méthode de calcul de la distance
     */
    public void categorizeData(Distance distanceSouhaitee) {
        for (Data d : userData) {
            if (userDataIsNotCategorized(d)) {
                List<Data> nearestData = getNearestDatas(d, distanceSouhaitee, bestNbVoisin);
                Map<String, Integer> categories = new HashMap<>();
                for (Data data : nearestData) {
                    categories.put(data.getCategory(), categories.getOrDefault(data.getCategory(), 0) + 1);
                }
                d.setCategory(Collections.max(categories.entrySet(), Map.Entry.comparingByValue()).getKey());
            }
        }
        notifyAllObservers();
    }

    private static boolean userDataIsNotCategorized(Data userData) {
        if (userData == null) {
            throw new IllegalArgumentException("userDataIsNotCategorized : userData ne peut pas être nul ");
        }
        return userData.getCategory().equals("Unknown");
    }

    /**
     * Devine la catégorie d'une donnée selon les attributs donnés et la distance souhaitée.
     * @param guessAttributes La map correspondant aux attributs du point duquel il faut determiner la catégorie
     * @param distanceSouhaitee La méthode de calcul de la distance
     * @return La catégorie devinée
     */
    public String guessCategory(Map<String, Number> guessAttributes, Distance distanceSouhaitee) {
        Data n = new FakeData(guessAttributes, dataList.get(0).getCategoryField());
        List<Data> nearestData = getNearestDatas(n, distanceSouhaitee, bestNbVoisin);
        Map<String, Integer> categories = new HashMap<>();
        for (Data d : nearestData) {
            categories.put(d.getCategory(), categories.getOrDefault(d.getCategory(), 0) + 1);
        }
        return Collections.max(categories.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    /**
     * Cherche les données les n plus proche de la donnée en paramètre.
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

    public static double valueOf(String attribute, String value) {
        return Data.valueOf(attribute, value);
    }

    public String nextColor() {
        return colorManager.nextColor(getNbCategories());
    }

    public void createColor() {
        colorManager.nextColor(getNbCategories());
    }

    @SuppressWarnings("unchecked")
    public double getBestNbVoisin(Distance d, String path, String targetField) throws FileNotFoundException {
        List<E> listetest= (List<E>) DataLoader.charger(path);
        for (Data da : listetest) {
            da.makeData();
        }
        for (Data da : listetest) {
            da.setCategoryField(targetField);
        }
        Pair<Integer,Double> p = ModelUtils.Robustesse((DataManager<Data>) this, (List<Data>) listetest,d);
        this.bestNbVoisin = p.getKey();
        return p.getValue();
    }


}