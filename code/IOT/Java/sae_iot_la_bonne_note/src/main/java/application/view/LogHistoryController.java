package application.view;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import application.control.Configuration;
import application.control.LogHistory;
import application.control.MainMenu;
import application.control.WharehouseMonitor;
import application.tools.AlertUtilities;
import application.visualEffects.Animations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LogHistoryController {

    // Référence à la classe de l'historique
    private LogHistory logHistory;

    // Référence au stage de la fenêtre principale
    private Stage primaryStage;

    @FXML
    private Button buttMenu;

    @FXML
    private Button buttCheckWhareHouse;

    @FXML
    private Button buttCheckHistory;

    @FXML
    private Button buttConfiguration;

    @FXML
    private ListView<String> lvHistory;
    private ObservableList<String> olLogs = FXCollections.observableArrayList();
    private ArrayList<Data> listLogs = new ArrayList<>();

    @FXML
    private TextField txtSearch;

    private class Data {
        String id;
        Date date;
        double temperature;
        double humidity;
        double activity;
        double co2;

        private Data(String _id, Date _date, double _temperature, double _humidity, double _activity, double _co2) {
            this.id = _id;
            this.date = _date;
            this.temperature = _temperature;
            this.humidity = _humidity;
            this.activity = _activity;
            this.co2 = _co2;
        }
    }

    private final String alertFilePath = "code\\IOT\\Python\\alertes.json";
    private final String logsFilePath = "code\\IOT\\Python\\logs.json";

    /**
     * Initialise le contrôleur de vue LogHistoryController.
     *
     * @param _mainMenu     L'instance du menu principal.
     * @param _primaryStage La scène principale associée au contrôleur.
     */
    public void initContext(LogHistory _mainMenu, Stage _primaryStage) {
        this.logHistory = _mainMenu;
        this.primaryStage = _primaryStage;
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        // Configure les animations pour les boutons
        Animations.setAnimatedButton(buttMenu, 1.1, 1, 100);
        Animations.setAnimatedButton(buttCheckWhareHouse, 1.1, 1, 100);
        Animations.setAnimatedButton(buttConfiguration, 1.1, 1, 100);
        Animations.setSelectedMenuAnimation(buttCheckHistory, 0.5, 0.8, 1000);
        // executor.scheduleAtFixedRate(this::checkFileForUpdates, 0, 1,
        // TimeUnit.SECONDS);

        updateHistory();
    }

    @FXML
    private void updateHistory() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(logsFilePath);

            JsonNode rootNode = objectMapper.readTree(file);

            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = fieldsIterator.next();
                String sensorId = entry.getKey();
                JsonNode sensorDataArray = entry.getValue();

                for (JsonNode sensorDataNode : sensorDataArray) {
                    String dateString = sensorDataNode.get("date").asText();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    Date date = sdf.parse(dateString);

                    JsonNode dataNode = sensorDataNode.get("donnees");

                    double temperature = dataNode.get("temperature").get("valeur").asDouble();
                    double humidite = dataNode.get("humidity").get("valeur").asDouble();
                    int activite = dataNode.get("activity").get("valeur").asInt();
                    int co2 = dataNode.get("co2").get("valeur").asInt();

                    String element = " " + sensorId + ",  " + sdf.format(date) + ",  Température : " + temperature
                            + ",  Humidité : " + humidite + ",  Activité : " + activite + ",  Co2 : " + co2;
                    listLogs.add(new Data(sensorId, date, temperature, humidite, activite, co2));
                    olLogs.add(element);
                }

            }
            lvHistory.setCellFactory(param -> new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null); // Réinitialise le contenu graphique
                    } else {
                        String[] data = item.split(", "); // Sépare les différentes données

                        HBox hbox = new HBox(); // Crée une HBox pour contenir les labels
                        hbox.setSpacing(5);

                        Label label = new Label(data[0] + " ");
                        label.getStyleClass().add("labelRoom");
                        hbox.getChildren().add(label);
                        label = new Label(data[1] + " ");
                        label.getStyleClass().add("labelDate");
                        hbox.getChildren().add(label);

                        for (int i = 2; i < data.length; i++) {
                            String dat = data[i];
                            label = new Label(dat + " ");
                            if (dat.contains("Date")) {
                                label.getStyleClass().add("labelDate");
                            } else if (dat.contains("Température")) {
                                label.getStyleClass().add("labelTemperature");
                            } else if (dat.contains("Humidité")) {
                                label.getStyleClass().add("labelHumidity");
                            } else if (dat.contains("Activité")) {
                                label.getStyleClass().add("labelActivity");
                            } else if (dat.contains("Co2")) {
                                label.getStyleClass().add("labelCo2");
                            }
                            hbox.getChildren().add(label); // Ajoute le label à la HBox
                        }
                        setGraphic(hbox); // Affiche la HBox dans la cellule
                    }
                }
            });
            // System.out.println(listLogs.get(0).toString());
            lvHistory.setItems(olLogs);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private String randomColorString() {
        double red = Math.random();
        double green = Math.random();
        double blue = Math.random();
        return String.format("#%02X%02X%02X",
                (int) (red * 255),
                (int) (green * 255),
                (int) (blue * 255));
    }

    @FXML
    private void doWharehouseMonitor() {
        Animations.sceneSwapAnimation(buttCheckWhareHouse, 1.15, 50, () -> {
            WharehouseMonitor wharehouse = new WharehouseMonitor(primaryStage);
            wharehouse.show();
        });
    }

    /**
     * Gère l'action liée au bouton de configuration.
     * Lance une animation de changement de scène vers la configuration.
     */
    @FXML
    private void doConfiguration() {
        Animations.sceneSwapAnimation(buttConfiguration, 1.15, 50, () -> {
            Configuration conf = new Configuration(primaryStage);
            conf.show();
        });
    }

    /**
     * Méthode associée au bouton FXML qui permet de fermer la fenêtre.
     * Initialise et affiche le menu principal lors de l'action de quitter.
     */
    @FXML
    private void doMenu() {
        Animations.sceneSwapAnimation(buttMenu, 1.15, 100, () -> {
            MainMenu menu = new MainMenu();
            menu.start(primaryStage);
            menu.show();
        });
    }

    /**
     * Méthode de fermeture de la fenêtre par la croix.
     *
     * @param _e L'événement de fermeture de fenêtre.
     */
    private void closeWindow(WindowEvent _e) {
        if (AlertUtilities.confirmYesCancel(this.primaryStage, "Quitter l'application",
                "Etes-vous sûr de vouloir quitter le jeu ?", null, AlertType.CONFIRMATION)) {
            this.primaryStage.close();
        }
        _e.consume();
    }
}