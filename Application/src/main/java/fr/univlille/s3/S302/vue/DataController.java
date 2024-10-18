package fr.univlille.s3.S302.vue;

import fr.univlille.s3.S302.model.Observable;
import fr.univlille.s3.S302.model.Observer;
import fr.univlille.s3.S302.model.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    public void initialize() {

        buildWidgets();

        categoryBtn.setOnAction(event -> {
            try {
                chart.getXAxis().setLabel(xCategory.getValue());
                chart.getYAxis().setLabel(yCategory.getValue());
                choosenAttributes = new Pair<>(xCategory.getValue(), yCategory.getValue());
                update();

            } catch (IllegalArgumentException | NoSuchElementException ile) {
                Popup popup = genErrorPopup(ile.getMessage());
                popup.show(chart.getScene().getWindow());


            }

        });

        addDataBtn.setOnAction(event -> {
            try {
                genererEcran();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void buildWidgets() {
        data = new ArrayList<>();
        categorieColor.put("Unknown", "black");

        chart.setTitle("Scatter Chart");

        chart.getXAxis().setAutoRanging(true);
        chart.getYAxis().setAutoRanging(true);
        chart.setAnimated(false);

        dataManager.attach(this);

        updateCategories();
        Set<String> attributes = dataManager.getAttributes();
        Iterator<String> it = attributes.iterator();
        xCategory.setValue(it.next());
        yCategory.setValue(it.next());
        chart.getXAxis().setLabel(xCategory.getValue());
        chart.getYAxis().setLabel(yCategory.getValue());

        choosenAttributes = new Pair<>(xCategory.getValue(), yCategory.getValue());

        constructChart();

        setChartStyle();
    }

    public static Popup genErrorPopup(String message) {
        Popup popup = new Popup();
        Label label = new Label("Erreur: \n" + message);
        label.setStyle(" -fx-background-color: white; -fx-border-radius: 10; -fx-padding: 10; -fx-border-color: red; -fx-border-width: 2;");
        label.setMinHeight(50);
        label.setMinWidth(200);
        popup.getContent().add(label);
        popup.setAutoHide(true);
        return popup;
    }

    private void update() {
        updateChart();
        setChartStyle();
    }

    private void setChartStyle() {
        for (final XYChart.Series<Number, Number> s : chart.getData()) {
            for (final XYChart.Data<Number, Number> data : s.getData()) {
                Data d = getNode(data);
                Tooltip tooltip = new Tooltip();
                tooltip.setText(d.getCategory() + "\n" + data.getXValue() + " : " + data.getYValue());
                tooltip.setShowDuration(javafx.util.Duration.seconds(10));
                tooltip.setShowDelay(javafx.util.Duration.seconds(0));
                Tooltip.install(data.getNode(), tooltip);
                data.getNode().setOnMouseEntered(event -> {
                    data.getNode().setScaleX(1.5);
                    data.getNode().setScaleY(1.5);
                });
                data.getNode().setOnMouseExited(event -> {
                    data.getNode().setScaleX(1);
                    data.getNode().setScaleY(1);
                });

                setNodeColor(data.getNode(), d.getCategory());
            }
        }
    }

    private Pair<Number, Number> getNodeXY(Data f) {
        Map<String, Number> attributes = f.getattributes();
        return new Pair<>(attributes.get(choosenAttributes.getKey()), attributes.get(choosenAttributes.getValue()));
    }

    private void updateChart() {
        chart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        List<Pair<XYChart.Data<Number, Number>, Data>> tmp = new ArrayList<>(this.data);
        for (Pair<XYChart.Data<Number, Number>, Data> d : tmp) {
            Data f = d.getValue();
            Pair<Number, Number> choosenAttributes = getNodeXY(f);
            XYChart.Data<Number, Number> node = new XYChart.Data<>(choosenAttributes.getKey(), choosenAttributes.getValue());
            series.getData().add(node);
            data.remove(d);
            data.add(new Pair<>(node, f));

        }
        chart.getData().add(series);

    }

    private void constructChart() {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        chart.getData().clear();
        for (Data f : dataManager.getDataList()) {
            Pair<Number, Number> choosenAttributes = getNodeXY(f);
            Coordonnee c = new Coordonnee(choosenAttributes.getKey().doubleValue(), choosenAttributes.getValue().doubleValue());
            XYChart.Data<Number, Number> node = new XYChart.Data<>(choosenAttributes.getKey(), choosenAttributes.getValue());
            data.add(new Pair<>(node, f));
            series.getData().add(node);
        }
        for (Data f : dataManager.getUserDataList()) {
            Pair<Number, Number> choosenAttributes = getNodeXY(f);
            Coordonnee c = new Coordonnee(choosenAttributes.getKey().doubleValue(), choosenAttributes.getValue().doubleValue());
            XYChart.Data<Number, Number> node = new XYChart.Data<>(choosenAttributes.getKey(), choosenAttributes.getValue());
            data.add(new Pair<>(node, f));
            series.getData().add(node);
        }
        chart.getData().add(series);

    }

    private Data getNode(XYChart.Data<Number, Number> data) {
        for (Pair<XYChart.Data<Number, Number>, Data> d : this.data) {
            if (d.getKey() == data) {
                return d.getValue();
            }
        }
        return null;
    }

    private Set<String> getAttributes() {
        return this.dataManager.getAttributes();
    }

    private void updateCategories() {
        xCategory.getItems().clear();
        yCategory.getItems().clear();
        xCategory.getItems().addAll(getAttributes());
        yCategory.getItems().addAll(getAttributes());
    }

    public void setNodeColor(Node node, String category) {
        if (node == null) {
            return;
        }
        if (!categorieColor.containsKey(category)) {
            String color = "rgb(" + (int) (Math.random() * 256) + "," + (int) (Math.random() * 256) + "," + (int) (Math.random() * 256) + ")";
            categorieColor.put(category, color);
        }

        node.setStyle("-fx-background-color: " + categorieColor.get(category) + ";");
    }

    public void addNode(Data data) {
        // TODO
    }

    @Override
    public void update(Observable<Data> ob) {
        constructChart();
        setChartStyle();
    }

    @Override
    public void update(Observable<Data> ob, Data elt) {
        constructChart();
        setChartStyle();
    }

    public void loadNewCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir un fichier CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            DataManager<Data> dataManager = DataManager.instance;
            dataManager.loadData(file.getAbsolutePath());
            buildWidgets();
        }
    }

    public void openNewWindow() throws IOException {
        App app = new App();
        app.start(new Stage());

    }

    public void addData(Map<String,Number> map){
        this.dataManager.addData(map);
    }

    public void genererEcran() throws IOException {
        Stage stage=new Stage();
        Scene scene=new Scene(App.loadFXML("AddPointWindow"));
        stage.setScene(scene);
        stage.show();
    }
}
