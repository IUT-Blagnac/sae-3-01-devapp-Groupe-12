package application.view;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import application.control.Configuration;
import application.control.LogHistory;
import application.control.MainMenu;
import application.control.WharehouseMonitor;
import application.tools.AlertUtilities;
import application.tools.Animations;
import application.tools.MQTTConnection;
import application.tools.NumbersUtilities;
import application.tools.PythonAndThreadManagement;
import application.tools.Style;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * Contrôleur de la fenêtre de configuration.
 * Ce contrôleur gère les interactions et la logique de la fenêtre de
 * configuration du fichier dans l'application.
 */
public class ConfigurationController {

    // Référence à la classe Configuration
    private Configuration configuration;

    // Référence au stage de la fenêtre principale
    private Stage primaryStage;

    // Chemin vers le fichier de configuration
    private final String confFilePath = "config.ini";

    // Propriétés pour stocker des configurations
    private Properties properties = new Properties();

    // Éléments FXML
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
    private Button buttTestConnection;

    @FXML
    private Button buttReset;

    @FXML
    private Button buttSave;

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

    // Animation pour l'icône de chargement
    private RotateTransition loadingIconAnimation;

    @FXML
    private ImageView imgInfoTopic;

    // Tooltip associé à txtTopic
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
    private ComboBox<String> cbTimeUnit;

    @FXML
    private TextField txtFrequency;

    @FXML
    private CheckBox cbSoundOn;

    @FXML
    private Label labSoundOn;

    @FXML
    private Label labSoundlvl;

    @FXML
    private Slider sliderSound;

    @FXML
    private Label labSoundLvlValue;

    @FXML
    private TextField txtMaxTemperature;

    @FXML
    private TextField txtMaxHumidity;

    @FXML
    private TextField txtMaxActivity;

    @FXML
    private TextField txtMaxCo2;

    // Variables pour stocker les valeurs des champs de l'IHM

    private String host;
    private boolean hostIsFilled;

    private int port;
    private boolean portIsFilled;

    // Thread de test de connexion
    private Thread connexionTestThread;

    // État de la connexion
    private boolean isConnected;
    // État du test de connexion
    private boolean isTestRunning;

    private String topic;
    private String alertFile;
    private String dataFile;
    private String logsFile;
    private String donneesDeBase;
    private String typeDuTemps;
    private String tpTemps;
    private Double frequency;
    private Double maxTemperature;
    private Double maxHumidity;
    private Double maxActivity;
    private Double maxCo2;

    /**
     * Initialise le contexte du contrôleur avec la configuration et le stage de la
     * fenêtre principale.
     * 
     * @param _configuration La configuration de l'application.
     * @param _primaryStage  Le stage de la fenêtre principale.
     */
    public void initContext(Configuration _configuration, Stage _primaryStage) {
        // Affecte la configuration et le stage reçus en paramètres aux variables
        // correspondantes
        configuration = _configuration;
        primaryStage = _primaryStage;

        PythonAndThreadManagement.initImgConnexionState(imgConnexionState);
        PythonAndThreadManagement.updateImgConnexionState();

        // Initialise les éléments visuels de l'IHM
        initViewElements();

        // Initialise le thread du test de la connexion
        initConnexionTestThread();
    }

    /**
     * Initialise les éléments de l'interface utilisateur (IHM) dans la fenêtre
     * principale.
     * Cette méthode configure les différentes actions et animations des éléments
     * IHM,
     * initialise les tooltips, les ChoiceBox, et met en place les valeurs initiales
     * à partir des configurations existantes.
     */
    private void initViewElements() {
        // Définit l'action à effectuer lorsque la fenêtre est fermée
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        // Applique des animations aux boutons de l'IHM
        Animations.setAnimatedButton(buttMenu, 1.1, 1, 100);
        Animations.setAnimatedButton(buttCheckWhareHouse, 1.1, 1, 100);
        Animations.setAnimatedButton(buttCheckHistory, 1.1, 1, 100);
        Animations.setAnimatedButton(buttTestConnection, 1.06, 1, 100);
        Animations.setAnimatedButton(buttReset, 1.06, 1, 100);
        Animations.setAnimatedButton(buttSave, 1.06, 1, 100);
        Animations.setSelectedMenuAnimation(buttConfiguration, 0.5, 0.8, 1000);

        // Initialise le style du Tooltip associé à txtTopic
        tooltipTopic.setStyle("-fx-font-size: 18px;");
        tooltipTopic.setShowDelay(Duration.ZERO);
        tooltipTopic.setShowDuration(Duration.INDEFINITE);

        // Installe le Tooltip sur l'ImageView imgInfoTopic
        Tooltip.install(imgInfoTopic, tooltipTopic);

        // Ajoute des éléments à la ChoiceBox cbTimeUnit et définit son style
        cbTimeUnit.getItems().addAll("seconde(s)", "minute(s)", "heure(s)", "jour(s)");
        cbTimeUnit.setStyle("-fx-font-size: 18px;");

        // Initialise les éléments de l'IHM à partir des configurations existantes
        setElementsByConf();

        // Initialise les écouteurs pour les champs de texte
        initListeners();
    }

    /**
     * Méthode associée au bouton FXML qui permet de fermer la fenêtre.
     * Initialise et affiche le menu principal lors de l'action de quitter.
     */
    @FXML
    private void doMenu() {
        MainMenu menu = new MainMenu();
        menu.start(primaryStage);
        menu.show();
    }

    /**
     * Démarre la surveillance de l'entrepôt en lançant la fenêtre dédiée.
     * Cette méthode réalise une animation de transition vers la surveillance de
     * l'entrepôt en créant une nouvelle fenêtre WharehouseMonitor.
     */
    @FXML
    private void doWharehouseMonitor() {
        WharehouseMonitor wharehouse = new WharehouseMonitor(primaryStage);
        wharehouse.show();
    }

    /**
     * Affiche l'historique des logs en lançant la fenêtre dédiée.
     * Cette méthode réalise une animation de transition vers l'historique des
     * journaux en créant une nouvelle fenêtre LogHistory.
     */
    @FXML
    private void doCheckHistory() {
        LogHistory history = new LogHistory(primaryStage);
        history.show();
    }

    /**
     * Effectue un test de connexion au serveur MQTT.
     * Si un test est déjà en cours, affiche une alerte informant l'utilisateur de
     * patienter. Sinon, initialise et lance un thread de test de connexion.
     */
    @FXML
    private void doConnectionTest() {
        if (isTestRunning) {
            // Si un test est déjà en cours, affiche une alerte pour informer l'utilisateur
            AlertUtilities.showAlert(primaryStage, "Erreur.", "Un test est déjà en cours. Veuillez patienter.",
                    "Veuillez attendre que le test en cours se termine.", AlertType.INFORMATION);
        } else {
            if (portIsFilled && hostIsFilled) {
                // Si tous les champs requis pour le test sont remplis, initialise et lance le
                // thread de test
                connexionTestThread.start();
            } else {
                // Si des champs requis pour le test sont vides, affiche une alerte pour
                // informer l'utilisateur
                AlertUtilities.showAlert(primaryStage, "Opération impossible.",
                        "Impossible d'initier le test de connexion.",
                        "Veuillez remplir tous les champs requis pour le test ! (en rouge)",
                        AlertType.INFORMATION);
            }
            initConnexionTestThread();
        }
    }

    /**
     * Réinitialise les champs de l'interface utilisateur avec les valeurs par
     * défaut. Affiche une confirmation avant la réinitialisation. En cas de
     * confirmation, réinitialise les champs et applique les nouvelles valeurs.
     */
    @FXML
    private void doReset() {
        // Affiche une alerte de confirmation
        if (AlertUtilities.confirmYesCancel(primaryStage, "Confirmation", "Réinitialiser la configuration ?",
                "Voulez vous vraiment réinitialiser ?", AlertType.CONFIRMATION)) {
            // Réinitialisation des champs de l'interface utilisateur avec les valeurs par
            // défaut
            this.txtHost.setText("chirpstack.iut-blagnac.fr");
            this.txtPort.setText("1883");
            this.txtTopic.setText("#");
            this.txtAlertFile.setText("alerte");
            this.txtDataFile.setText("donnees");
            this.txtLogsFile.setText("logs");
            this.cbTemperature.setSelected(true);
            this.cbHumidity.setSelected(true);
            this.cbActivity.setSelected(true);
            this.cbCo2.setSelected(true);
            this.cbTimeUnit.setValue("minute(s)");
            this.txtFrequency.setText("1");
            this.sliderSound.setValue(50);
            this.txtMaxTemperature.setText("0");
            this.txtMaxHumidity.setText("0");
            this.txtMaxActivity.setText("0");
            this.txtMaxCo2.setText("0");

            // Applique les nouvelles valeurs
            this.setNewValues();
        }
    }

    /**
     * Valide et sauvegarde les nouvelles valeurs spécifiées dans l'interface
     * utilisateur vers le fichier de configuration (crée le fichier s'il n'existe
     * pas).
     * Affiche une alerte en cas de succès ou d'échec de l'opération.
     */
    @FXML
    private void doSave() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(confFilePath))) {

            // Récupère les nouvelles valeurs spécifiées dans l'interface utilisateur
            setNewValues();

            // Remet à vide le fichier de configuration avant l'écriture des données
            properties.clear();

            // Écrit les nouvelles valeurs dans le fichier de configuration
            writer.write("[MQTT]\n");
            writer.write("broker=" + (host != null && host.length() > 1 ? host : "null") + "\n");
            writer.write("port=" + String.valueOf(port) + "\n");
            if (topic.equals("#") || topic.trim().equals("")) {
                topic = "AM107/by-room/#";
            } else {
                String[] rooms = topic.split(",");
                topic = "";
                for (int i = 0; i < rooms.length; i++) {
                    topic += "AM107/by-room/" + rooms[i] + "/data";
                    if (i + 1 < rooms.length) {
                        topic += ",";
                    }
                }

            }
            writer.write("topic=" + topic + "\n");
            writer.write("[CONFIG]\n");

            // écrit les noms des fichiers, attribue un nom par défaut s'ils sont nulles ou
            // vides
            writer.write(
                    "fichier_alerte=" + (alertFile != null && alertFile.length() > 0 ? alertFile : "alerte") + "\n");
            writer.write(
                    "fichier_donnees=" + (dataFile != null && dataFile.length() > 0 ? dataFile : "donnees") + "\n");
            writer.write("fichier_logs=" + (logsFile != null && logsFile.length() > 0 ? logsFile : "logs") + "\n");
            String choixDonnee = "";
            if (cbTemperature.isSelected()) {
                choixDonnee += "temperature,";
            }
            if (cbHumidity.isSelected()) {
                choixDonnee += "humidity,";
            }
            if (cbActivity.isSelected()) {
                choixDonnee += "activity,";
            }
            if (cbCo2.isSelected()) {
                choixDonnee += "co2,";
            }
            // Supprime la dernière virgule, si présente
            if (choixDonnee.endsWith(",")) {
                choixDonnee = choixDonnee.substring(0, choixDonnee.length() - 1);
            }
            if (choixDonnee.trim().isEmpty()) {
                writer.write("choix_donnees=" + "null" + "\n");
            } else {
                writer.write("choix_donnees=" + choixDonnee + "\n");
            }
            tpTemps = cbTimeUnit.getValue();
            if (frequency != null) {
                if (tpTemps == "minute(s)") {
                    frequency = NumbersUtilities.getDoubleFromString(txtFrequency.getText()) * 60;
                }
                if (tpTemps == "heure(s)") {
                    frequency = NumbersUtilities.getDoubleFromString(txtFrequency.getText()) * 3600;
                }
                if (tpTemps == "jour(s)") {
                    frequency = NumbersUtilities.getDoubleFromString(txtFrequency.getText()) * 86400;
                }
            }
            writer.write("typeTemps=" + tpTemps + "\n");
            writer.write("frequence_affichage=" + (frequency != null ? frequency : 0) + "\n");
            writer.write("[ALERT]\n");
            Double sound = null;
            if (cbSoundOn.isSelected()) {
                sound = sliderSound.getValue();
            }
            writer.write("son_Alertes=" + sound + "\n");
            if (maxTemperature != null) {
                writer.write("seuil_Temperature=" + maxTemperature + "\n");
            }
            if (maxHumidity != null) {
                writer.write("seuil_Humidity=" + maxHumidity + "\n");
            }
            if (maxCo2 != null) {
                writer.write("seuil_CO2=" + maxCo2 + "\n");
            }
            if (maxActivity != null) {
                writer.write("seuil_Activity=" + maxActivity + "\n");
            }

            // Arrêt du script python
            PythonAndThreadManagement.stopPythonThread();

            // Affiche une alerte en cas de succès de la sauvegarde
            AlertUtilities.showAlert(primaryStage, "Opération réussie.",
                    "Sauvegarde effectuée !",
                    "La configuration a bien été sauvegardé.\nSi une connexion MQTT était en cours, celle-ci a été arrêté, rendez-vous dans le menu \"Temps Réel pour la relancer\".",
                    AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtilities.showAlert(primaryStage, "Opération échouée.",
                    "Une erreur est survenue !",
                    "Une erreur est survenue lors de la sauvegarde, un nouveau fichier sera créé.\nCode d'erreur : "
                            + e,
                    AlertType.INFORMATION);
        }
    }

    /**
     * Charge les éléments de l'interface utilisateur à partir d'un fichier de
     * configuration existant, s'il est trouvé. Remplit les champs correspondants
     * avec les valeurs du fichier. Sinon, affiche une alerte informant qu'aucun
     * fichier de configuration n'a été trouvé.
     */
    private void setElementsByConf() {
        if (checkConfFile()) {
            // Récupère les valeurs depuis le fichier de configuration
            host = properties.getProperty("broker");
            txtHost.setText(host == null ? "" : host);
            hostIsFilled = host == null ? false : true;

            port = NumbersUtilities.getIntFromString(properties.getProperty("port"));
            txtPort.setText(port == 0 ? "" : "" + port);
            portIsFilled = port <= 0 ? false : true;

            if (!hostIsFilled || !portIsFilled) {
                Style.setNewIcon(imgConnexion, "failed_icon.png");
            }

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

            if (donneesDeBase.contains("temperature")) {
                cbTemperature.setSelected(true);
            }
            if (donneesDeBase.contains("humidity")) {
                cbHumidity.setSelected(true);
            }
            if (donneesDeBase.contains("activity")) {
                cbActivity.setSelected(true);
            }
            if (donneesDeBase.contains("co2")) {
                cbCo2.setSelected(true);
            }
            frequency = NumbersUtilities.getDoubleFromString(properties.getProperty("frequence_affichage"));
            txtFrequency.setText("" + frequency);
            typeDuTemps = properties.getProperty("typeTemps");
            if (typeDuTemps != null) {
                if (typeDuTemps.equals("minute(s)")) {
                    frequency /= 60;
                }
                if (typeDuTemps.equals("heure(s)")) {
                    frequency /= 3600;
                }
                if (typeDuTemps.equals("jour(s)")) {
                    frequency /= 86400;
                }
            } else {
                typeDuTemps = "seconde(s)";
            }
            this.cbTimeUnit.setValue("seconde(s)");
            Double soundLvl = NumbersUtilities.getDoubleFromString(properties.getProperty("son_Alertes"));
            if (soundLvl != null) {
                sliderSound.setValue(soundLvl.intValue());
            }
            cbSoundOn.setSelected(soundLvl == null ? false : true);
            labSoundOn.setText(cbSoundOn.isSelected() ? "Activé" : "Désactivé");
            labSoundlvl.setDisable(soundLvl == null ? true : false);
            sliderSound.setDisable(soundLvl == null ? true : false);
            labSoundLvlValue.setText(soundLvl == null ? "-" : "" + sliderSound.getValue());
            labSoundLvlValue.setDisable(soundLvl == null ? true : false);
            maxTemperature = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Temperature"));
            txtMaxTemperature.setText(maxTemperature == null ? "" : String.valueOf(maxTemperature));
            maxActivity = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Activity"));
            txtMaxActivity.setText(maxActivity == null ? "" : String.valueOf(maxActivity));
            maxCo2 = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_CO2"));
            txtMaxCo2.setText(maxCo2 == null ? "" : String.valueOf(maxCo2));
            maxHumidity = NumbersUtilities.getDoubleFromString(properties.getProperty("seuil_Humidity"));
            txtMaxHumidity.setText(maxHumidity == null ? "" : String.valueOf(maxHumidity));
        } else {
            // Si aucun fichier de configuration n'est trouvé, affiche une alerte
            AlertUtilities.showAlert(primaryStage, "Aucun fichier trouvé.",
                    "Aucune configuration existante trouvé.",
                    "Aucune configuration n'a pu être chargé.\nUn nouveau fichier sera crée lors de la sauvegarde.",
                    AlertType.INFORMATION);
        }
    }

    /**
     * Récupère les nouvelles valeurs des champs de l'interface utilisateur et les
     * stocke. Ces valeurs sont destinées à être utilisées pour la sauvegarde ou la
     * réinitialisation.
     */
    private void setNewValues() {
        // Récupération des nouvelles valeurs des champs et stockage dans les variables
        // associées
        host = txtHost.getText().trim();
        port = NumbersUtilities.getIntFromString(txtPort.getText().trim());
        topic = txtTopic.getText().trim();
        alertFile = txtAlertFile.getText().trim();
        dataFile = txtDataFile.getText().trim();
        logsFile = txtLogsFile.getText().trim();
        frequency = NumbersUtilities.getDoubleFromString(txtFrequency.getText().trim());
        maxTemperature = NumbersUtilities.getDoubleFromString(txtMaxTemperature.getText().trim());
        maxActivity = NumbersUtilities.getDoubleFromString(txtMaxActivity.getText().trim());
        maxHumidity = NumbersUtilities.getDoubleFromString(txtMaxHumidity.getText().trim());
        maxCo2 = NumbersUtilities.getDoubleFromString(txtMaxCo2.getText().trim());
    }

    /**
     * Met à jour le style d'un élément visuel en fonction de son contenu.
     * 
     * @param node     Le nœud à mettre à jour.
     * @param isFilled Un booléen indiquant si le champ est rempli ou non.
     * @param img      L'ImageView associé à l'état de remplissage.
     */
    private void updateStyle(Node node, boolean isFilled, ImageView img) {
        if (!isFilled) {
            // Applique un style spécifique lorsque le champ n'est pas valide
            Style.setUndefinedTextAreaStyle(node);
            img.setVisible(true);
        } else {
            // Réinitialise le style lorsque le champ est valide
            Style.resetTextAreaStyle(node);
            img.setVisible(false);
        }
    }

    /**
     * Initialise le thread de test de connexion MQTT.
     * Ce thread vérifie la connexion au serveur MQTT à l'aide des paramètres
     * d'hôte et de port.
     * Il met à jour l'IHM en conséquence, affichant une icône et des alertes en
     * cas de succès ou d'échec de connexion.
     */
    private void initConnexionTestThread() {
        // À l'intérieur du thread
        connexionTestThread = new Thread(() -> {
            try {
                isTestRunning = true;
                // Désactive les éléments de configuration minimale pendant le test
                disableMinConfWhileTest(true);
                Style.setNewIcon(imgConnexion, "loading_icon.jpg");
                loadingIconAnimation = Animations.startLoadingAnimation(imgConnexion);
                // Effectue le test de connexion MQTT
                isConnected = MQTTConnection.testMQTTConnection(host, port);

                // Réactive les éléments de configuration minimale après le test
                disableMinConfWhileTest(false);

                // Mise à jour de l'IHM après la fin du test (à l'intérieur de
                // Platform.runLater())
                Platform.runLater(() -> {
                    loadingIconAnimation.stop();
                    if (isConnected) {
                        Style.setNewIcon(imgConnexion, "success_icon.png");
                        AlertUtilities.showAlert(primaryStage, "Connexion établie.", "Connexion réussie !",
                                "La connexion au serveur MQTT a été établie.", AlertType.INFORMATION);

                    } else {
                        Style.setNewIcon(imgConnexion, "failed_icon.png");
                        AlertUtilities.showAlert(primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                                "Veuillez saisir les bons paramètres du serveur MQTT.", AlertType.ERROR);
                    }
                    isTestRunning = false;
                });

            } catch (Exception e) {
                // Gestion des exceptions à l'intérieur de Platform.runLater()
                Platform.runLater(() -> {
                    loadingIconAnimation.stop();
                    isTestRunning = false;
                    Style.setNewIcon(imgConnexion, "failed_icon.png");
                    AlertUtilities.showAlert(primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                            "Veuillez saisir les paramètres corrects pour votre serveur MQTT.", AlertType.ERROR);
                });
            }
        });
    }

    /**
     * Désactive ou active les éléments de configuration minimale pendant le test de
     * connexion.
     *
     * @param _disable true pour désactiver les éléments, false pour les activer.
     */
    private void disableMinConfWhileTest(boolean _disable) {
        buttReset.setDisable(_disable);
        buttSave.setDisable(_disable);
        txtHost.setDisable(_disable);
        txtPort.setDisable(_disable);
    }

    /**
     * Vérifie la présence du fichier de configuration.
     *
     * @return true si le fichier est trouvé et chargé avec succès, false sinon.
     */
    private boolean checkConfFile() {
        try (FileInputStream fileInputStream = new FileInputStream(confFilePath)) {
            properties.load(fileInputStream);
            return true; // Le fichier de configuration a été trouvé et chargé
        } catch (IOException e) {
            return false; // Aucun fichier de configuration trouvé
        }
    }

    /**
     * Initialise les listeners des éléments de la scène.
     * Configure les validateurs pour différents types de champs texte.
     * Configure les actions lorsque le texte change dans les champs d'hôte, de
     * port, de fichiers, de fréquence et de seuils.
     */
    private void initListeners() {
        cbSoundOn.selectedProperty().addListener((observable, oldValue, newValue) -> {
            labSoundOn.setText(newValue == true ? "Activé" : "Désactivé");
            if (newValue) {
                labSoundlvl.setDisable(false);
                sliderSound.setDisable(false);
                labSoundLvlValue.setText("" + sliderSound.getValue());
                labSoundLvlValue.setDisable(false);
            } else {
                labSoundlvl.setDisable(true);
                sliderSound.setDisable(true);
                labSoundLvlValue.setText("-");
                labSoundLvlValue.setDisable(true);
            }
        });

        sliderSound.valueProperty().addListener((observable, oldValue, newValue) -> {
            labSoundLvlValue.setText(String.valueOf(newValue.intValue() + "%"));
        });

        txtHost.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                hostIsFilled = false;
            } else if (newValue.trim().length() > 60) {
                txtPort.setText(oldValue);
            } else {
                hostIsFilled = true;
                host = newValue.trim();
            }
            updateStyle(txtHost, hostIsFilled, imgUndefinedHost);
        });
        txtPort.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().isEmpty()) {
                portIsFilled = false;
            } else if (newValue.length() > 7 || !newValue.matches("\\d*\\s*\\d*")) {
                txtPort.setText(oldValue);
            } else {
                port = NumbersUtilities.getIntFromString(newValue.trim());
                portIsFilled = port <= 0 ? false : true;
            }
            updateStyle(txtPort, portIsFilled, imgUndefinedPort);
        });

        setupTextValidation(txtAlertFile, 15, "^[a-zA-Z]*$", alertFile);
        setupTextValidation(txtDataFile, 15, "^[a-zA-Z]*$", dataFile);
        setupTextValidation(txtLogsFile, 15, "^[a-zA-Z]*$", logsFile);

        setupNumberTextValidation(txtFrequency, 7, "-?\\d*\\.?\\d*", frequency);
        setupNumberTextValidation(txtMaxTemperature, 7, "-?\\d*\\.?\\d*", maxTemperature);
        setupNumberTextValidation(txtMaxHumidity, 7, "-?\\d*\\.?\\d*", maxHumidity);
        setupNumberTextValidation(txtMaxActivity, 7, "-?\\d*\\.?\\d*", maxActivity);
        setupNumberTextValidation(txtMaxCo2, 7, "-?\\d*\\.?\\d*", maxCo2);

    }

    /**
     * Configure un validateur pour un champ de texte spécifié avec une longueur
     * maximale et une expression régulière.
     * 
     * @param textField Le champ de texte à valider.
     * @param maxLength Longueur maximale autorisée pour le champ de texte.
     * @param regex     Expression régulière utilisée pour la validation.
     * @param value     Valeur de référence pour le champ de texte.
     */
    private void setupTextValidation(TextField textField, int maxLength, String regex, String value) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > maxLength || !newValue.matches(regex)) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Configure un validateur pour un champ de texte numérique spécifié avec une
     * longueur maximale et une expression régulière.
     * 
     * @param textField Le champ de texte à valider.
     * @param maxLength Longueur maximale autorisée pour le champ de texte.
     * @param regex     Expression régulière utilisée pour la validation.
     * @param value     Valeur de référence pour le champ de texte.
     */
    private void setupNumberTextValidation(TextField textField, int maxLength, String regex, Double value) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ((newValue.length() > maxLength || !newValue.matches(regex))) {
                textField.setText(oldValue);
            }
        });
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
            PythonAndThreadManagement.stopPythonThread();
            primaryStage.close();
            System.exit(0);
        } else {
            _e.consume();
        }
    }
}