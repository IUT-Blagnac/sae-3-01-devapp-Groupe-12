package application.view;

import java.util.function.Consumer;

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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ConfigurationController {

    private Configuration configuration;
    private Stage primaryStage;

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
    private ChoiceBox cbTimeUnit;

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
    private boolean temperature;
    private boolean humidity;
    private boolean activity;
    private boolean co2;
    private int frequency;
    private double maxTemperature;
    private double maxHumidity;
    private double maxActivity;
    private double maxCo2;

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

    /**
     * Initializes the view elements and their respective functionalities.
     */
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

        cbTimeUnit.getItems().addAll("secondes", "minutes", "heures", "jours");
        cbTimeUnit.setStyle("-fx-font-size: 18px;");

        // Initialize listeners for text areas, file choosers, and other elements
        initTxtFieldListeners();
        setElementsByConf();
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
        this.connexionTestTask = new Task<Void>() {
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
        this.connexionTestTask.setOnSucceeded(e -> {
            this.loadingIconAnimation.stop();
            if (isConnected) {
                this.setNewIcon("SuccesIcon.png");
                AlertUtilities.showAlert(this.primaryStage, "Connexion établie.",
                        "Connexion réussie !", "La connexion au serveur MQTT a été établie.",
                        AlertType.INFORMATION);
            } else {
                this.setNewIcon("FailedIcon.png");
                AlertUtilities.showAlert(this.primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                        "Veuillez saisir les paramètres corrects du serveur MQTT.", AlertType.ERROR);
            }
        });
        this.connexionTestTask.setOnFailed(e -> {
            this.loadingIconAnimation.stop();
            this.setNewIcon("FailedIcon.png");
            Animations.stopLoadingAnimation(this.imgConnexion, this.loadingIconAnimation);
            AlertUtilities.showAlert(this.primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                    "Veuillez saisir les paramètres corrects pour votre serveur MQTT.", AlertType.ERROR);
        });
        this.connexionTestTask.setOnCancelled(e -> {
            this.loadingIconAnimation.stop();
        });
        this.connexionTestTask.setOnRunning(e -> {
            this.setNewIcon("LoadingIcon.jpg");
            this.loadingIconAnimation = Animations.startLoadingAnimation(this.imgConnexion);
        });
    }

    public static boolean testMQTTConnection(String host, int port) {
        String broker = String.format("tcp://%s:%d", host, port);
        String clientId = MqttClient.generateClientId();

        try {
            IMqttClient mqttClient = new MqttClient(broker, clientId);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // Temps d'attente pour la connexion
            connOpts.setConnectionTimeout(10);

            mqttClient.connect(connOpts);
            mqttClient.disconnect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    private void doConnectionTest() {
        if (this.connexionTestTask.isRunning()) {
            AlertUtilities.showAlert(this.primaryStage, "Erreur.", "Un test est déjà en cours. Veuillez patienter.",
                    "Veuillez attendre que le test en cours se termine.", AlertType.INFORMATION);
        } else {
            if (this.serverConfIsFilled.getValue()) {
                this.initConnexionTestTask();
                new Thread(this.connexionTestTask).start();
            } else {
                AlertUtilities.showAlert(this.primaryStage, "Opération impossible.",
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
        this.txtHost.setDisable(_disable);
        this.txtPort.setDisable(_disable);
    }

    /**
     * Sets a new icon for the loading icon element based on the provided image
     * name.
     * Updates the image and makes the loading icon visible.
     *
     * @param _imgName Name of the image file to be displayed.
     */
    private void setNewIcon(String _imgName) {
        this.imgConnexion.setRotate(0);
        String imagePath = "/application/images/" + _imgName;
        this.imgConnexion.setImage(new Image(getClass().getResourceAsStream(imagePath),
                this.imgConnexion.getFitWidth(), this.imgConnexion.getFitHeight(), true,
                true));
        this.imgConnexion.setVisible(true);
    }

    /**
     * Sets the elements in the configuration view based on the existing
     * configuration data.
     * Also performs validations for certain fields.
     */
    private void setElementsByConf() {
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
                try {
                    port = Integer.parseInt(newValue);
                    portIsFilled.setValue(port <= 0 ? false : true);
                } catch (NumberFormatException e) {
                    portIsFilled.setValue(false);
                }
            }
        });
        setupTextValidation(txtHost, hostIsFilled, imgUndefinedHost);
        setupTextValidation(txtPort, portIsFilled, imgUndefinedPort);

        setupTextValidation(txtTopic, 200, "^[\\p{L}0-9.,]*$", alertFile);
        setupTextValidation(txtAlertFile, 15, "^[a-zA-Z]*$", alertFile);
        setupTextValidation(txtDataFile, 15, "^[a-zA-Z]*$", dataFile);
        setupTextValidation(txtLogsFile, 15, "^[a-zA-Z]*$", logsFile);

        setupNumberTextValidation(txtFrequency, 7, "\\d*", frequency);
        setupNumberTextValidation(txtMaxTemperature, 7, "-?\\d*", maxTemperature);
        setupNumberTextValidation(txtMaxHumidity, 7, "-?\\d*", maxHumidity);
        setupNumberTextValidation(txtMaxActivity, 7, "-?\\d*", maxActivity);
        setupNumberTextValidation(txtMaxCo2, 7, "-?\\d*", maxCo2);

        setupCheckBoxListener(cbTemperature, temperature);
        setupCheckBoxListener(cbHumidity, humidity);
        setupCheckBoxListener(cbActivity, activity);
        setupCheckBoxListener(cbCo2, co2);
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

    private void setupTextValidation(TextField textField, int maxLength, String regex, Object value) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength || !newValue.matches(regex)) {
                textField.setText(oldValue);
            } else {
                updateValue(value, newValue.trim());
            }
        });
    }

    private void setupNumberTextValidation(TextField textField, int maxLength, String regex, Object value) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && (newValue.length() > maxLength || !newValue.matches(regex))) {
                textField.setText(oldValue);
            } else {
                try {
                    updateValue(value, newValue.trim());
                } catch (NumberFormatException e) {
                    textField.setText(oldValue);
                }
            }
        });
    }

    private void setupCheckBoxListener(CheckBox checkBox, Object value) {
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateValue(value, newValue);
        });
    }

    private void updateValue(Object _oldValue, Object _newValue) {
        _oldValue = _newValue;
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