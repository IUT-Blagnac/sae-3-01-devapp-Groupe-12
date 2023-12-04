package application.view;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import javafx.scene.control.ListView;
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

    private final String alertFilePath = "code\\IOT\\Python\\alertes.json";
    private final String logsFilePath = "code\\IOT\\Python\\logs.json";

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private long lastCheckedTime = 0;

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
        executor.scheduleAtFixedRate(this::checkFileForUpdates, 0, 1, TimeUnit.SECONDS);

        updateHistory();
    }

    private void checkFileForUpdates() {
        File file = new File(logsFilePath);
        long lastModifiedTime = file.lastModified();

        if (lastModifiedTime > lastCheckedTime) {
            lastCheckedTime = lastModifiedTime;
            updateHistory();
        }
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
                    double humidity = dataNode.get("humidity").get("valeur").asDouble();
                    int activity = dataNode.get("activity").get("valeur").asInt();
                    int co2 = dataNode.get("co2").get("valeur").asInt();

                    String item = sensorId + " - Date: " + date + ", Temp: " + temperature + ", Humidity: " + humidity
                            + ", Activity: " + activity + ", CO2: " + co2;
                    olLogs.add(item);
                }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        lvHistory.setItems(olLogs);
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
