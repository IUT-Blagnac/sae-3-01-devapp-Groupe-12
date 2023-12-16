package application.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import application.Main;
import application.model.Data;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GraphUtilies {

    public static void updateGraphData(LineChart<String, Number> _graph, List<Data> _listData, String _dataName,
            String _dataUnit, String _dateFormat) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtilities.transformDateFormat(_dateFormat),
                Locale.FRANCE);
        List<Data> allData = new ArrayList<>(_listData);
        _graph.getData().clear();

        Map<String, List<Data>> dataById = new HashMap<>();

        for (Data data : allData) {
            String id = data.getId();
            if (!dataById.containsKey(id)) {
                dataById.put(id, new ArrayList<>());
            }
            dataById.get(id).add(data);
        }

        for (Map.Entry<String, List<Data>> entry : dataById.entrySet()) {
            String id = entry.getKey();
            List<Data> dataList = entry.getValue();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(id);

            for (Data data : dataList) {
                Double value = getValueForDataName(data, _dataName.toLowerCase());
                if (value != null) {
                    XYChart.Data<String, Number> chartData = new XYChart.Data<>(dateFormat.format(data.getDate()),
                            value);
                    series.getData().add(chartData);
                    setDataToolTip(chartData, data.getId(), dateFormat.format(data.getDate()), value, _dataUnit);
                }
            }
            _graph.setLegendVisible(true);
            _graph.getData().add(series);
        }
    }

    public static void updateGraphsPositions(VBox _vbox, List<LineChart<String, Number>> _listGraphs,
            boolean _temperature, boolean _humidity, boolean _activity, boolean co2) {
        List<LineChart<String, Number>> graphs = new ArrayList<>();
        if (_temperature)
            graphs.add(_listGraphs.get(0));
        if (_humidity)
            graphs.add(_listGraphs.get(1));
        if (_activity)
            graphs.add(_listGraphs.get(2));
        if (co2)
            graphs.add(_listGraphs.get(3));

        _vbox.getChildren().clear();

        if (!graphs.isEmpty()) {
            _vbox.getStyleClass().setAll("vbox");
            HBox vboxForGraphs = new HBox();
            vboxForGraphs.setFillHeight(true);

            for (int i = 0; i < graphs.size(); i++) {

                LineChart<String, Number> graph = graphs.get(i);
                HBox.setMargin(graph, new Insets(5));
                HBox.setHgrow(graph, Priority.ALWAYS);

                if (graphs.size() <= 2) {
                    graphs.get(i).setPrefSize(_vbox.getWidth(), _vbox.getHeight());
                } else {
                    graph.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
                }
                if (i < 2) {
                    vboxForGraphs.getChildren().add(graph);
                } else {
                    if (graphs.size() == 4 && i == 2) {
                        VBox vbox = new VBox();
                        VBox.setMargin(graph, new Insets(5));
                        VBox.setVgrow(graph, Priority.ALWAYS);
                        vbox.getChildren().add(graph);

                        HBox hbox = new HBox(vbox, graphs.get(3));
                        HBox.setMargin(graphs.get(3), new Insets(5));
                        HBox.setHgrow(graphs.get(3), Priority.ALWAYS);

                        _vbox.getChildren().addAll(vboxForGraphs, hbox);
                        return;
                    } else if (graphs.size() == 3 && i == 2) {
                        VBox vbox = new VBox();
                        VBox.setMargin(graph, new Insets(5));
                        VBox.setVgrow(graph, Priority.ALWAYS);
                        vbox.getChildren().add(graph);

                        _vbox.getChildren().addAll(vboxForGraphs, vbox);
                        return;
                    }
                }
            }
            _vbox.getChildren().add(vboxForGraphs);
        } else {
            Label lab = new Label("Merci de seléctionner une donnée.");
            _vbox.getStyleClass().setAll("labelNoGraphs");
            _vbox.getChildren().addAll(lab);
        }
    }

    public static LineChart<String, Number> displayLargeGraph(Stage _primaryStage, LineChart<String, Number> _chart,
            List<Stage> _listStages,
            TextField _largeTxtSearch, String _dataUnit) {
        Stage largeGraphStage = new Stage();
        StageManagement.manageCenteringStage(_primaryStage, largeGraphStage);
        largeGraphStage.setMinWidth(400);
        largeGraphStage.setMinHeight(400);
        _listStages.add(largeGraphStage);

        LineChart<String, Number> largeGraph = new LineChart<>(new CategoryAxis(), new NumberAxis());
        largeGraph.setTitle(_chart.getTitle());

        ObservableList<XYChart.Series<String, Number>> originalChartData = _chart.getData();
        if (originalChartData != null && !originalChartData.isEmpty()) {
            for (XYChart.Series<String, Number> series : originalChartData) {
                XYChart.Series<String, Number> newSeries = new XYChart.Series<>();
                newSeries.setName(series.getName());
                for (XYChart.Data<String, Number> data : series.getData()) {
                    XYChart.Data<String, Number> newData = new XYChart.Data<>(data.getXValue(), data.getYValue());
                    newSeries.getData().add(newData);
                    setDataToolTip(newData, series.getName(), data.getXValue(),
                            ((Number) data.getYValue()).doubleValue(), _dataUnit);
                }
                largeGraph.getData().add(newSeries);
            }
        }
        BorderPane layout = new BorderPane();

        Image searchIconImage = new Image("/application/images/search_icon.png");
        ImageView searchIcon = new ImageView(searchIconImage);
        searchIcon.setFitWidth(45);
        searchIcon.setFitHeight(45);

        StackPane stackPane = new StackPane(_largeTxtSearch, searchIcon);
        stackPane.setAlignment(Pos.CENTER_RIGHT);
        StackPane.setMargin(searchIcon, new Insets(0, 10, 0, 0));

        layout.setCenter(largeGraph);
        layout.setTop(stackPane);

        BorderPane.setMargin(largeGraph, new Insets(10));
        BorderPane.setMargin(stackPane, new Insets(10));
        Scene largeGraphScene = new Scene(layout, 800, 600);

        largeGraphScene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
        largeGraph.getStyleClass().setAll("large-chart");
        _largeTxtSearch.getStyleClass().setAll("txtArea");
        largeGraph.setLegendVisible(true);

        largeGraphStage.setScene(largeGraphScene);
        largeGraphStage.show();
        return largeGraph;
    }

    private static Double getValueForDataName(Data data, String dataName) {
        switch (dataName) {
            case "temperature":
                return data.getTemperature() != null ? data.getTemperature() : null;
            case "humidity":
                return data.getHumidity() != null ? data.getHumidity() : null;
            case "activity":
                return data.getActivity() != null ? data.getActivity() : null;
            case "co2":
                return data.getCo2() != null ? data.getCo2() : null;
            default:
                return null;
        }
    }

    private static void setDataToolTip(XYChart.Data<String, Number> _chartData, String _id, String _date, double _data,
            String _dataUnit) {
        _chartData.nodeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Tooltip tooltipTopic = new Tooltip(
                        _id + " : " + String.valueOf(_data) + _dataUnit + " (" + _date + ")");
                tooltipTopic.setStyle("-fx-font-size: 18px;");
                Tooltip.install(newValue, tooltipTopic);
                newValue.setOnMouseEntered(
                        event -> tooltipTopic.show(newValue, event.getScreenX(), event.getScreenY() + 10));
                newValue.setOnMouseExited(event -> tooltipTopic.hide());
            }
        });
    }
}