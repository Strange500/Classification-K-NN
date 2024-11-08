package fr.univlille.s3.S302.view;

import fr.univlille.s3.S302.model.*;
import fr.univlille.s3.S302.utils.Distance;
import fr.univlille.s3.S302.utils.DistanceEuclidienne;
import fr.univlille.s3.S302.utils.Observable;
import fr.univlille.s3.S302.utils.Observer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataController implements Observer<Data> {

    private final Map<String, String> categorieColor = new HashMap<>();
    @FXML
    ScatterChart<Number, Number> chart;
    @FXML
    ComboBox<String> xCategory;
    @FXML
    ComboBox<String> yCategory;
    List<Pair<XYChart.Data<Number, Number>, Data>> data;
    DataManager<Data> dataManager = DataManager.instance;
    Pair<String, String> choosenAttributes;
    @FXML
    private Button categoryBtn;
    @FXML
    private Button addDataBtn;
    @FXML
    private VBox addPointVBox;
    Map<String, TextField> labelMap = new HashMap<>();
    @FXML
    Canvas canvas;
    @FXML
    GridPane grid;
    private HeatView heatView;

    private final static Distance DEFAULT_DISTANCE = new DistanceEuclidienne();

    private void addTextFields() {
        addPointVBox.getChildren().clear();
        Map<String, Number> map = dataManager.getDataList().get(0).getattributes();
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
                    tmp.put(s, Double.parseDouble(labelMap.get(s).getText()));
                }
            }
            dataManager.addData(tmp);
        } catch (NumberFormatException e) {
            DataController.genErrorPopup("Entrez valeurs valides").show(addPointVBox.getScene().getWindow());
        }
    }

    private static String cleanLabelName(String s) {
        int[] indexMajuscules = new int[s.length()];
        int j = 0;
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                indexMajuscules[j] = i;
                j++;
            }
        }
        StringBuilder sb = new StringBuilder(s);
        for (int i = j - 1; i >= 0; i--) {
            sb.insert(indexMajuscules[i], " ");
        }
        return sb.toString().substring(0, 1).toUpperCase() + sb.toString().substring(1);
    }

    @FXML
    /**
     * Initialisation de la fenêtre
     */
    public void initialize() {
        buildWidgets();
        constructVBox();
        categoryBtn.setOnAction(event -> {
            try {
                updateAxisCategory();
                update();
                boolean heatViewActive = heatView.isActive();
                heatView = new HeatView(canvas, chart, xCategory.getValue(), yCategory.getValue(), categorieColor);
                if (heatViewActive) {
                    heatView.toggle();
                }
                heatView.update();
            } catch (IllegalArgumentException | NoSuchElementException ile) {
                Popup popup = genErrorPopup(ile.getMessage());
                popup.show(chart.getScene().getWindow());
            }
        });

        addDataBtn.setOnAction(event -> {
            addUserPoint();
        });

        heatView = new HeatView(canvas, chart, xCategory.getValue(), yCategory.getValue(), categorieColor);
        chart.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            canvas.setHeight(chart.getHeight());
            heatView.update();
        });
        chart.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            canvas.setWidth(chart.getWidth());
            Platform.runLater(() -> {
                heatView.update();
            });
        });

    }

    private void updateAxisCategory() {
        chart.getXAxis().setLabel(xCategory.getValue());
        chart.getYAxis().setLabel(yCategory.getValue());
        choosenAttributes = new Pair<>(xCategory.getValue(), yCategory.getValue());
    }

    /**
     * Construit les widgets de la fenêtre
     */
    private void buildWidgets() {
        data = new ArrayList<>();
        categorieColor.put("Unknown", "black");

        chart.getXAxis().setAutoRanging(true);
        chart.getYAxis().setAutoRanging(true);
        chart.setAnimated(false);
        chart.legendVisibleProperty().setValue(false);

        dataManager.attach(this);

        updateCategories();
        Set<String> attributes = dataManager.getAttributes();
        Iterator<String> it = attributes.iterator();
        xCategory.setValue(it.next());
        yCategory.setValue(it.next());
        updateAxisCategory();

        constructChart();
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
        constructChart();
        constructVBox();
    }

    /**
     * Met le style du graphique
     */
    private void setChartStyle() {
        for (final XYChart.Series<Number, Number> s : chart.getData()) {
            for (final XYChart.Data<Number, Number> data : s.getData()) {
                Data d = getNode(data);
                attachInfoTooltip(data, d);
                data.getNode().setOnMouseEntered(event -> {
                    data.getNode().setScaleX(1.5);
                    data.getNode().setScaleY(1.5);
                });
                data.getNode().setOnMouseExited(event -> {
                    data.getNode().setScaleX(1);
                    data.getNode().setScaleY(1);
                });

                setNodeColor(data.getNode(), d.getCategory());
                if (dataManager.isUserData(d)) {
                    displaySquare(data);
                }
            }
        }
    }

    private static void displaySquare(XYChart.Data<Number, Number> data) {
        String st = data.getNode().getStyle();
        data.getNode().setStyle(st + "-fx-background-radius: 0;");
    }

    private static void attachInfoTooltip(XYChart.Data<Number, Number> data, Data d) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText(d.getCategory() + "\n" + data.getXValue() + " : " + data.getYValue());
        tooltip.setShowDuration(javafx.util.Duration.seconds(10));
        tooltip.setShowDelay(javafx.util.Duration.seconds(0));
        Tooltip.install(data.getNode(), tooltip);
    }

    /**
     * Récupère les coordonnées d'un noeud
     * 
     * @param f le noeud
     * 
     * @return les coordonnées du noeud
     */
    private Pair<Number, Number> getNodeXY(Data f) {
        Map<String, Number> attributes = f.getattributes();
        return new Pair<>(attributes.get(choosenAttributes.getKey()), attributes.get(choosenAttributes.getValue()));
    }

    /**
     * Construit le graphique
     */
    private void constructChart() {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        chart.getData().clear();
        for (Data f : dataManager.getDataList()) {
            addPoint(f, series);
        }
        for (Data f : dataManager.getUserDataList()) {
            if (f.getattributes().containsKey(choosenAttributes.getKey())
                    && f.getattributes().containsKey(choosenAttributes.getValue())) {
                addPoint(f, series);
            }

        }
        chart.getData().add(series);
        setChartStyle();

    }

    private void addPoint(Data f, XYChart.Series<Number, Number> series) {
        Pair<Number, Number> choosenAttributes = getNodeXY(f);
        Coordonnee c = new Coordonnee(choosenAttributes.getKey().doubleValue(),
                choosenAttributes.getValue().doubleValue());
        XYChart.Data<Number, Number> node = new XYChart.Data<>(choosenAttributes.getKey(),
                choosenAttributes.getValue());
        data.add(new Pair<>(node, f));
        series.getData().add(node);
    }

    /**
     * Récupère le un point sur le graphique
     * 
     * @param data le noeud
     * 
     * @return le noeud
     */
    private Data getNode(XYChart.Data<Number, Number> data) {
        for (Pair<XYChart.Data<Number, Number>, Data> d : this.data) {
            if (d.getKey() == data) {
                return d.getValue();
            }
        }
        return null;
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
     * Met la couleur d'un noeud
     * 
     * @param node     le noeud
     * @param category la catégorie
     */
    public void setNodeColor(Node node, String category) {
        if (node == null) {
            return;
        }
        if (categorieColor.isEmpty()) {
            createColor();
        }
        if (!categorieColor.containsKey(category)) {
            categorieColor.put(category, dataManager.nextColor());
        }

        node.setStyle("-fx-background-color: " + categorieColor.get(category) + ";");
    }

    private void createColor() {
        dataManager.createColor();
    }

    /**
     * Met à jour le graphique
     */
    @Override
    public void update(Observable<Data> ob) {
        constructChart();
        heatView.update();
    }

    /**
     * Met à jour le graphique
     * 
     * @param elt les données
     */
    @Override
    public void update(Observable<Data> ob, Data elt) {
        constructChart();
        heatView.update();
    }

    /**
     * Charge un nouveau fichier CSV
     */
    public void loadNewCsv() {
        File file = getCsv();
        if (file != null) {
            DataManager<Data> dataManager = DataManager.instance;
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
     * Ouvre une nouvelle fenêtre
     * 
     * @throws IOException si la fenêtre ne peut pas être ouverte
     */
    public void openNewWindow() throws IOException {
        App app = new App();
        app.start(new Stage());
    }

    public void genererEcran() throws IOException {
        Stage stage = new Stage();
        Scene scene = new Scene(App.loadFXML("AddPointWindow"));
        stage.setScene(scene);
        stage.show();
    }

    public void classify() {
        dataManager.categorizeData(DEFAULT_DISTANCE);
    }

    public void toggleHeatView() {
        heatView.toggle();
    }
}
