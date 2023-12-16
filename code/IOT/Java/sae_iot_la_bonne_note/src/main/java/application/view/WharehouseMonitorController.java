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
import application.tools.GraphUtilies;
import application.tools.JsonUtilities;
import application.tools.ListViewUtilies;
import application.tools.NumbersUtilities;
import application.tools.PythonProcessUtilities;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
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

public class WharehouseMonitorController {

    // Référence à la classe de surveillance de l'entrepôt
    private WharehouseMonitor wharehouseMonitor;

    // Référence au stage de la fenêtre principale
    private Stage primaryStage;
    private List<Stage> listLargeGraphsStages = new ArrayList<>();
    private List<LineChart<String, Number>> listLargeGraphs = new ArrayList<>();

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

    private final Tooltip tooltipImgSearch = new Tooltip(
            "Rentrer le nom de la salle à vérifier, choix multiples possible en séparant les salles par ','.\n\"Exemple : \"B103,E006,Amphi\".");

    private Thread updatesDataThread;

    private int frequency;
    private Double maxTemperature;
    private Double maxHumidity;
    private Double maxActivity;
    private Double maxCo2;

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

        if (!PythonProcessUtilities.isPythonRunning()) {
            PythonProcessUtilities.startPythonThread(_primaryStage);
        }

        setConfiguration();
        initViewElements();

        PythonProcessUtilities.stopPythonProcesses();
        PythonProcessUtilities.startPythonThread(primaryStage);
        initGetNewDatasThread();

        setSceneForView(true);
        setGraphView();
        updateSceneByView();
        // showLogs();
    }

    private void initGetNewDatasThread() {
        updatesDataThread = new Thread(() -> {
            // long currentTime = System.currentTimeMillis();
            long lastModified = 0;
            while (true) {
                try {
                    // System.out.println(i);
                    File jsonFile = new File("code\\IOT\\Python\\donnees.json");
                    long currentLastModified = jsonFile.lastModified();
                    if (currentLastModified > lastModified) {
                        Platform.runLater(() -> {
                            if (jsonFile.exists()) {
                                JsonUtilities.updateHistoryFromFile(primaryStage, true, false, obsList,
                                        listAllRoomsDatas, null, comboBoxRooms);
                                updateSceneByView();
                                checkAlertForLastData();
                            }
                        });
                        lastModified = currentLastModified;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        updatesDataThread.start();
    }

    private void updateSceneByView() {
        updateDatasHistory();
        if (buttGraphView.isDisabled()) {
            updateAllGraphs();
        }
    }

    private void checkAlertForLastData() {
        if (listAllRoomsDatas.size() > 0) {
            Data data = listAllRoomsDatas.get(listAllRoomsDatas.size() - 1);
            String alerts = "";
            if (data.getTemperature() > maxTemperature) {
                alerts += "     Seuil de température dépassé !\n";
            }
            if (data.getHumidity() > maxHumidity) {
                alerts += "     Seuil d'humidité dépassé !\n";
            }
            if (data.getActivity() > maxActivity) {
                alerts += "     Seuil d'activité dépassé !\n";
            }
            if (data.getCo2() > maxCo2) {
                alerts += "     Seuil de Co2 dépassé !\n";
            }
            if (!alerts.equals("")) {
                createAlertNotification(data.getId(), alerts);
            }
        }
    }

    private void createAlertNotification(String _roomId, String _alert) {
        Notifications.create()
                .title(_roomId)
                .text(_alert)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                // .owner(primaryStage)
                .graphic(new ImageView(new Image("/application/images/alert_data_icon.png",
                        45, 45, false, false)))
                .show();
    }

    private void initViewElements() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        Animations.setAnimatedButton(buttMenu, 1.1, 1, 100);
        Animations.setAnimatedButton(buttCheckHistory, 1.1, 1, 100);
        Animations.setAnimatedButton(buttConfiguration, 1.1, 1, 100);
        Animations.setSelectedMenuAnimation(buttCheckWhareHouse, 0.5, 0.8, 1000);

        initializeCheckboxes(cbTemperature);
        initializeCheckboxes(cbHumidity);
        initializeCheckboxes(cbActivity);
        initializeCheckboxes(cbCo2);

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
        initTxtSearch(txtSearch, null);
        lvHistory.setItems(obsList);
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
                ListViewUtilies.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                        cbActivity.isSelected(), cbCo2.isSelected());
                updateSceneByView();
            }
        });
    }

    private void setSceneForView(boolean _isGraphView) {
        if (_isGraphView) {
            vboxGraphView.getStyleClass().add("vbox");
            borderpane.setCenter(vboxGraphView);
            BorderPane.setMargin(vboxGraphView, new Insets(10));
        } else {
            borderpane.setCenter(lvHistory);
            BorderPane.setMargin(lvHistory, new Insets(10));
        }
    }

    @FXML
    private void setGraphView() {
        buttListView.setDisable(false);
        buttGraphView.setDisable(true);
        initGraph(graphTemperature, "Température (°c)", "temperature", "°c", 0, 0);
        initGraph(graphHumidity, "Humidité (%)", "humidity", "%", 1, 0);
        initGraph(graphActivity, "Activité (0 à 1)", "activity", "", 0, 1);
        initGraph(graphCo2, "Co2 (ppm)", "co2", "ppm", 1, 1);
        setSceneForView(true);
        GraphUtilies.updateGraphsPositions(
                vboxGraphView, Arrays.asList(graphTemperature, graphHumidity, graphActivity, graphCo2),
                cbTemperature.isVisible(), cbHumidity.isVisible(), cbActivity.isVisible(), cbCo2.isVisible());
        updateAllGraphs();
        updateSceneByView();
    }

    @FXML
    private void setListView() {
        buttListView.setDisable(true);
        buttGraphView.setDisable(false);
        setSceneForView(false);
        updateSceneByView();
    }

    private void setConfiguration() {
        try (FileInputStream fileInputStream = new FileInputStream("code\\IOT\\Python\\config.ini")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            String datasChosen = properties.getProperty("choix_donnees");
            setCheckBoxByConf(datasChosen, "temperature", cbTemperature, labTemperature);
            setCheckBoxByConf(datasChosen, "humidity", cbHumidity, labHumidity);
            setCheckBoxByConf(datasChosen, "activity", cbActivity, labActivity);
            setCheckBoxByConf(datasChosen, "co2", cbCo2, labCo2);
            frequency = getIntFromString(properties.getProperty("frequence_affichage"));
            maxTemperature = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Temperature"));
            maxHumidity = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Humidity"));
            maxActivity = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Activity"));
            maxCo2 = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_CO2"));
            ListViewUtilies.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                    cbActivity.isSelected(), cbCo2.isSelected());

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtilities.showAlert(primaryStage, "Aucun fichier trouvé.", "Aucune configuration existante trouvé.",
                    "Aucune configuration n'a pu être chargé, merci d'effectuer une sauvegarde du fichier de configuration.",
                    AlertType.ERROR);
            return;
        }
    }

    private void setCheckBoxByConf(String _dataChoosen, String _dataToCheck, CheckBox _cbData, Label _labData) {
        if (_dataChoosen.contains(_dataToCheck)) {
            _cbData.setSelected(true);
        } else {
            _cbData.setVisible(false);
            _labData.setVisible(false);
        }
    }

    /**
     * Convertit une chaîne en entier.
     * ²
     * 
     * @param _string La chaîne à convertir en entier.
     * @return La valeur entière de la chaîne si la conversion réussit, sinon
     *         retourne 0.
     */
    private int getIntFromString(String _string) {
        try {
            int val = Integer.parseInt(_string);
            return val;
        } catch (Exception e) {
            return 0;
        }
    }

    private void initGraph(LineChart<String, Number> _graph, String _tittle, String _dataName, String _dataUnit,
            int _gridPaneX, int _gridPaneY) {
        _graph.setTitle(_tittle);
        GridPane.setMargin(_graph, new Insets(5, 5, 5, 5));
        _graph.setOnMouseClicked(event -> {
            largeGraphViewDataName = _dataName;
            largeGraphViewDataUnit = _dataUnit;
            TextField largeTxtSearch = createLargeTxtSearch();
            LineChart<String, Number> largeGraph = GraphUtilies.displayLargeGraph(primaryStage, _graph,
                    listLargeGraphsStages,
                    largeTxtSearch, _dataUnit);
            initTxtSearch(largeTxtSearch, largeGraph);
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
        ListViewUtilies.setCellForData(lvHistory);
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
                        obsList.add(data.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
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

    private void initTxtSearch(TextField _textField, LineChart<String, Number> _largeGraph) {
        _textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            imgSearchIcon.setVisible(!newValue);
            imgInfoSearch.setVisible(!newValue);
        });
        _textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!currentSearch.toLowerCase().equals(newValue.trim().toLowerCase())) {
                currentSearch = newValue.trim();
                updateSceneByView();
            }
            if (_largeGraph != null) {
                GraphUtilies.updateGraphData(_largeGraph, listSearchedDatas, largeGraphViewDataName,
                        largeGraphViewDataUnit, comboBoxDateFormat.getValue());
            }
        });
    }

    private void updateAllGraphs() {
        GraphUtilies.updateGraphData(graphTemperature, listSearchedDatas, "temperature", "°c",
                comboBoxDateFormat.getValue());
        GraphUtilies.updateGraphData(graphHumidity, listSearchedDatas, "humidity", "%", comboBoxDateFormat.getValue());
        GraphUtilies.updateGraphData(graphActivity, listSearchedDatas, "activity", "", comboBoxDateFormat.getValue());
        GraphUtilies.updateGraphData(graphCo2, listSearchedDatas, "co2", "ppm", comboBoxDateFormat.getValue());
    }

    /**
     * Méthode de fermeture de la fenêtre par la croix.
     *
     * @param _e L'événement de fermeture de fenêtre.
     */
    private void closeWindow(WindowEvent _e) {
        // if (AlertUtilities.confirmYesCancel(this.primaryStage, "Quitter
        // l'application",
        // "Etes-vous sûr de vouloir quitter l'application ?", null,
        // AlertType.CONFIRMATION)) {
        PythonProcessUtilities.stopPythonProcesses();
        this.primaryStage.close();
        // }
        System.exit(0);
        _e.consume();
    }

    @FXML
    private void doCheckHistory() {
        LogHistory history = new LogHistory(primaryStage);
        history.show();
    }

    /**
     * Gère l'action liée au bouton de configuration.
     * Lance une animation de changement de scène vers la configuration.
     */
    @FXML
    private void doConfiguration() {
        PythonProcessUtilities.stopPythonProcesses();
        Configuration conf = new Configuration(primaryStage);
        conf.show();
    }

    /**
     * Méthode associée au bouton FXML qui permet de fermer la fenêtre.
     * Initialise et affiche le menu principal lors de l'action de quitter.
     */
    @FXML
    private void doMenu() {
        PythonProcessUtilities.stopPythonThread();
        MainMenu menu = new MainMenu();
        menu.start(primaryStage);
        menu.show();
    }
}
