package application.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import application.control.Configuration;
import application.control.LogHistory;
import application.control.MainMenu;
import application.control.WharehouseMonitor;
import application.model.Data;
import application.tools.AlertUtilities;
import application.tools.Animations;
import application.tools.DateUtilities;
import application.tools.GraphMaker;
import application.tools.JsonReader;
import application.tools.ListViewUtilities;
import application.tools.NumbersUtilities;
import application.tools.PythonAndThreadManagement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * Contrôleur pour la surveillance de l'entrepôt dans l'application.
 * Gère les interactions et les actions associées aux éléments de cette vue.
 */
public class WharehouseMonitorController {

    // Référence à la classe de surveillance de l'entrepôt
    private WharehouseMonitor wharehouseMonitor;

    // Référence au stage de la fenêtre principale
    private Stage primaryStage;

    // Elements FXML

    @FXML
    private BorderPane borderpane;

    @FXML
    private Button buttMenu;

    @FXML
    private Button buttCheckWhareHouse;
    @FXML
    private ImageView imgConnexionState;

    @FXML
    private Button buttCheckHistory;

    @FXML
    private Button buttConfiguration;

    @FXML
    private Button buttListView;

    @FXML
    private Button buttLineChartView;

    @FXML
    private Button buttBarChartView;

    @FXML
    private CheckBox cbAvg;
    @FXML
    private ImageView imgInfoAvg;
    private final Tooltip tooltipInfoAverage = new Tooltip(
            "Activer / Désactiver l'affichage des moyennes sur la vue \"Histogramme / Diagramme en bâtons.");

    @FXML
    private CheckBox cbTemperature;
    @FXML
    private Label labTemperature;

    @FXML
    private CheckBox cbHumidity;
    @FXML
    private Label labHumidity;

    @FXML
    private CheckBox cbActivity;
    @FXML
    private Label labActivity;

    @FXML
    private CheckBox cbCo2;
    @FXML
    private Label labCo2;

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

    private LineChart<String, Number> lineChartTemperature = new LineChart<>(new CategoryAxis(), new NumberAxis());
    private LineChart<String, Number> lineChartHumidity = new LineChart<>(new CategoryAxis(), new NumberAxis());
    private LineChart<String, Number> lineChartActivity = new LineChart<>(new CategoryAxis(), new NumberAxis());
    private LineChart<String, Number> lineChartCo2 = new LineChart<>(new CategoryAxis(), new NumberAxis());
    private BarChart<String, Number> barChartTemperature = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private BarChart<String, Number> barChartHumidity = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private BarChart<String, Number> barChartActivity = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private BarChart<String, Number> barChartCo2 = new BarChart<>(new CategoryAxis(), new NumberAxis());

    private List<Stage> listLargeGraphsStages = new ArrayList<>();
    private ArrayList<List<Data>> listSearchedDatasByLargeGraph = new ArrayList<>();
    private List<XYChart<String, Number>> listLargeGraphs = new ArrayList<>();
    private String largeGraphViewDataName = "";
    private String largeGraphViewDataUnit = "";

    private VBox vboxGraphView;

    private ListView<String> lvHistory = new ListView<>();
    private ObservableList<String> obsList = FXCollections.observableArrayList();

    private ArrayList<Data> listAllRoomsDatas = new ArrayList<>();
    private ArrayList<Data> listSearchedDatas = new ArrayList<>();

    private final Tooltip tooltipImgSearch = new Tooltip(
            "Rentrer le nom de la salle à vérifier, choix multiples possible en séparant les salles par ','.\n\"Exemple : \"B103,E006,Amphi\".");

    private Thread updatesDataThread;

    private String fileDataPath = "";

    private Double maxTemperature;
    private Double maxHumidity;
    private Double maxActivity;
    private Double maxCo2;

    public static String clickedNotif = null;

    /**
     * Initialise le contrôleur de vue WharehouseMonitorController.
     *
     * @param _mainMenu     L'instance du menu principal.
     * @param _primaryStage La scène principale associée au contrôleur.
     */
    public void initContext(WharehouseMonitor _wharehouse, Stage _primaryStage) {
        this.wharehouseMonitor = _wharehouse;
        this.primaryStage = _primaryStage;
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
        setConfiguration();

        PythonAndThreadManagement.initImgConnexionState(imgConnexionState);
        if (!PythonAndThreadManagement.isPythonRunning()) {
            PythonAndThreadManagement.startPythonThread(_primaryStage);
        }
        PythonAndThreadManagement.updateImgConnexionState();

        PythonAndThreadManagement.stopThreadByName("getNewDatasThread");
        initGetNewDatasThread();

        initViewElements();

        setBarChartView();
        updateDatasHistory();
    }

    /**
     * Initialise et lance un thread pour récupérer de nouvelles données à partir
     * d'un fichier JSON.
     * Ce thread vérifie périodiquement si le fichier JSON a été modifié et met à
     * jour les données si nécessaire.
     */
    private void initGetNewDatasThread() {
        updatesDataThread = new Thread(() -> {
            long lastTime = 0;
            long lastAlert = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    File jsonFile = new File(fileDataPath + ".json");
                    long currentTime = jsonFile.lastModified();
                    if (currentTime > lastTime) {
                        Platform.runLater(() -> {
                            if (jsonFile.exists()) {
                                JsonReader.updateHistoryFromFile(primaryStage, true, false, obsList,
                                        listAllRoomsDatas, null, comboBoxRooms);
                                if (currentTime > lastAlert) {
                                    checkAlertForLastData();
                                }
                                updateDatasHistory();
                            }
                        });
                        lastTime = currentTime;
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        updatesDataThread.setName("getNewDatasThread");
        updatesDataThread.start();
    }

    // Permet d'éviter les doubles envoies d'une notification
    private String lastRoomId = "";
    private long lastNotifTime = 0;

    /**
     * Vérifie les alertes pour les dernières données reçues et déclenche des
     * notifications en cas de dépassement des seuils.
     */
    private void checkAlertForLastData() {
        if (listAllRoomsDatas.size() > 0) {
            Data data = listAllRoomsDatas.get(listAllRoomsDatas.size() - 1);
            String alerts = "";

            if (maxTemperature != null && data.getTemperature() > maxTemperature) {
                alerts += "     Seuil de température dépassé !\n";
            }
            if (maxHumidity != null && data.getHumidity() > maxHumidity) {
                alerts += "     Seuil d'humidité dépassé !\n";
            }
            if (maxActivity != null && data.getActivity() > maxActivity) {
                alerts += "     Seuil d'activité dépassé !\n";
            }
            if (maxCo2 != null && data.getCo2() > maxCo2) {
                alerts += "     Seuil de Co2 dépassé !";
            }
            if (!alerts.equals("") && !lastRoomId.equals(data.getId())
                    || System.currentTimeMillis() - lastNotifTime > 30_000) {
                createAlertNotification(data, alerts);
                lastRoomId = data.getId();
                lastNotifTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Crée une notification d'alerte pour les données spécifiées.
     *
     * @param _data  Les données pour lesquelles créer l'alerte.
     * @param _alert Le message d'alerte à afficher.
     */
    private void createAlertNotification(Data _data, String _alert) {
        Notifications.create()
                .title(_data.getId())
                .text(_alert)
                .onAction(event -> {
                    clickedNotif = _data.getId();
                    LogHistory history = new LogHistory(primaryStage);
                    history.show();
                })
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .graphic(new ImageView(new Image("/application/images/alert_data_icon.png",
                        55, 55, false, false)))
                .show();
    }

    /**
     * Initialise les éléments de la vue.
     * Configure les éléments graphiques, les boutons et les actions associées.
     */
    private void initViewElements() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        Animations.setAnimatedButton(buttMenu, 1.1, 1, 100);
        Animations.setAnimatedButton(buttCheckHistory, 1.1, 1, 100);
        Animations.setAnimatedButton(buttConfiguration, 1.1, 1, 100);
        Animations.setSelectedMenuAnimation(buttCheckWhareHouse, 0.5, 0.8, 1000);

        tooltipImgSearch.setStyle("-fx-font-size: 18px;");
        tooltipImgSearch.setShowDelay(Duration.ZERO);
        tooltipImgSearch.setShowDuration(Duration.INDEFINITE);
        tooltipInfoAverage.setStyle("-fx-font-size: 18px;");
        tooltipInfoAverage.setShowDelay(Duration.ZERO);
        tooltipInfoAverage.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(imgInfoSearch, tooltipImgSearch);
        Tooltip.install(imgInfoAvg, tooltipInfoAverage);

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
                updateDatasHistory();
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
                updateDatasHistory();
            }
        });
        cbAvg.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateDatasHistory();
        });

        initializeCheckboxes(cbTemperature);
        initializeCheckboxes(cbHumidity);
        initializeCheckboxes(cbActivity);
        initializeCheckboxes(cbCo2);

        initTxtSearch(txtSearch, null, null);
        ListViewUtilities.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                cbActivity.isSelected(), cbCo2.isSelected());

        lvHistory.setItems(obsList);

        initGraph(lineChartTemperature, "Température (°c)", "temperature", "°c", 0, 0);
        initGraph(lineChartHumidity, "Humidité (%)", "humidity", "%", 1, 0);
        initGraph(lineChartActivity, "Activité", "activity", "", 0, 1);
        initGraph(lineChartCo2, "Co2 (ppm)", "co2", "ppm", 1, 1);

        initGraph(barChartTemperature, "Température (°c)", "temperature", "°c", 0, 0);
        initGraph(barChartHumidity, "Humidité (%)", "humidity", "%", 1, 0);
        initGraph(barChartActivity, "Activité", "activity", "", 0, 1);
        initGraph(barChartCo2, "Co2 (ppm)", "co2", "ppm", 1, 1);
    }

    /**
     * Initialise les événements des cases à cocher.
     * Met à jour les graphiques ou l'historique en fonction de l'état des boutons
     * d'affichage.
     *
     * @param _cb La case à cocher à initialiser.
     */
    private void initializeCheckboxes(CheckBox _cb) {
        _cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (buttLineChartView.isDisabled()) {
                GraphMaker.updateGraphsPositions(
                        vboxGraphView,
                        Arrays.asList(lineChartTemperature, lineChartHumidity, lineChartActivity, lineChartCo2),
                        cbTemperature.isSelected(), cbHumidity.isSelected(), cbActivity.isSelected(),
                        cbCo2.isSelected());
            } else if (buttBarChartView.isDisabled()) {
                GraphMaker.updateGraphsPositions(
                        vboxGraphView,
                        Arrays.asList(barChartTemperature, barChartHumidity, barChartActivity, barChartCo2),
                        cbTemperature.isSelected(), cbHumidity.isSelected(), cbActivity.isSelected(),
                        cbCo2.isSelected());
            } else {
                updateDatasHistory();
            }
        });
    }

    /**
     * Modifie la scène en fonction de la vue sélectionnée.
     * Actualise les données affichées en conséquence.
     *
     * @param _isGraphView Vrai si la vue est un graphique, faux sinon.
     */
    private void setSceneForView(boolean _isGraphView) {
        if (_isGraphView) {
            vboxGraphView = new VBox();
            borderpane.setCenter(vboxGraphView);
            BorderPane.setMargin(vboxGraphView, new Insets(10));
            if (buttBarChartView.isDisabled()) {
                GraphMaker.updateGraphsPositions(
                        vboxGraphView,
                        Arrays.asList(barChartTemperature, barChartHumidity, barChartActivity, barChartCo2),
                        cbTemperature.isSelected(), cbHumidity.isSelected(), cbActivity.isSelected(),
                        cbCo2.isSelected());
            } else {
                GraphMaker.updateGraphsPositions(
                        vboxGraphView,
                        Arrays.asList(lineChartTemperature, lineChartHumidity, lineChartActivity, lineChartCo2),
                        cbTemperature.isSelected(), cbHumidity.isSelected(), cbActivity.isSelected(),
                        cbCo2.isSelected());
            }
        } else {
            borderpane.setCenter(lvHistory);
            BorderPane.setMargin(lvHistory, new Insets(10));
            ListViewUtilities.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                    cbActivity.isSelected(), cbCo2.isSelected());
        }
    }

    /**
     * Configure la vue pour afficher les graphiques en ligne.
     * Actualise les données en fonction de cette vue.
     */
    @FXML
    private void setLineChartView() {
        buttListView.setDisable(false);
        buttBarChartView.setDisable(false);
        buttLineChartView.setDisable(true);
        cbAvg.setDisable(true);
        comboBoxDateFormat.setDisable(false);
        setSceneForView(true);
        updateDatasHistory();
    }

    /**
     * Configure la vue pour afficher les graphiques en barres.
     * Actualise les données en fonction de cette vue.
     */
    @FXML
    private void setBarChartView() {
        buttListView.setDisable(false);
        buttLineChartView.setDisable(false);
        buttBarChartView.setDisable(true);
        cbAvg.setDisable(false);
        comboBoxDateFormat.setDisable(true);
        setSceneForView(true);
        updateDatasHistory();
    }

    /**
     * Configure la vue pour afficher l'historique en liste.
     * Actualise les données en fonction de cette vue.
     */
    @FXML
    private void setListView() {
        buttListView.setDisable(true);
        buttLineChartView.setDisable(false);
        buttBarChartView.setDisable(false);
        cbAvg.setDisable(true);
        comboBoxDateFormat.setDisable(false);
        setSceneForView(false);
        updateDatasHistory();
    }

    /**
     * Affiche les alertes et met à jour l'historique.
     */
    @FXML
    private void showAlerts() {
        clickedNotif = "";
        doCheckHistory();
    }

    /**
     * Charge la configuration des données depuis le fichier "config.ini" et met à
     * jour l'interface en conséquence.
     */
    private void setConfiguration() {
        try (FileInputStream fileInputStream = new FileInputStream("config.ini")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            String datasChosen = properties.getProperty("choix_donnees");
            setCheckBoxByConf(datasChosen, "temperature", cbTemperature, labTemperature);
            setCheckBoxByConf(datasChosen, "humidity", cbHumidity, labHumidity);
            setCheckBoxByConf(datasChosen, "activity", cbActivity, labActivity);
            setCheckBoxByConf(datasChosen, "co2", cbCo2, labCo2);
            fileDataPath = properties.getProperty("fichier_donnees");
            maxTemperature = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Temperature"));
            maxHumidity = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Humidity"));
            maxActivity = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Activity"));
            maxCo2 = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_CO2"));
            ListViewUtilities.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                    cbActivity.isSelected(), cbCo2.isSelected());

        } catch (IOException e) {
            AlertUtilities.showAlert(primaryStage, "Aucun fichier trouvé.",
                    "Aucune configuration existante trouvé.",
                    "Aucune configuration n'a pu être chargé, merci d'effectuer une sauvegarde du fichier de configuration.",
                    AlertType.ERROR);
        }
    }

    /**
     * Met à jour la case à cocher et le label en fonction des paramètres de
     * configuration.
     *
     * @param _dataChoosen Les données choisies dans la configuration.
     * @param _dataToCheck Les données à vérifier.
     * @param _cbData      La case à cocher à mettre à jour.
     * @param _labData     Le label associé à la case à cocher.
     */
    private void setCheckBoxByConf(String _dataChoosen, String _dataToCheck, CheckBox _cbData, Label _labData) {
        if (_dataChoosen.contains(_dataToCheck)) {
            _cbData.setSelected(true);
        } else {
            _cbData.setVisible(false);
            _labData.setVisible(false);
        }
    }

    /**
     * Initialise les graphiques et leurs interactions.
     *
     * @param _graph     Le graphique à initialiser.
     * @param _tittle    Le titre du graphique.
     * @param _dataName  Le nom des données associées.
     * @param _dataUnit  L'unité des données.
     * @param _gridPaneX La position X sur la grille.
     * @param _gridPaneY La position Y sur la grille.
     */
    private void initGraph(XYChart<String, Number> _graph, String _tittle, String _dataName, String _dataUnit,
            int _gridPaneX, int _gridPaneY) {
        _graph.setTitle(_tittle);
        GridPane.setMargin(_graph, new Insets(5, 5, 5, 5));
        _graph.setOnMouseClicked(event -> {
            largeGraphViewDataName = _dataName;
            largeGraphViewDataUnit = _dataUnit;
            TextField largeTxtSearch = createLargeTxtSearch();
            ArrayList<Data> searchedDatasByGraph = new ArrayList<>(listSearchedDatas);
            listSearchedDatasByLargeGraph.add(searchedDatasByGraph);
            XYChart<String, Number> largeGraph = GraphMaker.displayLargeGraph(primaryStage, _graph,
                    listLargeGraphsStages, largeTxtSearch, _dataUnit);
            GraphMaker.updateGraphData(largeGraph, searchedDatasByGraph, largeGraphViewDataName,
                    largeGraphViewDataUnit, comboBoxDateFormat.getValue(), cbAvg.isSelected());
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

    /**
     * Met à jour l'historique des données affichées en fonction des critères de
     * recherche.
     */
    private void updateDatasHistory() {
        listSearchedDatas.clear();
        obsList.clear();
        ListViewUtilities.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                cbActivity.isSelected(), cbCo2.isSelected());
        ListViewUtilities.setCellForData(lvHistory);
        if (currentSearch == null || currentSearch.trim().isEmpty()) {
            listSearchedDatas.addAll(listAllRoomsDatas);
            for (Data data : listAllRoomsDatas) {
                obsList.add(data.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
            }
            Collections.reverse(obsList);
        } else {
            String[] roomsToSearch = currentSearch.split(",");
            for (Data data : listAllRoomsDatas) {
                for (String room : roomsToSearch) {
                    if (data.getId().toLowerCase().contains(room.trim().toLowerCase())) {
                        listSearchedDatas.add(data);
                        obsList.add(
                                data.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
                        break;
                    }
                }
            }
        }
        if (buttLineChartView.isDisabled()) {
            GraphMaker.updateGraphData(lineChartTemperature, listSearchedDatas, "temperature", "°c",
                    comboBoxDateFormat.getValue(), cbAvg.isSelected());
            GraphMaker.updateGraphData(lineChartHumidity, listSearchedDatas, "humidity", "%",
                    comboBoxDateFormat.getValue(), cbAvg.isSelected());
            GraphMaker.updateGraphData(lineChartActivity, listSearchedDatas, "activity", "",
                    comboBoxDateFormat.getValue(), cbAvg.isSelected());
            GraphMaker.updateGraphData(lineChartCo2, listSearchedDatas, "co2", "ppm", comboBoxDateFormat.getValue(),
                    cbAvg.isSelected());
        } else if (buttBarChartView.isDisabled()) {
            GraphMaker.updateGraphData(barChartTemperature, listSearchedDatas, "temperature", "°c",
                    comboBoxDateFormat.getValue(), cbAvg.isSelected());
            GraphMaker.updateGraphData(barChartHumidity, listSearchedDatas, "humidity", "%",
                    comboBoxDateFormat.getValue(), cbAvg.isSelected());
            GraphMaker.updateGraphData(barChartActivity, listSearchedDatas, "activity", "",
                    comboBoxDateFormat.getValue(), cbAvg.isSelected());
            GraphMaker.updateGraphData(barChartCo2, listSearchedDatas, "co2", "ppm", comboBoxDateFormat.getValue(),
                    cbAvg.isSelected());
        }
    }

    /**
     * Crée un champ de recherche pour une vue en grand format.
     *
     * @return Le champ de recherche pour la vue en grand format.
     */
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

    /**
     * Initialise le champ de recherche pour une vue spécifique.
     *
     * @param _textField                   Le champ de recherche à initialiser.
     * @param _listSearchedDatasLargeGraph La liste des données recherchées pour la
     *                                     vue en grand format.
     * @param _largeGraph                  Le graphique en grand format associé.
     */
    private void initTxtSearch(TextField _textField, ArrayList<Data> _listSearchedDatasLargeGraph,
            XYChart<String, Number> _largeGraph) {
        _textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (_largeGraph != null) {
                updateDataForLargeGraph(_listSearchedDatasLargeGraph, newValue.trim());
                GraphMaker.updateGraphData(_largeGraph, _listSearchedDatasLargeGraph, largeGraphViewDataName,
                        largeGraphViewDataUnit, comboBoxDateFormat.getValue(), cbAvg.isSelected());
            } else {
                if (!currentSearch.toLowerCase().equals(newValue.trim().toLowerCase())) {
                    currentSearch = newValue.trim();
                    updateDatasHistory();
                }
            }
        });
    }

    /**
     * Met à jour les données pour un graphique en grand format en fonction des
     * salles recherchées.
     *
     * @param _listSearched  La liste des données recherchées.
     * @param _searchedRooms Les salles recherchées.
     */
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

    /**
     * Méthode de fermeture de la fenêtre par la croix.
     *
     * @param _e L'événement de fermeture de fenêtre.
     */
    private void closeWindow(WindowEvent _e) {
        closeLargeGraphsStages();
        PythonAndThreadManagement.stopPythonProcesses();
        this.primaryStage.close();
        System.exit(0);
    }

    /**
     * Affiche l'historique des logs en lançant la fenêtre dédiée.
     * Cette méthode réalise une animation de transition vers l'historique des
     * journaux en créant une nouvelle fenêtre LogHistory.
     */
    @FXML
    private void doCheckHistory() {
        clickedNotif = null;
        LogHistory history = new LogHistory(primaryStage);
        history.show();
    }

    /**
     * Gère l'action liée au bouton de configuration.
     * Lance une animation de changement de scène vers la configuration.
     */
    @FXML
    private void doConfiguration() {
        PythonAndThreadManagement.stopPythonThread();
        Configuration conf = new Configuration(primaryStage);
        conf.show();
    }

    /**
     * Méthode associée au bouton FXML qui permet de fermer la fenêtre.
     * Initialise et affiche le menu principal lors de l'action de quitter.
     */
    @FXML
    private void doMenu() {
        PythonAndThreadManagement.stopPythonThread();
        MainMenu menu = new MainMenu();
        menu.start(primaryStage);
        menu.show();
    }

    /**
     * Ferme toutes les fenêtres des graphiques étendus.
     */
    private void closeLargeGraphsStages() {
        for (Stage largeGraph : listLargeGraphsStages) {
            largeGraph.close();
        }
    }
}