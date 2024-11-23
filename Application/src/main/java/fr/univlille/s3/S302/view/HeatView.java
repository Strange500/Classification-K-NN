package fr.univlille.s3.S302.view;

import fr.univlille.s3.S302.model.DataManager;
import fr.univlille.s3.S302.utils.Distance;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HeatView {

    // (!=0) 1 is better higher is worse
    public static int quality = 7;


    private final Canvas canvas;
    private final ScatterChart<Number, Number> scatterChart;
    private final String xAttribute;
    private final String yAttribute;
    private final Map<String, List<Double>> colorMap;
    private boolean active = false;
    private Distance distance ;

    public HeatView(Canvas cv, ScatterChart<Number, Number> scatterChart, String xAttribute, String yAttribute, Map<String, String> colorMap, Distance distance) {
        canvas = cv;
        this.scatterChart = scatterChart;
        this.xAttribute = xAttribute;
        this.yAttribute = yAttribute;
        Map<String, List<Double>> tmp = new HashMap<>();
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            tmp.put(entry.getKey(), getCategorieRgb(entry.getValue()));
        }
        this.colorMap = tmp;
        this.distance = distance;

    }


    public void toggle() {
        active = !active;
        if (active) {
            scatterChart.setOpacity(0.7);
            update();
        }else {
            clear();
            scatterChart.setOpacity(1);
        }
    }

    private void clear () {
        canvas.getGraphicsContext2D().clearRect(-100, -100, canvas.getWidth()+200, canvas.getHeight()+200);

    }
    public void update() {
            if (active) {
                clear();
                draw();
            }
    }

    private static List<Double> getCategorieRgb(String color) {
        if (color == null) {
            return List.of(0.0, 0.0, 0.0);
        }
        // color is a string in the form "rgb(x, y, z)"
        if (!color.startsWith("rgb(") || !color.endsWith(")")) {
            return List.of(0.0, 0.0, 0.0);
        }
        String[] rgb = color.substring(4, color.length() - 1).split(",");
        return List.of(Double.parseDouble(rgb[0]), Double.parseDouble(rgb[1]), Double.parseDouble(rgb[2]));
    }


    private double getMaxX() {
        return ((NumberAxis)scatterChart.getXAxis()).getUpperBound();
    }

    private double getMaxY() {
        return ((NumberAxis)scatterChart.getYAxis()).getUpperBound();
    }


    private void draw() {
        Node chartArea = scatterChart.lookup(".chart-plot-background");
        Bounds boundsChartArea = chartArea.localToParent(chartArea.getBoundsInLocal());
        double xOffSet = boundsChartArea.getMinX();
        double yOffset = boundsChartArea.getMinY();
        double maxX = getMaxX();
        double stepX = (maxX / boundsChartArea.getWidth()) * quality;
        double stepY = (getMaxY() / boundsChartArea.getHeight()) * quality;
        Map<String, Number> data =new HashMap<>();
        for (double y = 0; y < getMaxY(); y += stepY) {
            for (double x = 0; x < maxX; x += stepX) {
                data.put(xAttribute, x);
                data.put(yAttribute, y);
                String cat = DataManager.getInstance().guessCategory(data, distance);
                List<Double> tmp = colorMap.get(cat);
                canvas.getGraphicsContext2D().setFill(Color.rgb(tmp.get(0).intValue(), tmp.get(1).intValue(), tmp.get(2).intValue()));
                canvas.getGraphicsContext2D().fillRect(scatterChart.getXAxis().getDisplayPosition(x) + xOffSet,
                        scatterChart.getYAxis().getDisplayPosition(y) + yOffset , 10,10);
                data.clear();
            }
        }
    }

    public boolean isActive() {
        return  active;
    }
}
