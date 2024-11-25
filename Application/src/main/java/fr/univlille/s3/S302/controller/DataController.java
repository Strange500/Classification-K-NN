package fr.univlille.s3.S302.controller;

import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataManager;
import fr.univlille.s3.S302.utils.Distance;
import fr.univlille.s3.S302.utils.DistanceEuclidienne;
import fr.univlille.s3.S302.utils.DistanceEuclidienneNormalisee;
import fr.univlille.s3.S302.utils.DistanceManhattan;
import fr.univlille.s3.S302.utils.DistanceManhattanNormalisee;
import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;
import fr.univlille.s3.S302.view.App;
import fr.univlille.s3.S302.view.Chart;
import fr.univlille.s3.S302.view.HeatView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;

/**
 * Contrôleur de la fenêtre
 */
public class DataController extends Observer {

    List<Pair<XYChart.Data<Number, Number>, Data>> data;
    DataManager<Data> dataManager = DataManager.getInstance();
    Pair<String, String> choosenAttributes;
    Map<String, TextField> labelMap = new HashMap<>();
    private Chart chartController;
    private Distance defaultDistance = new DistanceEuclidienne();

    @FXML
    ScatterChart<Number, Number> chart;
    @FXML
    ComboBox<String> xCategory;
    @FXML
    ComboBox<String> yCategory;
    @FXML
    private Button categoryBtn;
    @FXML
    private Button addDataBtn;
    @FXML
    private ComboBox<String> distanceComboBox;
    @FXML
    private Button saveChart;
    @FXML
    private VBox addPointVBox;
    @FXML
    Canvas canvas;
    @FXML
    GridPane grid;
    @FXML
    Label pRobustesse;
    @FXML
    Label nbVoisin;
    private HeatView heatView;
    @FXML
    ComboBox<String> cateCombo;

    /**
     * Initialisation de la fenêtre
     */
    public void initialize() {
        chartController = new Chart(this.chart);
        distanceComboBox.setValue("Euclidienne");
        distanceComboBox.setItems(FXCollections.observableArrayList("Euclidienne", "Manhattan", "Euclidienne normalisée", "Manhattan normalisée"));
        cateCombo.getItems().addAll(dataManager.getAttributes());
        cateCombo.setValue(dataManager.getDataList().get(0).getCategoryField());
        nbVoisin.setText(dataManager.getBestNbVoisin() + " Voisins");
        buildWidgets();
        constructVBox();
        categoryBtn.setOnAction(event -> {
            try {
                updateAxisCategory();
                update();
                this.heatView = recreateHeatView();
                this.heatView.update();
            } catch (IllegalArgumentException | NoSuchElementException ile) {
                Popup popup = genErrorPopup(ile.getMessage());
                popup.show(chart.getScene().getWindow());
            }
        });

        addDataBtn.setOnAction(event -> {
            addUserPoint();
        });

        heatView = new HeatView(canvas, chart, xCategory.getValue(), yCategory.getValue(), chartController.categorieColor, defaultDistance);
        chart.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            canvas.setHeight(chart.getHeight());
            heatView.update();
        });
        chart.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            canvas.setWidth(chart.getWidth());
            heatView.update();
        });

        cateCombo.setOnAction(event -> {
            if (cateCombo.getValue() == null) {
                return;
            }
            changeCategoryField();
        });

        distanceComboBox.setOnAction(event -> {
            defaultDistance = getChosenDistance();
            update();
        });
    }

    /**
     * Ajoute les champs de texte pour ajouter un point
     */
    private void addTextFields() {
        addPointVBox.getChildren().clear();
        Map<String, Number> map = dataManager.getDataList().get(0).getAttributes();
        for (String s : map.keySet()) {
            if (s.equals(cateCombo.getValue())) {
                continue;
            }
            VBox tmp = genererLigneAttributs(s);
            addPointVBox.getChildren().add(tmp);
        }
    }

    /**
     * Génère une ligne d'attributs
     *
     * @param label le nom de l'attribut
     *
     * @return une VBox contenant le label et le champ de texte
     */
    public VBox genererLigneAttributs(String label) {
        VBox vbox = new VBox();
        Label labels = new Label(cleanLabelName(label));
        TextField tf = new TextField();
        vbox.getChildren().addAll(labels, tf);
        labelMap.put(label, tf);
        return vbox;
    }

    /**
     * Ajoute un point à la liste de points
     */
    public void addUserPoint() {
        Map<String, Number> tmp = new HashMap<>();
        try {
            for (String s : labelMap.keySet()) {
                if (!labelMap.get(s).getText().isEmpty()) {
                    tmp.put(s, DataManager.valueOf(s, labelMap.get(s).getText()));
                }
            }
            dataManager.addUserData(tmp);
        } catch (NumberFormatException e) {
            DataController.genErrorPopup("Entrez valeurs valides").show(addPointVBox.getScene().getWindow());
        }
    }

    /**
     * Nettoie le nom de l'attribut
     *
     * @param label le nom de l'attribut
     *
     * @return le nom de l'attribut nettoyé
     */
    private static String cleanLabelName(String label) {
        int[] indexMajuscules = new int[label.length()];
        int j = 0;
        for (int i = 0; i < label.length(); i++) {
            if (Character.isUpperCase(label.charAt(i))) {
                indexMajuscules[j] = i;
                j++;
            }
        }
        StringBuilder sb = new StringBuilder(label);
        for (int i = j - 1; i >= 0; i--) {
            sb.insert(indexMajuscules[i], " ");
        }
        return sb.substring(0, 1).toUpperCase() + sb.substring(1);
    }

    /**
     * Sauvegarde le graphique en image à l'endroit où l'utilisateur le souhaite
     */
    public void saveChartAsImage() {
        String path = getPathToSaveChart();

        if (path == null) {
            return;
        }

        WritableImage image = chart.snapshot(new SnapshotParameters(), null);
        File file = new File(path);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            System.out.println("Image saved; path: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("An error occurred while saving the image");
        }
    }

    /**
     * Recrée la heatview
     *
     * @return la nouvelle heatview
     */
    private HeatView recreateHeatView() {
        boolean heatViewActive = heatView.isActive();
        HeatView tmp = new HeatView(canvas, chart, xCategory.getValue(), yCategory.getValue(), chartController.categorieColor, defaultDistance);
        if (heatViewActive) {
            tmp.toggle();
        }
        return tmp;
    }

    /**
     * Met à jour les catégories des axes
     */
    private void updateAxisCategory() {
        chart.getXAxis().setLabel(xCategory.getValue());
        chart.getYAxis().setLabel(yCategory.getValue());
        choosenAttributes = new Pair<>(xCategory.getValue(), yCategory.getValue());
    }

    private void changeCategoryField() {
        dataManager.changeCategoryField(cateCombo.getValue());
    }

    /**
     * Construit les widgets de la fenêtre
     */
    private void buildWidgets() {
        data = new ArrayList<>();
        dataManager.attach(this);
        rebuild();
    }

    /**
     * Reconstruit le graphique
     */
    private void rebuild() {
        boolean heatViewActive = false;
        if (heatView != null) {
            heatViewActive = heatView.isActive();
            this.heatView = null;
        }

        if (!dataManager.getAttributes().equals(new HashSet<>(xCategory.getItems()))) {
            updateCategories();
            Set<String> attributes = dataManager.getAttributes();
            Iterator<String> it = attributes.iterator();
            xCategory.setValue(it.next());
            yCategory.setValue(it.next());
        }
        updateAxisCategory();
        chartController.recreateChart(dataManager.getDataList(), dataManager.getUserDataList(), choosenAttributes);

        this.heatView = new HeatView(canvas, chart, xCategory.getValue(), yCategory.getValue(), chartController.categorieColor, defaultDistance);
        if (heatViewActive) {
            this.heatView.toggle();
        }

    }

    /**
     * Génère une popup d'erreur
     * 
     * @param message le message d'erreur
     * 
     * @return la popup d'erreur
     */
    public static Popup genErrorPopup(String message) {
        Popup popup = new Popup();
        Label label = new Label("Erreur: \n" + message);
        label.setStyle(
                " -fx-background-color: black; -fx-border-radius: 10; -fx-padding: 10; -fx-border-color: red; -fx-border-width: 2;");
        label.setMinHeight(50);
        label.setMinWidth(200);
        popup.getContent().add(label);
        popup.setAutoHide(true);
        return popup;
    }

    private void constructVBox() {
        addTextFields();
    }

    /**
     * Met à jour le graphique
     */
    private void update() {
        rebuild();
        constructVBox();
    }


    /**
     * Récupère les attributs
     * 
     * @return les attributs
     */
    private Set<String> getAttributes() {
        return this.dataManager.getAttributes();
    }

    /**
     * Met à jour les catégories
     */
    private void updateCategories() {
        xCategory.getItems().clear();
        yCategory.getItems().clear();
        xCategory.getItems().addAll(getAttributes());
        yCategory.getItems().addAll(getAttributes());
    }

    /**
     * Met à jour le graphique
     */
    @Override
    public void update(Observable ob) {
        update();
    }

    /**
     * Met à jour le graphique
     * 
     * @param elt les données
     */
    @Override
    public void update(Observable ob, Object elt) {
        update();
    }

    /**
     * Charge un nouveau fichier CSV
     */
    public void loadNewCsv() {
        File file = getCsv();
        if (file != null) {
            dataManager.loadData(file.getAbsolutePath());
            buildWidgets();
        }
        cateCombo.getItems().clear();
        cateCombo.getItems().addAll(dataManager.getAttributes());
        cateCombo.setValue(dataManager.getDataList().get(0).getCategoryField());
    }

    /**
     * @return le fichier CSV à charger
     */
    private static File getCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un fichier CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return  fileChooser.showOpenDialog(null);
    }

    /**
     * @return le chemin où le graphique doit être sauvegardée
     */
    private static String getPathToSaveChart() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le graphique");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers PNG", "*.png"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showSaveDialog(null);
        return (file != null) ? file.getAbsolutePath() : null;
    }

    /**
     * Ouvre une nouvelle fenêtre
     * 
     * @throws IOException si la fenêtre ne peut pas être ouverte
     */
    public void openNewWindow() throws IOException {
        App app = new App();
        app.start(new Stage());
    }

    /**
     * @return la distance choisie par l'utilisateur dans la ComboBox
     */
    private Distance getChosenDistance() {
        switch (distanceComboBox.getValue()) {
            case "Manhattan":
                return new DistanceManhattan();
            case "Euclidienne normalisée":
                return new DistanceEuclidienneNormalisee();
            case "Manhattan normalisée":
                return new DistanceManhattanNormalisee();
            default:
                return new DistanceEuclidienne();
        }
    }

    /**
     * Classifie les données
     */
    public void classify() {
        dataManager.categorizeData(getChosenDistance());
    }

    /**
     * Affiche ou cache la HeatView
     */
    public void toggleHeatView() {
        heatView.toggle();
    }

    /**
     * Met à jour les labels de robustesse
     */
    public void updateRobustesseLabels()  {

        double percent;
        try {
            percent = dataManager.getBestNbVoisin(defaultDistance,getCsv().getPath(), cateCombo.getValue());
        } catch (FileNotFoundException e) {
            genErrorPopup("Erreur lors du chargement du fichier").show(chart.getScene().getWindow());
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            genErrorPopup("Aucun fichier selectionné").show(chart.getScene().getWindow());
            throw new RuntimeException(e);
        }
        pRobustesse.setVisible(true);
        nbVoisin.setVisible(true);
        pRobustesse.setText((percent *100) + " %");
        nbVoisin.setText(dataManager.getBestNbVoisin() + " Voisins");
    }
}
