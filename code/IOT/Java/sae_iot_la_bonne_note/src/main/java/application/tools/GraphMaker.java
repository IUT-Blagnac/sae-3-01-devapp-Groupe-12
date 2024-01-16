package application.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import application.Main;
import application.model.Data;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Fournit des méthodes pour manipuler et gérer les graphiques JavaFX.
 */
public class GraphMaker {

    /**
     * Met à jour les données du graphique avec une liste de données, un nom de
     * données, et d'autres paramètres.
     *
     * @param _graph      Le graphique à mettre à jour.
     * @param _listData   La liste de données à afficher sur le graphique.
     * @param _dataName   Le nom des données à afficher.
     * @param _dataUnit   L'unité des données.
     * @param _dateFormat Le format de la date pour l'affichage.
     * @param _displayAvg Indique s'il faut afficher la moyenne des données sur le
     *                    graphique.
     */
    public static void updateGraphData(XYChart<String, Number> _graph, List<Data> _listData, String _dataName,
            String _dataUnit, String _dateFormat, boolean _displayAvg) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtilities.transformDateFormat(_dateFormat),
                Locale.FRANCE);
        List<Data> allData = new ArrayList<>(_listData);
        _graph.getData().clear();

        Comparator<Data> comparator = (data1, data2) -> {
            return data1.getDate().compareTo(data2.getDate());
        };
        Collections.sort(allData, comparator);

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
                Double avg = getAverageForDataName(data, _dataName.toLowerCase());

                if (value != null) {
                    XYChart.Data<String, Number> chartData;

                    if (_graph instanceof LineChart) {
                        chartData = new XYChart.Data<>(dateFormat.format(data.getDate()), value);
                    } else if (_graph instanceof BarChart) {
                        chartData = new XYChart.Data<>("", value);

                        if (avg != null) {
                            chartData.setExtraValue(avg);
                        }
                    } else {
                        throw new IllegalArgumentException("Type de graphique non pris en charge");
                    }

                    series.getData().add(chartData);
                    setDataToolTip(chartData, id, dateFormat.format(data.getDate()), value, _dataUnit);
                }
            }

            _graph.getData().add(series);
        }

        if (_graph instanceof BarChart && _displayAvg) {
            for (XYChart.Series<String, Number> series : _graph.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    Double avg = (Double) data.getExtraValue();
                    if (avg != null) {
                        Text label = new Text(String.valueOf(avg));
                        label.setFill(Color.BLACK);
                        label.setFont(Font.font(20));
                        StackPane stackPane = (StackPane) data.getNode();
                        stackPane.getChildren().add(label);
                        StackPane.setAlignment(label, Pos.TOP_CENTER);
                    }
                }
            }
        }
    }

    /**
     * Récupère la moyenne des données spécifiées pour un objet Data.
     *
     * @param data     L'objet Data contenant les données.
     * @param dataName Le nom des données pour lesquelles récupérer la moyenne.
     * @return La moyenne des données spécifiées dans l'objet Data, ou null s'il n'y
     *         en a pas.
     */
    private static Double getAverageForDataName(Data data, String dataName) {
        switch (dataName) {
            case "temperature":
                return data.getAvgTemperature() != null ? data.getAvgTemperature() : null;
            case "humidity":
                return data.getAvgHumidity() != null ? data.getAvgHumidity() : null;
            case "activity":
                return data.getAvgActivity() != null ? data.getAvgActivity() : null;
            case "co2":
                return data.getAvgCo2() != null ? data.getAvgCo2() : null;
            default:
                return null;
        }
    }

    /**
     * Récupère la valeur des données spécifiées pour un objet Data.
     *
     * @param data     L'objet Data contenant les données.
     * @param dataName Le nom des données pour lesquelles récupérer la valeur.
     * @return La valeur des données spécifiées dans l'objet Data, ou null s'il n'y
     *         en a pas.
     */
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

    /**
     * Met à jour la disposition des graphiques dans un conteneur VBox en fonction
     * des paramètres spécifiés.
     *
     * @param _vbox        Le conteneur VBox contenant les graphiques.
     * @param _listGraphs  La liste des graphiques à afficher.
     * @param _temperature Indique si le graphique de température doit être affiché.
     * @param _humidity    Indique si le graphique d'humidité doit être affiché.
     * @param _activity    Indique si le graphique d'activité doit être affiché.
     * @param co2          Indique si le graphique de CO2 doit être affiché.
     */
    public static void updateGraphsPositions(VBox _vbox, List<Chart> _listGraphs,
            boolean _temperature, boolean _humidity, boolean _activity, boolean co2) {
        List<Chart> graphs = new ArrayList<>();
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

            // Add change listeners for width and height of the VBox
            _vbox.widthProperty().addListener((obs, oldWidth, newWidth) -> {
                for (int i = 0; i < graphs.size(); i++) {
                    graphs.get(i).setPrefWidth(newWidth.doubleValue());
                }
            });

            _vbox.heightProperty().addListener((obs, oldHeight, newHeight) -> {
                for (int i = 0; i < graphs.size(); i++) {
                    graphs.get(i).setPrefHeight(newHeight.doubleValue());
                }
            });

            for (int i = 0; i < graphs.size(); i++) {
                Chart graph = graphs.get(i);
                HBox.setMargin(graph, new Insets(5));
                HBox.setHgrow(graph, Priority.ALWAYS);

                vboxForGraphs.getChildren().add(graph);
                VBox.setVgrow(graph, Priority.ALWAYS);
            }

            _vbox.getChildren().add(vboxForGraphs);
        } else {
            Label lab = new Label("Merci de sélectionner une donnée.");
            _vbox.getStyleClass().setAll("labelNoGraphs");
            _vbox.getChildren().addAll(lab);
        }
    }

    /**
     * Affiche un graphique agrandi dans une nouvelle fenêtre.
     *
     * @param _primaryStage   La scène principale de l'application.
     * @param _graph          Le graphique à agrandir.
     * @param _listStages     La liste des fenêtres de graphiques.
     * @param _largeTxtSearch Le champ de recherche pour le graphique agrandi.
     * @param _isHistory      S'il d'agit d'un graphique de l'historique ou non, utilisé pour le titre.
     * @return Le graphique agrandi affiché dans une nouvelle fenêtre.
     */
    public static XYChart<String, Number> displayLargeGraph(Stage _primaryStage, XYChart<String, Number> _graph,
            List<Stage> _listStages, TextField _largeTxtSearch, boolean _isHistory) {
        Stage largeGraphStage = new Stage();
        StageManagement.manageCenteringStage(_primaryStage, largeGraphStage);
        largeGraphStage.setWidth(_primaryStage.getWidth() / 2);
        largeGraphStage.setHeight(_primaryStage.getHeight() / 1.5);
        largeGraphStage.setMinWidth(800);
        largeGraphStage.setMinHeight(600);
        largeGraphStage.setTitle((_isHistory ? "Historique : " : "Temps Réel : ") + _graph.getTitle());

        XYChart<String, Number> largeGraph;
        if (_graph instanceof BarChart) {
            largeGraphStage.getIcons().add(new Image("/application/images/bar-chart_icon.png"));
            largeGraph = new BarChart<>(new CategoryAxis(), new NumberAxis());
        } else if (_graph instanceof LineChart) {
            largeGraphStage.getIcons().add(new Image("/application/images/line-chart_icon.png"));
            largeGraph = new LineChart<>(new CategoryAxis(), new NumberAxis());
        } else {
            throw new IllegalArgumentException("Type de graphique non pris en charge");
        }
        _listStages.add(largeGraphStage);

        largeGraph.setTitle(_graph.getTitle());

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

    /**
     * Configure l'affichage d'une infobulle au survol d'un point de données sur un
     * graphique.
     *
     * @param _chartData Le point de données du graphique sur lequel afficher
     *                   l'infobulle.
     * @param _id        L'identifiant des données.
     * @param _date      La date associée aux données.
     * @param _data      La valeur des données.
     * @param _dataUnit  L'unité des données.
     */
    private static void setDataToolTip(XYChart.Data<String, Number> _chartData, String _id, String _date, double _data,
            String _dataUnit) {
        _chartData.nodeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Tooltip tooltipTopic = new Tooltip(
                        _id + " : " + String.valueOf(_data) + _dataUnit
                                + (_date.equals("") ? "" : " (" + _date + ")"));
                tooltipTopic.setStyle("-fx-font-size: 18px;");
                Tooltip.install(newValue, tooltipTopic);
                newValue.setOnMouseEntered(
                        event -> tooltipTopic.show(newValue, event.getScreenX(), event.getScreenY() + 10));
                newValue.setOnMouseExited(event -> tooltipTopic.hide());
            }
        });
    }
}