package fr.univlille.s3.S302.view;

import fr.univlille.s3.S302.model.Data;
import fr.univlille.s3.S302.model.DataManager;
import javafx.scene.Node;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.util.Pair;

import java.util.*;

public class Chart {

    private final ScatterChart<Number, Number> chart;

    public final Map<String, String> categorieColor = new HashMap<>();


    List<Pair<XYChart.Data<Number, Number>, Data>> data;

    DataManager dataManager = DataManager.getInstance();


    public Chart(ScatterChart<Number, Number> chart) {
        this.chart = chart;
        this.data = new ArrayList<>();
        chart.getXAxis().setAutoRanging(true);
        chart.getYAxis().setAutoRanging(true);
        chart.setAnimated(false);
        //chart.legendVisibleProperty().setValue(false);
    }

    public void recreateChart(List<Data> dataList, List<Data> userDataList, Pair<String, String> choosenAttributes) {
        categorieColor.clear();
        categorieColor.put("Unknown", "black");
        update(dataList, userDataList, choosenAttributes);
    }

    public void update(List<Data> dataList, List<Data> userDataList, Pair<String, String> choosenAttributes) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        chart.getData().clear();
        this.data.clear();
        for (Data f : dataList) {
            addPoint(f, series, choosenAttributes);
        }
        for (Data f : dataList) {
            if (f.getAttributes().containsKey(choosenAttributes.getKey())
                    && f.getAttributes().containsKey(choosenAttributes.getValue())) {
                addPoint(f, series , choosenAttributes);
            }

        }
        for (Data f : userDataList) {
            if (f.getAttributes().containsKey(choosenAttributes.getKey())
                    && f.getAttributes().containsKey(choosenAttributes.getValue())) {
                addPoint(f, series , choosenAttributes);
            }

        }
        chart.getData().add(series);
        setChartStyle();
    }


    private void addPoint(Data f, XYChart.Series<Number, Number> series, Pair<String, String> choosenAttributes) {
        Pair<Number, Number> valueChoosenAttr = getNodeXY(f, choosenAttributes);
        XYChart.Data<Number, Number> node = new XYChart.Data<>(valueChoosenAttr.getKey(),
                valueChoosenAttr.getValue());
        data.add(new Pair<>(node, f));
        series.getData().add(node);
    }

    private Pair<Number, Number> getNodeXY(Data f, Pair<String, String> choosenAttributes) {
        Map<String, Number> attributes = f.getAttributes();
        return new Pair<>(attributes.get(choosenAttributes.getKey()), attributes.get(choosenAttributes.getValue()));
    }

    private void setChartStyle() {
        for (final XYChart.Series<Number, Number> s : chart.getData()) {
            for (final XYChart.Data<Number, Number> data : s.getData()) {
                Data d = getNode(data);
                attachInfoTooltip(data, Objects.requireNonNull(d));
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

    private static void attachInfoTooltip(XYChart.Data<Number, Number> data, Data d) {
        Tooltip tooltip = new Tooltip();
        tooltip.setText(d.getCategoryField() + ":" + d.getCategory() + "\n" + data.getXValue() + " : " + data.getYValue());
        tooltip.setShowDuration(javafx.util.Duration.seconds(10));
        tooltip.setShowDelay(javafx.util.Duration.seconds(0));
        Tooltip.install(data.getNode(), tooltip);
    }

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


    private Data getNode(XYChart.Data<Number, Number> data) {
        for (Pair<XYChart.Data<Number, Number>, Data> d : this.data) {
            if (d.getKey() == data) {
                return d.getValue();
            }
        }
        return null;
    }

    private static void displaySquare(XYChart.Data<Number, Number> data) {
        String st = data.getNode().getStyle();
        data.getNode().setStyle(st + "-fx-background-radius: 0;");
    }
}
