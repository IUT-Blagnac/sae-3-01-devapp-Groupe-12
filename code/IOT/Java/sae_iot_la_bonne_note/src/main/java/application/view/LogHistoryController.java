package application.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import application.control.Configuration;
import application.control.LogHistory;
import application.control.MainMenu;
import application.control.WharehouseMonitor;
import application.model.Alert;
import application.model.Data;
import application.tools.AlertUtilities;
import application.tools.Animations;
import application.tools.DateUtilities;
import application.tools.GraphUtilies;
import application.tools.JsonUtilities;
import application.tools.ListViewUtilies;
import application.tools.PythonProcessUtilities;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class LogHistoryController {

    // Référence à la classe de l'historique
    private LogHistory logHistory;

    // Référence au stage de la fenêtre principale
    private Stage primaryStage;

    @FXML
    private BorderPane borderpane;

    @FXML
    private Button buttMenu;

    @FXML
    private Button buttCheckWhareHouse;

    @FXML
    private Button buttCheckHistory;

    @FXML
    private Button buttConfiguration;

    @FXML
    private Button buttListView;

    @FXML
    private Button buttGraphView;

    @FXML
    private Button buttCheckLogs;

    @FXML
    private Button buttCheckAlerts;

    @FXML
    private CheckBox cbTemperature;

    @FXML
    private CheckBox cbHumidity;

    @FXML
    private CheckBox cbActivity;

    @FXML
    private CheckBox cbCo2;

    @FXML
    private TextField txtSearch;
    private String currentSearch = "";

    @FXML
    private ImageView imgInfoSearch;

    @FXML
    private ImageView imgSearchIcon;

    @FXML
    private ComboBox<String> comboBoxRooms;

    @FXML
    private ComboBox<String> comboBoxDateFormat;

    private LineChart<String, Number> graphTemperature = new LineChart<>(new CategoryAxis(), new NumberAxis());
    private LineChart<String, Number> graphHumidity = new LineChart<>(new CategoryAxis(), new NumberAxis());
    private LineChart<String, Number> graphActivity = new LineChart<>(new CategoryAxis(), new NumberAxis());
    private LineChart<String, Number> graphCo2 = new LineChart<>(new CategoryAxis(), new NumberAxis());

    private String largeGraphViewDataName = "";
    private String largeGraphViewDataUnit = "";

    private VBox vboxGraphView = new VBox();

    private ListView<String> lvHistory = new ListView<>();
    private ObservableList<String> obsList = FXCollections.observableArrayList();

    private ArrayList<Data> listAllRoomsDatas = new ArrayList<>();
    private ArrayList<Data> listSearchedDatas = new ArrayList<>();

    private ArrayList<Alert> listAllRoomsAlerts = new ArrayList<>();
    private ArrayList<Alert> listSearchedAlerts = new ArrayList<>();

    private List<Stage> listLargeGraphsStages = new ArrayList<>();
    private ArrayList<List<Data>> listSearchedDatasByLargeGraph = new ArrayList<>();
    private List<LineChart<String, Number>> listLargeGraphs = new ArrayList<>();

    private final Tooltip tooltipImgSearch = new Tooltip(
            "Rentrer le nom de la salle à vérifier, choix multiples possible en séparant les salles par ','.\n\"Exemple : \"B103,E006,Amphi\".");

    private Thread loadDataThread;

    /**
     * Initialise le contrôleur de vue LogHistoryController.
     *
     * @param _mainMenu     L'instance du menu principal.
     * @param _primaryStage La scène principale associée au contrôleur.
     */
    public void initContext(LogHistory _mainMenu, Stage _primaryStage) {
        this.logHistory = _mainMenu;
        this.primaryStage = _primaryStage;

        if (!PythonProcessUtilities.isPythonRunning()) {
            PythonProcessUtilities.startPythonThread(_primaryStage);
        }

        initViewElements();
        showLogs();
        initializeCheckboxes(cbTemperature);
        initializeCheckboxes(cbHumidity);
        initializeCheckboxes(cbActivity);
        initializeCheckboxes(cbCo2);
        ListViewUtilies.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                cbActivity.isSelected(), cbCo2.isSelected());
        setGraphView();
    }

    private void initializeCheckboxes(CheckBox _cb) {
        _cb.setOnMouseEntered(e -> {
            _cb.getScene().setCursor(Cursor.HAND);
        });
        _cb.setOnMouseExited(e -> {
            _cb.getScene().setCursor(Cursor.DEFAULT);
        });
        _cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (buttGraphView.isDisabled()) {
                GraphUtilies.updateGraphsPositions(
                        vboxGraphView, Arrays.asList(graphTemperature, graphHumidity, graphActivity, graphCo2),
                        cbTemperature.isSelected(), cbHumidity.isSelected(), cbActivity.isSelected(),
                        cbCo2.isSelected());
            } else {
                updateSceneByView();
            }
        });
    }

    private void updateSceneByView() {
        if (buttCheckAlerts.isDisabled()) {
            updateAlertsHistory();
        } else {
            updateDatasHistory();
        }
        if (buttGraphView.isDisabled()) {
            updateAllGraphs();
        } else {
            ListViewUtilies.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                    cbActivity.isSelected(), cbCo2.isSelected());
            lvHistory.setItems(obsList);
        }
    }

    private void initViewElements() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        Animations.setAnimatedButton(buttMenu, 1.1, 1, 100);
        Animations.setAnimatedButton(buttCheckWhareHouse, 1.1, 1, 100);
        Animations.setAnimatedButton(buttConfiguration, 1.1, 1, 100);
        Animations.setSelectedMenuAnimation(buttCheckHistory, 0.5, 0.8, 1000);

        tooltipImgSearch.setStyle("-fx-font-size: 18px;");
        tooltipImgSearch.setShowDelay(Duration.ZERO);
        tooltipImgSearch.setShowDuration(Duration.INDEFINITE);

        Tooltip.install(imgInfoSearch, tooltipImgSearch);

        comboBoxDateFormat.getItems().addAll(
                "JJ",
                "MM",
                "AAAA",
                "MM/AAAA",
                "JJ/MM",
                "JJ/MM à hh",
                "JJ/MM à hh:mm",
                "JJ/MM à hh:mm:ss",
                "JJ/MM/AAAA",
                "JJ/MM/AAAA à hh",
                "JJ/MM/AAAA à hh:mm",
                "JJ/MM/AAAA à hh:mm:ss");
        comboBoxDateFormat.setValue("JJ/MM à hh:mm");
        comboBoxRooms.setValue("Toutes");
        comboBoxDateFormat.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateSceneByView();
            }
        });
        comboBoxRooms.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.equals("Toutes")) {
                    currentSearch = "";
                    txtSearch.setText("");
                } else {
                    currentSearch = newValue;
                    txtSearch.setText(newValue);
                }
                updateSceneByView();
            }
        });
        initTxtSearch(txtSearch, null, null);
    }

    private void initLoadDataThread(boolean _loadFromFile, boolean _isAlert) {
        loadDataThread = new Thread(() -> {
            Platform.runLater(() -> {
                if (_loadFromFile) {
                    JsonUtilities.updateHistoryFromFile(primaryStage, false, _isAlert, obsList,
                            listAllRoomsDatas, null, comboBoxRooms);
                }
                updateSceneByView();
                comboBoxDateFormat.setValue(comboBoxDateFormat.getValue());
                // comboBoxDateFormat.setValue("JJ/MM à hh:mm");
                // checkAlertForLastData();
            });
        });
    }

    private void setSceneForView(boolean _isGraphView) {
        if (_isGraphView) {
            vboxGraphView.getStyleClass().add("vbox");
            borderpane.setCenter(vboxGraphView);
            BorderPane.setMargin(vboxGraphView, new Insets(10));
        } else {
            if (lvHistory == null) {
                lvHistory = new ListView<>();
            }
            borderpane.setCenter(lvHistory);
            BorderPane.setMargin(lvHistory, new Insets(10));
        }
    }

    @FXML
    private void setGraphView() {
        buttListView.setDisable(false);
        buttGraphView.setDisable(true);
        buttCheckAlerts.setDisable(false);
        buttCheckLogs.setDisable(true);
        setSceneForView(false);
        initGraph(graphTemperature, "Température (°c)", "temperature", "°c", 0, 0);
        initGraph(graphHumidity, "Humidité (%)", "humidity", "%", 1, 0);
        initGraph(graphActivity, "Activité", "activity", "", 0, 1);
        initGraph(graphCo2, "Co2 (ppm)", "co2", "ppm", 1, 1);
        setSceneForView(true);
        GraphUtilies.updateGraphsPositions(
                vboxGraphView, Arrays.asList(graphTemperature, graphHumidity, graphActivity, graphCo2),
                cbTemperature.isSelected(), cbHumidity.isSelected(), cbActivity.isSelected(),
                cbCo2.isSelected());
        updateAllGraphs();
        updateSceneByView();
    }

    @FXML
    private void setListView() {
        buttListView.setDisable(true);
        buttGraphView.setDisable(false);
        setSceneForView(false);
    }

    @FXML
    private void showLogs() {
        if (buttGraphView.isDisabled()) {
            setListView();
        }
        buttCheckLogs.setDisable(true);
        buttCheckAlerts.setDisable(false);
        if (listAllRoomsDatas.size() == 0) {
            initLoadDataThread(true, false);
            loadDataThread.start();
        }
        updateSceneByView();
    }

    @FXML
    private void showAlerts() {
        if (buttGraphView.isDisabled()) {
            setListView();
        }
        buttCheckLogs.setDisable(false);
        buttCheckAlerts.setDisable(true);
        if (listAllRoomsAlerts.size() == 0) {
            initLoadDataThread(true, true);
            loadDataThread.start();
            // JsonUtilities.updateHistoryFromFile(primaryStage, false, true, obsList,
            // listAllRoomsDatas,
            // listAllRoomsAlerts, comboBoxRooms);
        }
        updateSceneByView();
    }

    private void initGraph(LineChart<String, Number> _graph, String _tittle, String _dataName, String _dataUnit,
            int _gridPaneX, int _gridPaneY) {
        _graph.setTitle(_tittle);
        GridPane.setMargin(_graph, new Insets(5, 5, 5, 5));
        _graph.setOnMouseClicked(event -> {
            largeGraphViewDataName = _dataName;
            largeGraphViewDataUnit = _dataUnit;
            TextField largeTxtSearch = createLargeTxtSearch();
            ArrayList<Data> searchedDatasByGraph = new ArrayList<>(listSearchedDatas);
            listSearchedDatasByLargeGraph.add(searchedDatasByGraph);
            LineChart<String, Number> largeGraph = GraphUtilies.displayLargeGraph(primaryStage, _graph,
                    listLargeGraphsStages,
                    largeTxtSearch, _dataUnit);
            initTxtSearch(largeTxtSearch, searchedDatasByGraph, largeGraph);
            listLargeGraphs.add(largeGraph);
        });
        _graph.setOnMouseEntered(e -> {
            _graph.getScene().setCursor(Cursor.HAND);
        });
        _graph.setOnMouseExited(e -> {
            _graph.getScene().setCursor(Cursor.DEFAULT);
        });
    }

    private void updateDatasHistory() {
        listSearchedDatas.clear();
        obsList.clear();
        ListViewUtilies.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                cbActivity.isSelected(), cbCo2.isSelected());
        ListViewUtilies.setCellForData(lvHistory);
        if (currentSearch == null || currentSearch.trim().isEmpty()) {
            listSearchedDatas.addAll(listAllRoomsDatas);
            for (Data data : listAllRoomsDatas) {
                obsList.add(data.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
            }
        } else {
            String[] roomsToSearch = currentSearch.split(",");
            for (Data data : listAllRoomsDatas) {
                for (String room : roomsToSearch) {
                    if (data.getId().toLowerCase().contains(room.trim().toLowerCase())) {
                        listSearchedDatas.add(data);
                        obsList.add(data.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
                        break;
                    }
                }
            }
        }
    }

    private void updateAlertsHistory() {
        listSearchedAlerts.clear();
        obsList.clear();
        ListViewUtilies.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                cbActivity.isSelected(), cbCo2.isSelected());
        ListViewUtilies.setCellForAlert(lvHistory);
        if (currentSearch == null || currentSearch.trim().isEmpty()) {
            listSearchedAlerts.addAll(listAllRoomsAlerts);
            for (Alert alert : listAllRoomsAlerts) {
                obsList.add(alert.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
            }
        } else {
            String[] roomsToSearch = currentSearch.split(",");
            for (Alert alert : listAllRoomsAlerts) {
                for (String room : roomsToSearch) {
                    if (alert.getId().toLowerCase().contains(room.trim().toLowerCase())) {
                        listSearchedAlerts.add(alert);
                        obsList.add(alert.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
                        break;
                    }
                }
            }
        }
    }

    private TextField createLargeTxtSearch() {
        TextField largeViewTxtSearch = new TextField();
        largeViewTxtSearch.setFocusTraversable(false);
        if (txtSearch.getText().trim().equals("")) {
            largeViewTxtSearch.setPromptText("Rechercher une salle...");
        } else {
            largeViewTxtSearch.setText(txtSearch.getText().trim());
        }
        largeViewTxtSearch.setPrefHeight(50);
        largeViewTxtSearch.setStyle("-fx-font-size: 25px;");
        return largeViewTxtSearch;
    }

    private void initTxtSearch(TextField _textField, ArrayList<Data> _listSearchedDatasLargeGraph,
            LineChart<String, Number> _largeGraph) {
        _textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            imgSearchIcon.setVisible(!newValue);
            imgInfoSearch.setVisible(!newValue);
        });
        _textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (_largeGraph != null) {
                updateDataForLargeGraph(_listSearchedDatasLargeGraph, newValue.trim());
                GraphUtilies.updateGraphData(_largeGraph, _listSearchedDatasLargeGraph, largeGraphViewDataName,
                        largeGraphViewDataUnit, comboBoxDateFormat.getValue());
            } else {
                if (!currentSearch.toLowerCase().equals(newValue.trim().toLowerCase())) {
                    currentSearch = newValue.trim();
                    updateSceneByView();
                }
            }
        });
    }

    private void updateDataForLargeGraph(ArrayList<Data> _listSearched, String _searchedRooms) {
        _listSearched.clear();
        if (_searchedRooms == null || _searchedRooms.trim().isEmpty()) {
            _listSearched.addAll(listAllRoomsDatas);
        } else {
            String[] roomsToSearch = _searchedRooms.split(",");
            for (Data data : listAllRoomsDatas) {
                for (String room : roomsToSearch) {
                    if (data.getId().toLowerCase().contains(room.trim().toLowerCase())) {
                        _listSearched.add(data);
                        break;
                    }
                }
            }
        }
    }

    private void updateAllGraphs() {
        GraphUtilies.updateGraphData(graphTemperature, listSearchedDatas, "temperature", "°c",
                comboBoxDateFormat.getValue());
        GraphUtilies.updateGraphData(graphHumidity, listSearchedDatas, "humidity", "%", comboBoxDateFormat.getValue());
        GraphUtilies.updateGraphData(graphActivity, listSearchedDatas, "activity", "", comboBoxDateFormat.getValue());
        GraphUtilies.updateGraphData(graphCo2, listSearchedDatas, "co2", "ppm", comboBoxDateFormat.getValue());
    }

    @FXML
    private void doWharehouseMonitor() {
        closeLargeGraphsStages();
        WharehouseMonitor wharehouse = new WharehouseMonitor(primaryStage);
        wharehouse.show();
    }

    /**
     * Gère l'action liée au bouton de configuration.
     * Lance une animation de changement de scène vers la configuration.
     */
    @FXML
    private void doConfiguration() {
        closeLargeGraphsStages();
        Configuration conf = new Configuration(primaryStage);
        conf.show();
    }

    /**
     * Méthode associée au bouton FXML qui permet de fermer la fenêtre.
     * Initialise et affiche le menu principal lors de l'action de quitter.
     */
    @FXML
    private void doMenu() {
        closeLargeGraphsStages();
        MainMenu menu = new MainMenu();
        menu.start(primaryStage);
        menu.show();
    }

    /**
     * Méthode de fermeture de la fenêtre par la croix.
     *
     * @param _e L'événement de fermeture de fenêtre.
     */
    private void closeWindow(WindowEvent _e) {
        if (AlertUtilities.confirmYesCancel(primaryStage, "Quitter l'application",
                "Etes-vous sûr de vouloir quitter l'application ?", null, AlertType.CONFIRMATION)) {
            closeLargeGraphsStages();
            primaryStage.close();
            System.exit(0);
        }
        _e.consume();
    }

    private void closeLargeGraphsStages() {
        for (Stage largeGraph : listLargeGraphsStages) {
            largeGraph.close();
        }
    }
}