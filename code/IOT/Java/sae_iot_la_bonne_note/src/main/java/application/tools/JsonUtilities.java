package application.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

public class JsonUtilities {

    public static boolean isJsonFileOK(String _jsonPathFile) {
        try {
            File jsonFile = new File(_jsonPathFile);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonFile);

            return jsonNode != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static Double getValue(JsonNode _dataNode, String _dataName, String _variableName) {
        try {
            return _dataNode.get(_dataName).get(_variableName).asDouble();
        } catch (Exception e) {
            return null;
        }
    }

    public static void updateHistoryFromFile(Stage _primaryStage, boolean isCurrentDatas, boolean _isAlertFile,
            ObservableList<String> _olAll, List<Data> _listAllRoomsDatas, List<Alert> _listAllRoomsAlerts,
            ComboBox<String> _comboBoxRooms) {
        String filePath = "";
        try (FileInputStream fileInputStream = new FileInputStream("code\\IOT\\Python\\config.ini")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            if (!isCurrentDatas) {
                filePath = "code\\IOT\\Python\\"
                        + properties.getProperty(_isAlertFile ? "fichier_alerte" : "fichier_logs")
                        + ".json";
            } else {
                filePath = "code\\IOT\\Python\\"
                        + properties.getProperty("fichier_donnees")
                        + ".json";
            }
            fileInputStream.close();
        } catch (IOException e) {
            AlertUtilities.showAlert(_primaryStage, "Aucun fichier trouvé.", "Aucune configuration existante trouvé.",
                    "Aucune configuration n'a pu être chargé, merci d'effectuer une sauvegarde du fichier de configuration.",
                    AlertType.ERROR);
            return;
        }
        _olAll.clear();
        if (_isAlertFile) {
            if (_listAllRoomsAlerts != null) {
                _listAllRoomsAlerts.clear();
            }
        } else {
            if (_listAllRoomsDatas != null) {
                _listAllRoomsDatas.clear();
            }
        }
        _comboBoxRooms.getItems().clear();
        _comboBoxRooms.getItems().add("Toutes");
        _comboBoxRooms.setValue("Toutes");
        if (JsonUtilities.isJsonFileOK(filePath)) {
            try {
                File jsonFile = new File(filePath);
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
                                JsonUtilities.addAlertLog(_olAll, _listAllRoomsAlerts, _roomId, sensorDataArray);
                            }
                        } else {
                            if (_listAllRoomsDatas != null) {
                                JsonUtilities.addDataLog(_olAll, _listAllRoomsDatas, _roomId, sensorDataArray);
                            }
                        }
                        _comboBoxRooms.getItems().add(_roomId);
                    }
                } else {
                    AlertUtilities.showAlert(_primaryStage, "Erreur",
                            "Lecture des données impossible.",
                            "Erreur lors de la lecture des données sur le fichier JSON " + filePath, AlertType.ERROR);
                }
                if (!_olAll.isEmpty()) {
                    Collections.reverse(_olAll);
                }
            } catch (IOException e) {
                AlertUtilities.showAlert(_primaryStage, "Erreur",
                        "Erreur lors de la lecture des données.",
                        "Erreur lors de la lecture des données sur le fichier JSON " + filePath
                                + ".\nCode d'erreur : " + e,
                        AlertType.ERROR);
            }
        } else {
            AlertUtilities.showAlert(_primaryStage, "Erreur",
                    "Fichier JSON introuvable.", "Le fichier JSON " + filePath + " n'a pas été trouvé.",
                    AlertType.ERROR);
        }
    }

    public static void addDataLog(ObservableList<String> _olData, List<Data> _listAllRoomsDatas, String _roomId,
            JsonNode sensorDataArray) {
        for (JsonNode sensorDataNode : sensorDataArray) {
            try {
                Date date = DateUtilities.getDateFromString(sensorDataNode.get("date").asText());
                if (date != null) {
                    JsonNode dataNode = sensorDataNode.get("donnees");

                    Double temperature = JsonUtilities.getValue(dataNode, "temperature", "valeur");
                    Double humidity = JsonUtilities.getValue(dataNode, "humidity", "valeur");
                    Double activity = JsonUtilities.getValue(dataNode, "activity", "valeur");
                    Double co2 = JsonUtilities.getValue(dataNode, "co2", "valeur");
                    Data data = new Data(_roomId, date, temperature == null ? null : temperature,
                            humidity == null ? null : humidity, activity == null ? null : activity,
                            co2 == null ? null : co2);
                    _listAllRoomsDatas.add(data);
                    _olData.add(data.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    public static void addAlertLog(ObservableList<String> _olData, List<Alert> _listAllRoomsAlerts, String _roomId,
            JsonNode sensorDataArray) {
        for (JsonNode sensorDataNode : sensorDataArray) {
            Date date = DateUtilities.getDateFromString(sensorDataNode.get("date").asText());

            if (date != null) {
                JsonNode dataNode = sensorDataNode.get("donnees");

                Double temperature = JsonUtilities.getValue(dataNode, "temperature", "valeur");
                Double maxTemperature = JsonUtilities.getValue(dataNode, "temperature", "seuil_max");
                Double humidity = JsonUtilities.getValue(dataNode, "humidity", "valeur");
                Double maxHumidity = JsonUtilities.getValue(dataNode, "humidity", "seuil_max");
                Double activity = JsonUtilities.getValue(dataNode, "activity", "valeur");
                Double maxActivity = JsonUtilities.getValue(dataNode, "activity", "seuil_max");
                Double co2 = JsonUtilities.getValue(dataNode, "co2", "valeur");
                Double maxCo2 = JsonUtilities.getValue(dataNode, "co2", "seuil_max");

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