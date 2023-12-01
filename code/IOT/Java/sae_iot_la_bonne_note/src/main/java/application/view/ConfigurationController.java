package application.view;



import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import application.control.Configuration;
import application.control.MainMenu;
import application.tools.AlertUtilities;
import application.visualEffects.Animations;
import application.visualEffects.Style;
import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ConfigurationController {

    private Configuration configuration;
    private Stage primaryStage;

    private final String confFilePath = "code\\IOT\\Python\\config.ini";
    private Properties properties = new Properties();

    @FXML
    private Button buttReset;

    @FXML
    private Button buttLeave;

    @FXML
    private Button buttConfirm;

    @FXML
    private TextField txtHost;
    @FXML
    private ImageView imgUndefinedHost;

    @FXML
    private TextField txtPort;
    @FXML
    private ImageView imgUndefinedPort;

    @FXML
    private ImageView imgConnexion;
    private RotateTransition loadingIconAnimation;

    @FXML
    private ImageView imgInfoTopic;

    private final Tooltip tooltipTopic = new Tooltip(
            "Rentrer le nom des salles à surveiller en séparant chaque salle par une virgule.\nExemple : \"B104,B105,B106\". Entrer \"#\" pour surveiller toutes les salles.");

    @FXML
    private TextField txtTopic;

    @FXML
    private TextField txtAlertFile;

    @FXML
    private TextField txtDataFile;

    @FXML
    private TextField txtLogsFile;

    @FXML
    private CheckBox cbTemperature;

    @FXML
    private CheckBox cbHumidity;

    @FXML
    private CheckBox cbActivity;

    @FXML
    private CheckBox cbCo2;

    @FXML
    private ChoiceBox<String> cbTimeUnit;

    @FXML
    private TextField txtFrequency;

    @FXML
    private TextField txtMaxTemperature;

    @FXML
    private TextField txtMaxHumidity;

    @FXML
    private TextField txtMaxActivity;

    @FXML
    private TextField txtMaxCo2;

    private String host;
    private BooleanProperty hostIsFilled = new SimpleBooleanProperty();

    private int port;
    private BooleanProperty portIsFilled = new SimpleBooleanProperty();

    private BooleanProperty serverConfIsFilled = new SimpleBooleanProperty();

    private Task<Void> connexionTestTask;
    private boolean isConnected;

    private String topic;
    private String alertFile;
    private String dataFile;
    private String logsFile;
    private String donneesDeBase;
    private String seuilAlerte;
    private String frequence;
    private String typeDuTemps;
    private String tpTemps;  
    private boolean temperature;
    private boolean humidity;
    private boolean activity;
    private boolean co2;
    private int frequency;
    private int maxTemperature;
    private int maxHumidity;
    private int maxActivity;
    private int maxCo2;

    public void initContext(Configuration _configuration, Stage _primaryStage) {
        configuration = _configuration;
        primaryStage = _primaryStage;
        BooleanBinding combinedBinding = Bindings.and(hostIsFilled, portIsFilled);
        serverConfIsFilled.bind(combinedBinding);
        hostIsFilled.set(true);
        portIsFilled.set(true);

        initViewElements();

        initConnexionTestTask();
    }

    private void initViewElements() {
        // Sets action on closing the primary stage
        primaryStage.setOnCloseRequest(e -> {
            doLeave();
        });

        BooleanBinding combinedBinding = Bindings.and(hostIsFilled, portIsFilled);
        serverConfIsFilled.bind(combinedBinding);
        hostIsFilled.set(true);
        portIsFilled.set(true);

        // Tooltip settings and installations
        tooltipTopic.setStyle("-fx-font-size: 18px;");
        tooltipTopic.setShowDelay(Duration.ZERO);
        tooltipTopic.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(imgInfoTopic, tooltipTopic);

        cbTimeUnit.getItems().addAll("seconde(s)", "minute(s)", "heure(s)", "jour(s)");
        cbTimeUnit.setStyle("-fx-font-size: 18px;");

        setElementsByConf();

        // Initialize listeners for text areas, file choosers, and other elements
        initTxtFieldListeners();

    }

    private void updateStyle(Node node, boolean isFilled, BooleanProperty boolProp, ImageView img) {
        // boolProp.set(isFilled);
        if (!isFilled) {
            Style.setUndefinedTextAreaStyle(node);
            img.setVisible(true);
        } else {
            Style.resetTextAreaStyle(node);
            img.setVisible(false);
        }
    }

    private void initConnexionTestTask() {
        connexionTestTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    disableMinConfWhileTest(true);
                    isConnected = testMQTTConnection(host, port);
                    disableMinConfWhileTest(false);
                } catch (Exception e) {
                    cancel();
                }
                return null;
            }
        };
        connexionTestTask.setOnSucceeded(e -> {
            loadingIconAnimation.stop();
            if (isConnected) {
                setNewIcon("SuccesIcon.png");
                AlertUtilities.showAlert(primaryStage, "Connexion établie.",
                        "Connexion réussie !", "La connexion au serveur MQTT a été établie.",
                        AlertType.INFORMATION);
            } else {
                setNewIcon("FailedIcon.png");
                AlertUtilities.showAlert(primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                        "Veuillez saisir les paramètres corrects du serveur MQTT.", AlertType.ERROR);
            }
        });
        connexionTestTask.setOnFailed(e -> {
            loadingIconAnimation.stop();
            setNewIcon("FailedIcon.png");
            Animations.stopLoadingAnimation(imgConnexion, loadingIconAnimation);
            AlertUtilities.showAlert(primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                    "Veuillez saisir les paramètres corrects pour votre serveur MQTT.", AlertType.ERROR);
        });
        connexionTestTask.setOnCancelled(e -> {
            loadingIconAnimation.stop();
        });
        connexionTestTask.setOnRunning(e -> {
            setNewIcon("LoadingIcon.jpg");
            loadingIconAnimation = Animations.startLoadingAnimation(imgConnexion);
        });
    }

    public static boolean testMQTTConnection(String host, int port) {
        String broker = String.format("tcp://%s:%d", host, port);
        String clientId = MqttClient.generateClientId();

        try {
            IMqttClient mqttClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            connOpts.setConnectionTimeout(10);

            mqttClient.connect(connOpts);
            mqttClient.disconnect();
            mqttClient.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void doConnectionTest() {
        if (connexionTestTask.isRunning()) {
            AlertUtilities.showAlert(primaryStage, "Erreur.", "Un test est déjà en cours. Veuillez patienter.",
                    "Veuillez attendre que le test en cours se termine.", AlertType.INFORMATION);
        } else {
            if (serverConfIsFilled.getValue()) {
                initConnexionTestTask();
                new Thread(connexionTestTask).start();
            } else {
                AlertUtilities.showAlert(primaryStage, "Opération impossible.",
                        "Impossible d'initier le test de connexion.",
                        "Veuillez remplir tous les champs requis pour le test ! (en rouge)",
                        AlertType.INFORMATION);
            }
        }
    }

    /**
     * Disables specified elements during the test connection process.
     *
     * @param _disable Boolean value to enable or disable elements.
     */
    private void disableMinConfWhileTest(boolean _disable) {
        txtHost.setDisable(_disable);
        txtPort.setDisable(_disable);
    }

    /**
     * Sets a new icon for the loading icon element based on the provided image
     * name.
     * Updates the image and makes the loading icon visible.
     *
     * @param _imgName Name of the image file to be displayed.
     */
    private void setNewIcon(String _imgName) {
        imgConnexion.setRotate(0);
        String imagePath = "/application/images/" + _imgName;
        imgConnexion.setImage(new Image(getClass().getResourceAsStream(imagePath),
                imgConnexion.getFitWidth(), imgConnexion.getFitHeight(), true,
                true));
        imgConnexion.setVisible(true);
    }

    private void setElementsByConf() {
        if (checkConfFile()) {
            host = properties.getProperty("broker");
            txtHost.setText(host == null ? "" : host);
            port = getIntFromString(properties.getProperty("port"));
            txtPort.setText(port == 0 ? "" : "" + port);
            topic = properties.getProperty("topic");
            if (topic != null) {
                if (topic.contains("AM107/by-room/#")) {
                    txtTopic.setText("#");
                } else {
                    String[] rooms = topic.split(",");
                    List<String> roomNames = new ArrayList<>();
                    for (String room : rooms) {
                        String[] parts = room.split("/");
                        if (parts.length >= 3) {
                            String roomName = parts[2];
                            roomNames.add(roomName);
                        }
                    }
                    String concatenatedRooms = String.join(",", roomNames);
                    txtTopic.setText(concatenatedRooms);
                }
            }
            alertFile = properties.getProperty("fichier_alerte");
            txtAlertFile.setText(alertFile == null ? "" : alertFile);
            dataFile = properties.getProperty("fichier_donnees");
            txtDataFile.setText(dataFile == null ? "" : dataFile);
            logsFile = properties.getProperty("fichier_logs");
            txtLogsFile.setText(logsFile == null ? "" : logsFile);
            donneesDeBase = properties.getProperty("choix_donnees");
            
            if (donneesDeBase.contains("temperature")){
                cbTemperature.setSelected(true);
            }
            if(donneesDeBase.contains("humidity")){
                cbHumidity.setSelected(true);
            }
            if(donneesDeBase.contains("activity")){
                cbActivity.setSelected(true);
            }
            if(donneesDeBase.contains("co2")){
                cbCo2.setSelected(true);
            }
            frequence = properties.getProperty("frequence_affichage");
            //int freq = getIntFromString(frequence);
            typeDuTemps = properties.getProperty("typeTemps");
            cbTimeUnit.setValue(typeDuTemps);

        
        } else {
            AlertUtilities.showAlert(primaryStage, "Aucun fichier trouvé.",
                    "Aucune configuration existante trouvé.",
                    "Aucune configuration n'a pu être chargé.", AlertType.INFORMATION);
        }
    }

    private boolean checkConfFile() {
        try (FileInputStream fileInputStream = new FileInputStream(confFilePath)) {
            properties.load(fileInputStream);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @FXML
    private void doValider() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(confFilePath))) {
            setNewValues();
            properties.clear();
            writer.write("[MQTT]\n");
            writer.write("broker=" + host + "\n");
            writer.write("port=" + String.valueOf(port) + "\n");
            writer.write("topic=" + topic + "\n");
            writer.write("[CONFIG]\n");
            writer.write("fichier_alerte=" + alertFile + "\n");
            writer.write("fichier_donnees=" + dataFile + "\n");
            writer.write("fichier_logs=" + logsFile + "\n");
            String choixDonnee="";
            if(cbTemperature.isSelected()){
                choixDonnee+= "temperature," + " ";
            }
            if(cbHumidity.isSelected()){
                choixDonnee+= "humidity," + " ";
            }
            if(cbActivity.isSelected()){
                choixDonnee+= "activity," + " ";
            }
            if(cbCo2.isSelected()){
                choixDonnee+= "co2," + " ";
            }
            writer.write("choix_donnees=" + choixDonnee + "\n");

            tpTemps = cbTimeUnit.getValue();
            if(tpTemps == "minute(s)"){
                frequency = getIntFromString(txtFrequency.getText()) * 60;
                //frequency = frequency * 60;
                writer.write("typeTemps=" + tpTemps + "\n");
            }
            if(tpTemps == "heure(s)"){
                frequency = getIntFromString(txtFrequency.getText()) * 3600;
                writer.write("typeTemps=" + tpTemps + "\n");
            }
            if(tpTemps == "jour(s)"){
                frequency = getIntFromString(txtFrequency.getText()) * 86400;
                writer.write("typeTemps=" + tpTemps + "\n");
            }
            writer.write("frequence_affichage=" + frequency + "\n");
            writer.write("[ALERT]\n");
            writer.write("seuil_Temperature=" + maxTemperature + "\n");
            writer.write("seuil_Humidity=" + maxHumidity + "\n");
            writer.write("seuil_CO2=" + maxCo2 + "\n");
            writer.write("seuil_Activity=" + maxActivity + "\n");
            AlertUtilities.showAlert(primaryStage, "Opération réussie.",
                    "Sauvegarde effectuée !",
                    "La configuration a bien été sauvegardé.", AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtilities.showAlert(primaryStage, "Opération échouée.",
                    "Une erreur est survenue !",
                    "Une erreur est survenue lors de la sauvegarde.", AlertType.INFORMATION);
        }
    }

    private void setNewValues() {
        topic = txtTopic.getText().trim();
        alertFile = txtAlertFile.getText().trim();
        dataFile = txtDataFile.getText().trim();
        logsFile = txtLogsFile.getText().trim();
        frequency = getIntFromString(txtFrequency.getText().trim());
        maxTemperature = getIntFromString(txtMaxTemperature.getText().trim());
        maxActivity = getIntFromString(txtMaxActivity.getText().trim());
        maxHumidity = getIntFromString(txtMaxHumidity.getText().trim());
        maxCo2 = getIntFromString(txtMaxCo2.getText().trim());
    }

    private int getIntFromString(String _string) {
        try {
            int val = Integer.parseInt(_string);
            return val;
        } catch (Exception e) {
            return 0;
        }
    }

    private void initTxtFieldListeners() {
        txtHost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                hostIsFilled.setValue(false);
            } else if (newValue.trim().length() > 60) {
                txtPort.setText(oldValue);
            } else {
                hostIsFilled.setValue(true);
                host = newValue.trim();
            }
        });
        txtPort.textProperty().addListener((observable, oldValue, newValue) -> {
            newValue = newValue.trim();
            if (newValue.isEmpty()) {
                portIsFilled.setValue(false);
            } else if (newValue.length() > 7 || !newValue.matches("\\d*")) {
                txtPort.setText(oldValue);
            } else {
                port = getIntFromString(newValue);
                portIsFilled.setValue(port <= 0 ? false : true);
            }
        });
        setupTextValidation(txtHost, hostIsFilled, imgUndefinedHost);
        setupTextValidation(txtPort, portIsFilled, imgUndefinedPort);

        setupTextValidation(txtAlertFile, 15, "^[a-zA-Z]*$", alertFile);
        setupTextValidation(txtDataFile, 15, "^[a-zA-Z]*$", dataFile);
        setupTextValidation(txtLogsFile, 15, "^[a-zA-Z]*$", logsFile);

        setupNumberTextValidation(txtFrequency, 7, "\\d*", frequency);
        setupNumberTextValidation(txtMaxTemperature, 7, "-?\\d*", maxTemperature);
        setupNumberTextValidation(txtMaxHumidity, 7, "-?\\d*", maxHumidity);
        setupNumberTextValidation(txtMaxActivity, 7, "-?\\d*", maxActivity);
        setupNumberTextValidation(txtMaxCo2, 7, "-?\\d*", maxCo2);

        cbTemperature.selectedProperty().addListener((observable, oldValue, newValue) -> {
            temperature = newValue;
        });
        cbHumidity.selectedProperty().addListener((observable, oldValue, newValue) -> {
            humidity = newValue;
        });
        cbActivity.selectedProperty().addListener((observable, oldValue, newValue) -> {
            activity = newValue;
        });
        cbCo2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            co2 = newValue;
        });
    }

    private void setupTextValidation(TextField _txtField, BooleanProperty _boolProp, ImageView _img) {
        _boolProp.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateStyle(_txtField, true, _boolProp, _img);
            } else {
                updateStyle(_txtField, false, _boolProp, _img);
            }
        });
    }

    private void setupTextValidation(TextField textField, int maxLength, String regex, String value) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength || !newValue.matches(regex)) {
                textField.setText(oldValue);
            }
        });
    }

    private void setupNumberTextValidation(TextField textField, int maxLength, String regex, Object value) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && (newValue.length() > maxLength || !newValue.matches(regex))) {
                textField.setText(oldValue);
            }
        });
    }

    /*
     * Méthode associé au bouton FXML qui permet de fermer la fenêtre.
     * 
     */
    @FXML
    private void doLeave() {
        MainMenu menu = new MainMenu();
        menu.start(primaryStage);
        menu.show();
    }
}