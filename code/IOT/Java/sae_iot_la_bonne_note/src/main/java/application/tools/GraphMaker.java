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
import javafx.scene.Node;
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
import javafx.util.Duration;

/**
 * Fournit des méthodes pour manipuler et gérer les graphiques JavaFX.
 */
public class GraphMaker {

    private static Double maxTemperature;
    private static Double maxHumidity;
    private static Double maxActivity;
    private static Double maxCo2;

    /**
     * Définit les valeurs maximales pour les différents paramètres (température,
     * humidité, activité et CO2).
     *
     * @param _maxTemperature La valeur maximale autorisée pour la température.
     * @param _maxHumidity    La valeur maximale autorisée pour l'humidité.
     * @param _maxActivity    La valeur maximale autorisée pour l'activité.
     * @param _maxCo2         La valeur maximale autorisée pour le CO2.
     */
    public static void setMaxValues(Double _maxTemperature, Double _maxHumidity, Double _maxActivity, Double _maxCo2) {
        maxTemperature = _maxTemperature;
        maxHumidity = _maxHumidity;
        maxActivity = _maxActivity;
        maxCo2 = _maxCo2;
    }

    /**
     * Met à jour les données du graphique avec une liste de données, un nom de
     * données, et d'autres paramètres.
     *
     * @param _graph              Le graphique à mettre à jour.
     * @param _listData           La liste de données à afficher sur le graphique.
     * @param _dataName           Le nom des données à afficher.
     * @param _dataUnit           L'unité des données.
     * @param _dateFormat         Le format de la date pour l'affichage.
     * @param _displayAvg         Indique s'il faut afficher la moyenne des données
     *                            sur le graphique.
     * @param _displayGraphAlerts Indique s'il faut afficher les alertes sur le
     *                            graphique.
     */
    public static void updateGraphData(XYChart<String, Number> _graph, List<Data> _listData, String _dataName,
            String _dataUnit, String _dateFormat, boolean _displayAvg, boolean _displayGraphAlerts) {
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

                    chartData = new XYChart.Data<>(
                            _graph instanceof LineChart ? dateFormat.format(data.getDate()) : "", value);

                    if (_graph instanceof BarChart && avg != null) {

                        StackPane stackPane = new StackPane(); // Créer un StackPane
                        chartData.setNode(stackPane); // Définir le StackPane comme le nœud associé à chartData

                        if (_displayAvg) {
                            // Affiche la moyenne pour ce type de données
                            Text label = new Text(String.valueOf(avg));
                            label.setFill(Color.BLACK);
                            label.setFont(Font.font(14));
                            stackPane.getChildren().add(label);
                            StackPane.setAlignment(label, Pos.TOP_CENTER);
                        }
                        boolean limitReached = false;
                        if (_displayGraphAlerts) {
                            // Affiche une icône d'alerte si la valeur est supérieur au seuil maximale
                            // défini pour ce type de données
                            limitReached = checkIfValueReachedLimit(data, _dataName.toLowerCase());
                            if (limitReached) {
                                ImageView alertIcon = new ImageView(new Image("/application/images/alert_data_icon.png",
                                        20, 20, false, false));
                                stackPane.getChildren().add(alertIcon);
                                StackPane.setAlignment(alertIcon, Pos.TOP_CENTER);
                                StackPane.setMargin(alertIcon, new Insets(50, 0, 0, 0));
                            }
                        }
                        setDataToolTip((StackPane) chartData.getNode(), null, id, dateFormat.format(data.getDate()),
                                value, _dataUnit,
                                limitReached ? "\nLe seuil maximal de " + getMaxForDataName(_dataName) + _dataUnit
                                        + " a été dépassé, valeur : " + value + _dataUnit
                                        : "");
                    } else {
                        setDataToolTip(null, chartData, id, dateFormat.format(data.getDate()), value,
                                _dataUnit, null);
                    }
                    series.getData().add(chartData);
                }
            }

            _graph.getData().add(series);
        }
    }

    /**
     * Obtient la valeur maximale associée à un nom de données spécifique.
     *
     * @param _dataName Le nom de la valeur de données (par exemple, "temperature",
     *                  "humidity", "activity", "co2").
     * @return La valeur maximale correspondante, ou {@code null} si le nom de
     *         données n'est pas reconnu.
     */
    private static Double getMaxForDataName(String _dataName) {
        switch (_dataName) {
            case "temperature":
                return maxTemperature;
            case "humidity":
                return maxHumidity;
            case "activity":
                return maxActivity;
            case "co2":
                return maxCo2;
            default:
                return null;
        }
    }

    /**
     * Vérifie si une valeur spécifique de données a atteint sa limite définie.
     *
     * @param _data     L'objet de données contenant la valeur pertinente.
     * @param _dataName Le nom de la valeur de données à vérifier (par exemple,
     *                  "temperature", "humidity", "activity", "co2").
     * @return {@code true} si la valeur a atteint sa limite, {@code false} sinon.
     */
    private static Boolean checkIfValueReachedLimit(Data _data, String _dataName) {
        switch (_dataName) {
            case "temperature":
                return _data.getTemperature() != null && maxTemperature != null
                        && _data.getTemperature() > maxTemperature;
            case "humidity":
                return _data.getHumidity() != null && maxHumidity != null && _data.getHumidity() > maxHumidity;
            case "activity":
                return _data.getActivity() != null && maxActivity != null && _data.getActivity() > maxActivity;
            case "co2":
                return _data.getCo2() != null && maxCo2 != null && _data.getCo2() > maxCo2;
            default:
                return false;
        }
    }

    /**
     * Récupère la moyenne des données spécifiées pour un objet Data.
     *
     * @param _data     L'objet Data contenant les données.
     * @param _dataName Le nom des données pour lesquelles récupérer la moyenne.
     * @return La moyenne des données spécifiées dans l'objet Data, ou null s'il n'y
     *         en a pas.
     */
    private static Double getAverageForDataName(Data _data, String _dataName) {
        switch (_dataName) {
            case "temperature":
                return _data.getAvgTemperature() != null ? _data.getAvgTemperature() : null;
            case "humidity":
                return _data.getAvgHumidity() != null ? _data.getAvgHumidity() : null;
            case "activity":
                return _data.getAvgActivity() != null ? _data.getAvgActivity() : null;
            case "co2":
                return _data.getAvgCo2() != null ? _data.getAvgCo2() : null;
            default:
                return null;
        }
    }

    /**
     * Récupère la valeur des données spécifiées pour un objet Data.
     *
     * @param _data     L'objet Data contenant les données.
     * @param _dataName Le nom des données pour lesquelles récupérer la valeur.
     * @return La valeur des données spécifiées dans l'objet Data, ou null s'il n'y
     *         en a pas.
     */
    private static Double getValueForDataName(Data _data, String _dataName) {
        switch (_dataName) {
            case "temperature":
                return _data.getTemperature() != null ? _data.getTemperature() : null;
            case "humidity":
                return _data.getHumidity() != null ? _data.getHumidity() : null;
            case "activity":
                return _data.getActivity() != null ? _data.getActivity() : null;
            case "co2":
                return _data.getCo2() != null ? _data.getCo2() : null;
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
     * @param _co2         Indique si le graphique de CO2 doit être affiché.
     */
    public static void updateGraphsPositions(VBox _vbox, List<Chart> _listGraphs,
            boolean _temperature, boolean _humidity, boolean _activity, boolean _co2) {
        List<Chart> graphs = new ArrayList<>();
        if (_temperature)
            graphs.add(_listGraphs.get(0));
        if (_humidity)
            graphs.add(_listGraphs.get(1));
        if (_activity)
            graphs.add(_listGraphs.get(2));
        if (_co2)
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
     * @param _isHistory      S'il d'agit d'un graphique de l'historique ou non,
     *                        utilisé pour le titre.
     * @return Le graphique agrandi affiché dans une nouvelle fenêtre.
     */
    public static XYChart<String, Number> displayLargeGraph(Stage _primaryStage, XYChart<String, Number> _graph,
            List<Stage> _listStages, TextField _largeTxtSearch, boolean _isHistory) {
        Stage largeGraphStage = new Stage();
        StageManagement.manageCenteringStage(_primaryStage, largeGraphStage);
        largeGraphStage.setWidth(_primaryStage.getWidth() / 2);
        largeGraphStage.setHeight(_primaryStage.getHeight() / 1.5);
        largeGraphStage.setMinWidth(600);
        largeGraphStage.setMinHeight(400);
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
        largeGraphStage.requestFocus();
        return largeGraph;
    }

    /**
     * Configure l'affichage d'une infobulle au survol d'un point de données sur un
     * graphique.
     *
     * @param _node      Le Node sur lequel afficher l'infobulle.
     * @param _chartData Le point de données du graphique sur lequel afficher
     *                   l'infobulle.
     * @param _id        L'identifiant des données.
     * @param _date      La date associée aux données.
     * @param _data      La valeur des données.
     * @param _dataUnit  L'unité des données.
     */
    private static void setDataToolTip(Node _node, XYChart.Data<String, Number> _chartData, String _id, String _date,
            double _data, String _dataUnit, String _alertMsg) {
        Tooltip tooltipTopic = new Tooltip(
                _id + " : " + String.valueOf(_data) + _dataUnit
                        + (_date.equals("") ? "" : " (" + _date + ")") + (_alertMsg != null ? _alertMsg : ""));
        tooltipTopic.setStyle("-fx-font-size: 18px;");

        if (_node != null) {
            Style.setToolTip(tooltipTopic, 18, Duration.ZERO, Duration.INDEFINITE);
            _node.setOnMouseEntered(event -> {
                tooltipTopic.show(_node, event.getScreenX(), event.getScreenY() + 10);
            });
            _node.setOnMouseExited(event -> tooltipTopic.hide());
        } else if (_chartData != null) {
            _chartData.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Tooltip.install(newValue, tooltipTopic);
                    newValue.setOnMouseEntered(event -> {
                        tooltipTopic.show(newValue, event.getScreenX(), event.getScreenY() + 10);
                    });
                    newValue.setOnMouseExited(event -> tooltipTopic.hide());
                }
            });
        }
    }
}