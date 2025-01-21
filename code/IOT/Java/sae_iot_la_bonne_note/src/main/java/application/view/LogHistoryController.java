package application.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import application.tools.GraphMaker;
import application.tools.JsonReader;
import application.tools.ListViewUtilities;
import application.tools.PythonAndThreadManagement;
import application.tools.Style;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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

/**
 * Contrôleur pour la visualisation de l'historique des logs / alertes.
 */
public class LogHistoryController {

    // Référence à la classe de l'historique
    private LogHistory logHistory;

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
    private List<XYChart<String, Number>> listLargeGraphs = new ArrayList<>();

    private final Tooltip tooltipImgSearch = new Tooltip(
            "Rentrer le nom de la salle à vérifier, choix multiples possible en séparant les salles par ','.\n\"Exemple : \"B103,E006,Amphi\".");

    /**
     * Initialise le contrôleur de vue LogHistoryController.
     *
     * @param _mainMenu     L'instance du menu principal.
     * @param _primaryStage La scène principale associée au contrôleur.
     * @param _clickedAlert La notification d'alerte qui a été cliquée.
     */
    public void initContext(LogHistory _mainMenu, Stage _primaryStage, Alert _clickedAlert) {
        this.logHistory = _mainMenu;
        this.primaryStage = _primaryStage;

        PythonAndThreadManagement.initImgConnexionState(imgConnexionState);
        PythonAndThreadManagement.updateImgConnexionState();

        Platform.runLater(() -> {
            JsonReader.updateHistoryFromFile(primaryStage, false, true, obsList,
                    null, listAllRoomsAlerts, comboBoxRooms);
            JsonReader.updateHistoryFromFile(primaryStage, false, false, obsList,
                    listAllRoomsDatas, null, comboBoxRooms);
            Comparator<Data> comparator = (data1, data2) -> {
                return data1.getDate().compareTo(data2.getDate());
            };

            // Trie les données par date croissante
            Collections.sort(listAllRoomsAlerts, comparator);
            Collections.sort(listAllRoomsDatas, comparator);

            // Inverse la liste pour avoir au début les données les plus récentes
            Collections.reverse(listAllRoomsAlerts);
            Collections.reverse(listAllRoomsDatas);

            initViewElements();
            if (_clickedAlert != null) {
                setListView();
                showAlerts();
                if (!_clickedAlert.getId().equals("null")) {
                    // Recherche l'alerte cliqué pour la sélectionner
                    for (Alert alert : listAllRoomsAlerts) {
                        if (_clickedAlert.equals(alert.getId(), alert.getDate())) {
                            lvHistory.getSelectionModel().select(listAllRoomsAlerts.indexOf(alert));
                            break;
                        }
                    }
                }
            } else {
                setGraphView();
                showLogs();
            }
        });

    }

    /**
     * Initialise les événements liés aux cases à cocher.
     * À chaque modification de la sélection d'une case à cocher, cette méthode est
     * appelée pour vérifier les actions à effectuer.
     * Si la vue graphique est désactivée, met à jour les positions des graphiques
     * en fonction des cases cochées.
     * Sinon, met à jour la scène en fonction de la vue sélectionnée.
     *
     * @param _cb La checkbox concernée pour initialiser ses événements.
     */
    private void initializeCheckboxes(CheckBox _cb) {
        _cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (buttGraphView.isDisabled()) {
                GraphMaker.updateGraphsPositions(
                        vboxGraphView, Arrays.asList(graphTemperature, graphHumidity, graphActivity, graphCo2),
                        cbTemperature.isSelected(), cbHumidity.isSelected(), cbActivity.isSelected(),
                        cbCo2.isSelected());
            } else {
                updateSceneByView();
            }
        });
    }

    /**
     * Met à jour la scène en fonction de la vue actuellement sélectionnée.
     * Si la vérification des alertes est désactivée, met à jour l'historique des
     * alertes.
     * Sinon, met à jour l'historique des données.
     */
    private void updateSceneByView() {
        if (buttCheckAlerts.isDisabled()) {
            updateHistory(true);
        } else {
            updateHistory(false);
        }
    }

    /**
     * Initialise les éléments de la vue.
     * Configurations des animations pour les boutons, les tooltips, les ComboBox.
     * Gestion des événements sur les ComboBox, les cases à cocher, et les listes.
     * Préparation des graphiques de température, humidité, activité et CO2.
     */
    private void initViewElements() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        Animations.setAnimatedButton(buttMenu, 1.1, 1, 100);
        Animations.setAnimatedButton(buttCheckWhareHouse, 1.1, 1, 100);
        Animations.setAnimatedButton(buttConfiguration, 1.1, 1, 100);
        Animations.setSelectedMenuAnimation(buttCheckHistory, 0.5, 0.8, 1000);

        Style.setToolTip(tooltipImgSearch, 18, Duration.ZERO, Duration.INDEFINITE);

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
        comboBoxDateFormat.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateSceneByView();
            }
        });
        comboBoxRooms.setValue("Toutes");
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
        initializeCheckboxes(cbTemperature);
        initializeCheckboxes(cbHumidity);
        initializeCheckboxes(cbActivity);
        initializeCheckboxes(cbCo2);
        initTxtSearch(txtSearch, null, null);
        ListViewUtilities.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                cbActivity.isSelected(), cbCo2.isSelected());
        lvHistory.setItems(obsList);
        initGraph(graphTemperature, "Température (°c)", "temperature", "°c", 0, 0);
        initGraph(graphHumidity, "Humidité (%)", "humidity", "%", 1, 0);
        initGraph(graphActivity, "Activité", "activity", "", 0, 1);
        initGraph(graphCo2, "Co2 (ppm)", "co2", "ppm", 1, 1);
    }

    /**
     * Configure la scène en fonction de la vue souhaitée.
     * Si _isGraphView est vrai, affiche la vue graphique avec les graphiques de
     * température,
     * humidité, activité et CO2. Sinon, affiche la liste d'historique des données.
     * Met à jour les éléments sélectionnés en fonction des cases à cocher.
     *
     * @param _isGraphView Booléen indiquant si la vue graphique doit être affichée
     *                     ou non.
     */
    private void setSceneForView(boolean _isGraphView) {
        if (_isGraphView) {
            borderpane.setCenter(vboxGraphView);
            BorderPane.setMargin(vboxGraphView, new Insets(10));
            GraphMaker.updateGraphsPositions(
                    vboxGraphView, Arrays.asList(graphTemperature, graphHumidity, graphActivity, graphCo2),
                    cbTemperature.isSelected(), cbHumidity.isSelected(), cbActivity.isSelected(),
                    cbCo2.isSelected());
        } else {
            borderpane.setCenter(lvHistory);
            BorderPane.setMargin(lvHistory, new Insets(10));
            ListViewUtilities.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                    cbActivity.isSelected(), cbCo2.isSelected());
        }
    }

    /**
     * Active la vue graphique.
     * Désactive la vue en liste, active la vue graphique, et met à jour la scène en
     * conséquence.
     * Met à jour la scène en fonction de la vue actuelle.
     */
    @FXML
    private void setGraphView() {
        buttListView.setDisable(false);
        buttGraphView.setDisable(true);
        buttCheckAlerts.setDisable(false);
        buttCheckLogs.setDisable(true);
        setSceneForView(true);
        updateSceneByView();
    }

    /**
     * Active la vue en liste.
     * Désactive la vue graphique, active la vue en liste, et met à jour la scène en
     * conséquence.
     * Met à jour la scène en fonction de la vue actuelle.
     */
    @FXML
    private void setListView() {
        buttListView.setDisable(true);
        buttGraphView.setDisable(false);
        setSceneForView(false);
        updateSceneByView();
    }

    /**
     * Affiche les logs.
     * Désactive l'affichage des alertes, active l'affichage des logs et met à jour
     * la scène.
     */
    @FXML
    private void showLogs() {
        buttCheckLogs.setDisable(true);
        buttCheckAlerts.setDisable(false);
        updateSceneByView();

    }

    /**
     * Affiche les alertes.
     * Si la vue graphique est désactivée, active la vue en liste avant d'afficher
     * les alertes.
     * Active l'affichage des alertes et met à jour la scène.
     */
    @FXML
    private void showAlerts() {
        if (buttGraphView.isDisabled()) {
            setListView();
        }
        buttCheckLogs.setDisable(false);
        buttCheckAlerts.setDisable(true);
        updateSceneByView();
    }

    /**
     * Supprime les logs.
     * Appelle la méthode de suppression pour les logs (données).
     */
    @FXML
    private void doDeleteLogs() {
        doDelete(false);
    }

    /**
     * Supprime les alertes.
     * Appelle la méthode de suppression pour les alertes.
     */
    @FXML
    private void doDeleteAlerts() {
        doDelete(true);
    }

    /**
     * Supprime les logs ou les alertes en fonction du paramètre _isAlert.
     * Affiche une boîte de dialogue de confirmation avant la suppression.
     * En cas de réussite, met à jour l'historique depuis le fichier, la vue et
     * affiche un message de succès.
     * En cas d'échec, affiche un message d'erreur.
     *
     * @param _isAlert Booléen indiquant s'il s'agit de la suppression d'alertes
     *                 (true) ou de logs (false).
     */
    private void doDelete(boolean _isAlert) {
        String type = _isAlert ? "alertes" : "logs";

        if (AlertUtilities.confirmYesCancel(primaryStage, "Vider l'historique",
                "Voulez-vous vraiment supprimer l'historique des " + type + " ?", null, AlertType.CONFIRMATION)) {
            if (JsonReader.deleteJsonFile(_isAlert ? "fichier_alerte" : "fichier_logs")) {
                JsonReader.updateHistoryFromFile(primaryStage, false, _isAlert, obsList,
                        _isAlert ? null : listAllRoomsDatas, _isAlert ? listAllRoomsAlerts : null, comboBoxRooms);
                updateSceneByView();
                AlertUtilities.showAlert(primaryStage, "Opération réussie",
                        "L'historique des " + type + " a bien été supprimé.", null, AlertType.INFORMATION);
            } else {
                AlertUtilities.showAlert(primaryStage, "Opération échouée",
                        "Une erreur s'est produite lors de la suppression de l'historique.",
                        "Veuillez vérifier que le fichier des " + type
                                + " existe bien et est placé au même endroit que l'application (exécutable).",
                        AlertType.ERROR);
            }
        }
    }

    /**
     * Initialise le graphique avec le titre, les marges et les interactions
     * utilisateur.
     * Associe des actions en cliquant ou survolant le graphique pour afficher une
     * vue détaillée.
     *
     * @param _graph     Le graphique à initialiser.
     * @param _tittle    Le titre du graphique.
     * @param _dataName  Le nom des données du graphique.
     * @param _dataUnit  L'unité des données du graphique.
     * @param _gridPaneX Position X dans le GridPane.
     * @param _gridPaneY Position Y dans le GridPane.
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
                    listLargeGraphsStages, largeTxtSearch, true);
            GraphMaker.updateGraphData(largeGraph, searchedDatasByGraph, largeGraphViewDataName,
                    largeGraphViewDataUnit, comboBoxDateFormat.getValue(), false, false);
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
     * Met à jour l'historique des données ou des alertes en fonction du paramètre
     * _isAlert.
     * Réinitialise les listes des données ou des alertes, la liste observable et
     * les cellules de la ListView.
     * Effectue une recherche selon le paramètre currentSearch, sinon affiche toutes
     * les données ou alertes.
     * Met à jour les graphiques si la vue graphique est activée et qu'il s'agit de
     * l'historique des données.
     *
     * @param _isAlert Booléen indiquant s'il s'agit de mettre à jour les alertes
     *                 (true) ou les données (false).
     */
    private void updateHistory(boolean _isAlert) {
        if (_isAlert) {
            listSearchedAlerts.clear();
        } else {
            listSearchedDatas.clear();
        }
        obsList.clear();
        ListViewUtilities.updateSelectedElements(cbTemperature.isSelected(), cbHumidity.isSelected(),
                cbActivity.isSelected(), cbCo2.isSelected());
        if (_isAlert) {
            ListViewUtilities.setCellForAlert(lvHistory);
        } else {
            ListViewUtilities.setCellForData(lvHistory);
        }

        if (currentSearch == null || currentSearch.trim().isEmpty()) {
            if (_isAlert) {
                listSearchedAlerts.addAll(listAllRoomsAlerts);
                for (Alert alert : listAllRoomsAlerts) {
                    obsList
                            .add(alert.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
                }
            } else {
                listSearchedDatas.addAll(listAllRoomsDatas);
                for (Data data : listAllRoomsDatas) {
                    obsList.add(data.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
                }
            }
        } else {
            String[] roomsToSearch = currentSearch.split(",");
            if (_isAlert) {
                for (Alert alert : listAllRoomsAlerts) {
                    for (String room : roomsToSearch) {
                        if (alert.getId().toLowerCase().contains(room.trim().toLowerCase())) {
                            listSearchedAlerts.add(alert);
                            obsList.add(
                                    alert.toString(DateUtilities.transformDateFormat(comboBoxDateFormat.getValue())));
                            break;
                        }
                    }
                }
            } else {
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
        }
        if (!_isAlert && buttGraphView.isDisabled()) {
            GraphMaker.updateGraphData(graphTemperature, listSearchedDatas, "temperature", "°c",
                    comboBoxDateFormat.getValue(), false, false);
            GraphMaker.updateGraphData(graphHumidity, listSearchedDatas, "humidity", "%",
                    comboBoxDateFormat.getValue(), false, false);
            GraphMaker.updateGraphData(graphActivity, listSearchedDatas, "activity", "",
                    comboBoxDateFormat.getValue(), false, false);
            GraphMaker.updateGraphData(graphCo2, listSearchedDatas, "co2", "ppm",
                    comboBoxDateFormat.getValue(), false, false);
        }
    }

    /**
     * Crée un champ de texte pour la recherche dans la vue graphique étendue.
     * Initialise le champ avec le texte de recherche actuel s'il existe, sinon
     * affiche un message.
     *
     * @return Le champ de texte pour la recherche dans la vue graphique étendue.
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
     * Initialise le champ de recherche pour les événements de modification du
     * texte.
     * Met à jour les données du graphique étendu si applicable, sinon met à jour la
     * scène.
     *
     * @param _textField                   Le champ de texte à initialiser.
     * @param _listSearchedDatasLargeGraph La liste des données du graphique étendu.
     * @param _largeGraph                  Le graphique étendu à mettre à jour si
     *                                     applicable.
     */
    private void initTxtSearch(TextField _textField, ArrayList<Data> _listSearchedDatasLargeGraph,
            XYChart<String, Number> _largeGraph) {
        _textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (_largeGraph != null) {
                updateDataForLargeGraph(_listSearchedDatasLargeGraph, newValue.trim());
                GraphMaker.updateGraphData(_largeGraph, _listSearchedDatasLargeGraph, largeGraphViewDataName,
                        largeGraphViewDataUnit, comboBoxDateFormat.getValue(), false, false);
            } else {
                if (!currentSearch.toLowerCase().equals(newValue.trim().toLowerCase())) {
                    currentSearch = newValue.trim();
                    updateSceneByView();
                }
            }
        });
    }

    /**
     * Met à jour les données pour le graphique étendu en fonction des salles
     * recherchées.
     * Réinitialise la liste des données du graphique étendu avec les salles
     * correspondantes.
     *
     * @param _listSearched  La liste des données à mettre à jour pour le graphique
     *                       étendu.
     * @param _searchedRooms Les salles recherchées pour la mise à jour des données.
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
     * Gère l'action liée au bouton de surveillance de l'entrepôt.
     * Crée une instance de WharehouseMonitor et affiche la fenêtre.
     */
    @FXML
    private void doWharehouseMonitor() {
        WharehouseMonitor wharehouse = new WharehouseMonitor(primaryStage);
        wharehouse.show();
    }

    /**
     * Gère l'action liée au bouton de configuration.
     * Initialise une instance de Configuration et lance une animation de changement
     * de scène vers la configuration.
     */
    @FXML
    private void doConfiguration() {
        Configuration conf = new Configuration(primaryStage);
        conf.show();
    }

    /**
     * Gère l'action liée au bouton du menu principal.
     * Initialise et affiche le menu principal lors de l'action de quitter.
     */
    @FXML
    private void doMenu() {
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
        if (AlertUtilities.confirmYesCancel(primaryStage, "Quitter l'application ?",
                "Voulez-vous vraiment quitter l'application ?", null,
                AlertType.CONFIRMATION)) {
            closeLargeGraphsStages();
            PythonAndThreadManagement.stopPythonThread();
            primaryStage.close();
            System.exit(0);
        } else {
            _e.consume();
        }
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