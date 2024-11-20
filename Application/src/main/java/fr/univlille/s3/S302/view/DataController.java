package fr.univlille.s3.S302.view;

import fr.univlille.s3.S302.model.*;
import fr.univlille.s3.S302.utils.*;
import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;

public class DataController implements Observer<Data> {

    @FXML
    ScatterChart<Number, Number> chart;
    @FXML
    ComboBox<String> xCategory;
    @FXML
    ComboBox<String> yCategory;
    List<Pair<XYChart.Data<Number, Number>, Data>> data;
    DataManager<Data> dataManager = DataManager.getInstance();
    Pair<String, String> choosenAttributes;
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
    Map<String, TextField> labelMap = new HashMap<>();
    @FXML
    Canvas canvas;
    @FXML
    GridPane grid;
    private HeatView heatView;

    private Chart chartController;

    private final static Distance DEFAULT_DISTANCE = new DistanceEuclidienne();

    @FXML
    /**
     * Initialisation de la fenêtre
     */
    public void initialize() {
        chartController = new Chart(this.chart);
        distanceComboBox.setValue("Euclidienne");
        distanceComboBox.setItems(FXCollections.observableArrayList("Euclidienne", "Manhattan"));
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

        heatView = new HeatView(canvas, chart, xCategory.getValue(), yCategory.getValue(), chartController.categorieColor);
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
    }
    private void addTextFields() {
        addPointVBox.getChildren().clear();
        Map<String, Number> map = dataManager.getDataList().get(0).getAttributes();
        for (String s : map.keySet()) {
            VBox tmp = genererLigneAttributs(s);
            addPointVBox.getChildren().add(tmp);
        }
    }

    public VBox genererLigneAttributs(String label) {
        VBox vbox = new VBox();
        Label labels = new Label(cleanLabelName(label));
        TextField tf = new TextField();
        vbox.getChildren().addAll(labels, tf);
        labelMap.put(label, tf);
        return vbox;
    }

    public void addUserPoint() {
        Map<String, Number> tmp = new HashMap<>();
        try {
            for (String s : labelMap.keySet()) {
                if (!labelMap.get(s).getText().isEmpty()) {
                    tmp.put(s, DataManager.valueOf(s, labelMap.get(s).getText()));
                }
            }
            dataManager.addData(tmp);
        } catch (NumberFormatException e) {
            DataController.genErrorPopup("Entrez valeurs valides").show(addPointVBox.getScene().getWindow());
        }
    }

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


    private HeatView recreateHeatView() {
        boolean heatViewActive = heatView.isActive();
        HeatView tmp = new HeatView(canvas, chart, xCategory.getValue(), yCategory.getValue(), chartController.categorieColor);
        if (heatViewActive) {
            tmp.toggle();
        }
        return tmp;
    }

    private void updateAxisCategory() {
        chart.getXAxis().setLabel(xCategory.getValue());
        chart.getYAxis().setLabel(yCategory.getValue());
        choosenAttributes = new Pair<>(xCategory.getValue(), yCategory.getValue());
    }

    private void changeCategoryField() {
        // random choice from attributes
        int i = 1;
        // decommenter quand c'est finit
        //dataManager.changeCategoryField(attributes.get(x));
    }

    /**
     * Construit les widgets de la fenêtre
     */
    private void buildWidgets() {
        data = new ArrayList<>();
        dataManager.attach(this);
        rebuild();
    }

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

        this.heatView = new HeatView(canvas, chart, xCategory.getValue(), yCategory.getValue(), chartController.categorieColor);
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
    public void update(Observable<Data> ob) {
        update();
    }

    /**
     * Met à jour le graphique
     * 
     * @param elt les données
     */
    @Override
    public void update(Observable<Data> ob, Data elt) {
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
    }

    private static File getCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un fichier CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(null);
        return file;
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

    private Distance getChosenDistance() {
        switch (distanceComboBox.getValue()) {
            case "Euclidienne":
                return new DistanceEuclidienne();
            case "Manhattan":
                return new DistanceManhattan();
            default:
                return new DistanceEuclidienne();
        }
    }

    public void classify() {
        dataManager.categorizeData(getChosenDistance(), 3);
    }

    public void toggleHeatView() {
        heatView.toggle();
    }
}
