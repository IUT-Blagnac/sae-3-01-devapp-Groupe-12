package application.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import application.model.Alert;
import application.model.Data;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Classe utilitaire pour la lecture et la manipulation de fichiers JSON.
 */
public class JsonReader {

    /**
     * Supprime les données d'un fichier Json spécifié.
     *
     * @param _nameFile nom du type de fichier à supprimer
     * 
     * @return Vrai si la suppression a réussi, sinon faux si la supression a échouée ou le fichier indiqué n'est pas trouvé.
     */
    public static boolean deleteJsonFile(String _nameFile) {
        try (FileInputStream fileInputStream = new FileInputStream("config.ini")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            String filePath = properties.getProperty(_nameFile) + ".json";
            if (isJsonFileOK(filePath)) {
                PrintWriter writer = new PrintWriter(filePath);
                writer.print(""); // Écrit une chaîne vide dans le fichier
                writer.close();
                fileInputStream.close();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Vérifie si le fichier JSON spécifié est valide et accessible en lecture.
     *
     * @param _jsonPathFile Chemin du fichier JSON.
     * @return Vrai si le fichier est accessible et valide, sinon faux.
     */
    private static boolean isJsonFileOK(String _jsonPathFile) {
        try {
            File jsonFile = new File(_jsonPathFile);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonFile);

            return jsonNode != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Met à jour l'historique à partir d'un fichier JSON, remplissant les listes de
     * données et d'alertes.
     *
     * @param _primaryStage       Stage principal de l'application.
     * @param isCurrentDatas      Indique si les données sont actuelles ou
     *                            historiques.
     * @param _isAlertFile        Indique si le fichier est un fichier d'alerte ou
     *                            un fichier de logs.
     * @param _olAll              Liste observable pour affichage des données.
     * @param _listAllRoomsDatas  Liste des données pour toutes les pièces.
     * @param _listAllRoomsAlerts Liste des alertes pour toutes les pièces.
     * @param _comboBoxRooms      ComboBox pour la sélection des pièces.
     */
    public static void updateHistoryFromFile(Stage _primaryStage, boolean _isCurrentDatas, boolean _isAlertFile,
            ObservableList<String> _olAll, List<Data> _listAllRoomsDatas, List<Alert> _listAllRoomsAlerts,
            ComboBox<String> _comboBoxRooms) {
        String filePath = "";
        try (FileInputStream fileInputStream = new FileInputStream("config.ini")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            if (!_isCurrentDatas) {
                filePath = properties.getProperty(_isAlertFile ? "fichier_alerte" : "fichier_logs")
                        + ".json";
            } else {
                filePath = properties.getProperty("fichier_donnees")
                        + ".json";

            }
            fileInputStream.close();
        } catch (IOException e) {
            if (_isAlertFile) {
                AlertUtilities.showAlert(_primaryStage, "Aucun fichier trouvé.",
                        "Aucune configuration existante trouvé.",
                        "Aucune configuration n'a pu être chargé, merci\nd'effectuer une sauvegarde du fichier de configuration.",
                        AlertType.ERROR);
                return;
            }
        }
        _olAll.clear();
        if (_isAlertFile) {
            if (_listAllRoomsAlerts != null && !_listAllRoomsAlerts.isEmpty()) {
                _listAllRoomsAlerts.clear();
            }
        } else {
            if (_listAllRoomsDatas != null && !_listAllRoomsDatas.isEmpty()) {
                _listAllRoomsDatas.clear();
            }
        }
        if (_comboBoxRooms != null) {
            _comboBoxRooms.getItems().clear();
            _comboBoxRooms.getItems().add("Toutes");
            _comboBoxRooms.setValue("Toutes");
        }
        if (filePath != null && filePath.length() > 0) {
            if (isJsonFileOK(filePath)) {
                try {
                    File jsonFile = new File(filePath);
                    FileInputStream inputStream = new FileInputStream(jsonFile);
                    if (jsonFile.canRead()) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(jsonFile);
                        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = rootNode.fields();
                        while (fieldsIterator.hasNext()) {
                            Map.Entry<String, JsonNode> entry = fieldsIterator.next();
                            String _roomId = entry.getKey();
                            JsonNode sensorDataArray = entry.getValue();
                            if (_isAlertFile) {
                                if (_listAllRoomsAlerts != null) {
                                    addAlertLog(_olAll, _listAllRoomsAlerts, _roomId, sensorDataArray);
                                }
                            } else {
                                if (_listAllRoomsDatas != null) {
                                    addDataLog(_olAll, _listAllRoomsDatas, _roomId, sensorDataArray);
                                }
                            }
                            if (_comboBoxRooms != null) {
                                _comboBoxRooms.getItems().add(_roomId);
                            }
                        }
                    } else {
                        AlertUtilities.showAlert(_primaryStage, "Erreur",
                                "Lecture des données impossible.",
                                "Erreur lors de la lecture des données sur le fichier JSON " + filePath,
                                AlertType.ERROR);
                    }
                    if (!_olAll.isEmpty()) {
                        Collections.reverse(_olAll);
                    }
                    inputStream.close();
                } catch (IOException e) {
                    AlertUtilities.showAlert(_primaryStage, "Erreur",
                            "Erreur lors de la lecture des données.",
                            "Erreur lors de la lecture des données sur le fichier JSON " + filePath
                                    + ".\nCode d'erreur : " + e,
                            AlertType.ERROR);
                }
            } else {
                System.out.println(filePath);
                AlertUtilities.showAlert(_primaryStage, "Erreur",
                        "Fichier JSON introuvable.",
                        "Le fichier JSON " + filePath
                                + " n'a pas été trouvé.\nMerci de vérifier le chemin d'accès du fichier ou veuillez lancer le script Python et attendre la réception de données.",
                        AlertType.INFORMATION);
            }
        }
    }

    /**
     * Récupère la valeur d'une donnée spécifique à partir d'un nœud JSON.
     *
     * @param _dataNode     Le nœud JSON contenant les données.
     * @param _dataName     Le nom de la donnée à récupérer.
     * @param _variableName Le nom de la variable de la donnée à récupérer.
     * @return La valeur de la donnée, ou null si elle est absente ou invalide.
     */
    private static Double getValue(JsonNode _dataNode, String _dataName, String _variableName) {
        try {
            return _dataNode.get(_dataName).get(_variableName).asDouble();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Ajoute des données de capteurs à la liste des données pour une pièce
     * spécifique.
     *
     * @param _olData            Liste observable pour affichage des données.
     * @param _listAllRoomsDatas Liste des données pour toutes les pièces.
     * @param _roomId            Identifiant de la pièce.
     * @param sensorDataArray    Données du capteur sous forme de nœud JSON.
     */
    private static void addDataLog(ObservableList<String> _olData, List<Data> _listAllRoomsDatas, String _roomId,
            JsonNode sensorDataArray) {
        for (JsonNode sensorDataNode : sensorDataArray) {
            try {
                Date date = DateUtilities.getDateFromString(sensorDataNode.get("date").asText());
                if (date != null) {
                    JsonNode dataNode = sensorDataNode.get("donnees");

                    Double temperature = getValue(dataNode, "temperature", "valeur");
                    Double avgTemperature = getValue(dataNode, "temperature", "moyenne");
                    Double humidity = getValue(dataNode, "humidity", "valeur");
                    Double avgHumidity = getValue(dataNode, "humidity", "moyenne");
                    Double activity = getValue(dataNode, "activity", "valeur");
                    Double avgActivity = getValue(dataNode, "activity", "moyenne");
                    Double co2 = getValue(dataNode, "co2", "valeur");
                    Double avgCo2 = getValue(dataNode, "co2", "moyenne");
                    Data data = new Data(_roomId, date, temperature, avgTemperature, humidity, avgHumidity, activity,
                            avgActivity, co2, avgCo2);
                    _listAllRoomsDatas.add(data);
                    _olData.add(data.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Ajoute des alertes à la liste des alertes pour une pièce spécifique.
     *
     * @param _olData             Liste observable pour affichage des données.
     * @param _listAllRoomsAlerts Liste des alertes pour toutes les pièces.
     * @param _roomId             Identifiant de la pièce.
     * @param sensorDataArray     Données du capteur sous forme de nœud JSON.
     */
    public static void addAlertLog(ObservableList<String> _olData, List<Alert> _listAllRoomsAlerts, String _roomId,
            JsonNode sensorDataArray) {
        for (JsonNode sensorDataNode : sensorDataArray) {
            Date date = DateUtilities.getDateFromString(sensorDataNode.get("date").asText());

            if (date != null) {
                JsonNode dataNode = sensorDataNode.get("donnees");

                Double temperature = getValue(dataNode, "temperature", "valeur");
                Double maxTemperature = getValue(dataNode, "temperature", "seuil_max");
                Double humidity = getValue(dataNode, "humidity", "valeur");
                Double maxHumidity = getValue(dataNode, "humidity", "seuil_max");
                Double activity = getValue(dataNode, "activity", "valeur");
                Double maxActivity = getValue(dataNode, "activity", "seuil_max");
                Double co2 = getValue(dataNode, "co2", "valeur");
                Double maxCo2 = getValue(dataNode, "co2", "seuil_max");

                Alert alert = new Alert(_roomId, date, temperature == null ? 0 : temperature,
                        maxTemperature == null ? 0 : maxTemperature, humidity == null ? 0 : humidity,
                        maxHumidity == null ? 0 : maxHumidity, activity == null ? 0 : activity,
                        maxActivity == null ? 0 : maxActivity, co2 == null ? 0 : co2, maxCo2 == null ? 0 : maxCo2);
                _listAllRoomsAlerts.add(alert);
                _olData.add(alert.toString());
            }
        }
    }
}