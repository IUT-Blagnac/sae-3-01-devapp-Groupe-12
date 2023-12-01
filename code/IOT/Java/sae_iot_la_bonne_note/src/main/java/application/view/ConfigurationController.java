package application.view;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import application.control.Configuration;
import application.control.MainMenu;
import application.tools.AlertUtilities;
import application.tools.MQTTConnection;
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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
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
    private final String confFilePath = "code\\IOT\\Python\\config.ini";

    // Propriétés pour stocker des configurations
    private Properties properties = new Properties();

    // Éléments FXML
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

    // Variables pour stocker les valeurs des champs de l'IHM

    private String host;
    private BooleanProperty hostIsFilled = new SimpleBooleanProperty();

    private int port;
    private BooleanProperty portIsFilled = new SimpleBooleanProperty();

    private BooleanProperty serverConfIsFilled = new SimpleBooleanProperty();

    // Tâche de test de connexion
    private Task<Void> connexionTestTask;

    // État de la connexion
    private boolean isConnected;

    private String topic;
    private String alertFile;
    private String dataFile;
    private String logsFile;
    private String donneesDeBase;
    private String typeDuTemps;
    private String tpTemps;
    private int frequency;
    private int maxTemperature;
    private int maxHumidity;
    private int maxActivity;
    private int maxCo2;

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

        // Crée un binding combiné entre les propriétés hostIsFilled et portIsFilled
        BooleanBinding combinedBinding = Bindings.and(hostIsFilled, portIsFilled);

        // Lie le boolean serverConfIsFilled au binding combiné
        serverConfIsFilled.bind(combinedBinding);

        // Initialise les propriétés hostIsFilled et portIsFilled à true
        hostIsFilled.set(true);
        portIsFilled.set(true);

        // Initialise les éléments visuels de l'IHM
        initViewElements();

        // Initialise la tâche de test de connexion
        initConnexionTestTask();
    }

    private void initViewElements() {
        // Définit l'action à effectuer lorsque la fenêtre est fermée
        primaryStage.setOnCloseRequest(e -> {
            doLeave();
        });

        // Crée un binding combiné entre les propriétés hostIsFilled et portIsFilled
        BooleanBinding combinedBinding = Bindings.and(hostIsFilled, portIsFilled);

        // Lie le boolean serverConfIsFilled au binding combiné
        serverConfIsFilled.bind(combinedBinding);

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
        initTxtFieldListeners();
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
     * Initialise la tâche de test de connexion MQTT.
     * Cette tâche vérifie la connexion au serveur MQTT à l'aide des paramètres
     * d'hôte et de port.
     * Elle met à jour l'IHM en conséquence, affichant une icône et des alertes en
     * cas de succès ou d'échec de connexion.
     */
    private void initConnexionTestTask() {
        connexionTestTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Désactive les éléments de configuration minimale pendant le test
                    disableMinConfWhileTest(true);

                    // Effectue le test de connexion MQTT
                    isConnected = MQTTConnection.testMQTTConnection(host, port);

                    // Réactive les éléments de configuration minimale après le test
                    disableMinConfWhileTest(false);
                } catch (Exception e) {
                    cancel();
                }
                return null;
            }
        };

        // Actions à effectuer lorsque la tâche réussit, échoue, est annulée ou en cours

        // En cas de succès, arrête l'animation, met à jour l'IHM avec une icône de
        // réussite et affiche une alerte d'information
        connexionTestTask.setOnSucceeded(e -> {
            loadingIconAnimation.stop();
            if (isConnected) {
                Style.setNewIcon(imgConnexion, "SuccesIcon.png");
                AlertUtilities.showAlert(primaryStage, "Connexion établie.", "Connexion réussie !",
                        "La connexion au serveur MQTT a été établie.", AlertType.INFORMATION);
            } else {
                Style.setNewIcon(imgConnexion, "FailedIcon.png");
                AlertUtilities.showAlert(primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                        "Veuillez saisir les paramètres corrects du serveur MQTT.", AlertType.ERROR);
            }
        });

        // En cas d'échec, arrête l'animation, met à jour l'IHM avec une icône d'échec
        // et affiche une alerte d'erreur
        connexionTestTask.setOnFailed(e -> {
            loadingIconAnimation.stop();
            Style.setNewIcon(imgConnexion, "FailedIcon.png");
            Animations.stopLoadingAnimation(imgConnexion, loadingIconAnimation);
            AlertUtilities.showAlert(primaryStage, "Échec de la connexion.", "Échec de la connexion !",
                    "Veuillez saisir les paramètres corrects pour votre serveur MQTT.", AlertType.ERROR);
        });

        // En cas d'annulation, arrête l'animation
        connexionTestTask.setOnCancelled(e -> {
            loadingIconAnimation.stop();
        });

        // Au démarrage de la tâche, met à jour l'IHM avec une icône de chargement et
        // démarre une animation de chargement
        connexionTestTask.setOnRunning(e -> {
            Style.setNewIcon(imgConnexion, "LoadingIcon.jpg");
            loadingIconAnimation = Animations.startLoadingAnimation(imgConnexion);
        });
    }

    /**
     * Effectue un test de connexion au serveur MQTT.
     * Si un test est déjà en cours, affiche une alerte informant l'utilisateur de
     * patienter.
     * Sinon, initialise et lance une nouvelle tâche de test de connexion.
     */
    @FXML
    private void doConnectionTest() {
        if (connexionTestTask.isRunning()) {
            // Si un test est déjà en cours, affiche une alerte pour informer l'utilisateur
            AlertUtilities.showAlert(primaryStage, "Erreur.", "Un test est déjà en cours. Veuillez patienter.",
                    "Veuillez attendre que le test en cours se termine.", AlertType.INFORMATION);
        } else {
            if (serverConfIsFilled.getValue()) {
                // Si tous les champs requis pour le test sont remplis, initialise et lance la
                // tâche de test
                initConnexionTestTask();
                new Thread(connexionTestTask).start();
            } else {
                // Si des champs requis pour le test sont vides, affiche une alerte pour
                // informer l'utilisateur
                AlertUtilities.showAlert(primaryStage, "Opération impossible.",
                        "Impossible d'initier le test de connexion.",
                        "Veuillez remplir tous les champs requis pour le test ! (en rouge)",
                        AlertType.INFORMATION);
            }
        }
    }

    /**
     * Désactive ou active les éléments de configuration minimale pendant le test de
     * connexion.
     *
     * @param _disable true pour désactiver les éléments, false pour les activer.
     */
    private void disableMinConfWhileTest(boolean _disable) {
        txtHost.setDisable(_disable);
        txtPort.setDisable(_disable);
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
            frequency = getIntFromString(properties.getProperty("frequence_affichage"));
            txtFrequency.setText("" + frequency);
            typeDuTemps = properties.getProperty("typeTemps");
            cbTimeUnit.setValue(typeDuTemps);
            maxTemperature = getIntFromString(properties.getProperty("seuil_Temperature"));
            txtMaxTemperature.setText(maxTemperature == 0 ? "" : String.valueOf(maxTemperature));
            maxActivity = getIntFromString(properties.getProperty("seuil_Activity"));
            txtMaxActivity.setText(maxActivity == 0 ? "" : String.valueOf(maxActivity));
            maxCo2 = getIntFromString(properties.getProperty("seuil_CO2"));
            txtMaxCo2.setText(maxCo2 == 0 ? "" : String.valueOf(maxCo2));
            maxHumidity = getIntFromString(properties.getProperty("seuil_Humidity"));
            txtMaxHumidity.setText(maxHumidity == 0 ? "" : String.valueOf(maxHumidity));
        } else {
            // Si aucun fichier de configuration n'est trouvé, affiche une alerte
            AlertUtilities.showAlert(primaryStage, "Aucun fichier trouvé.",
                    "Aucune configuration existante trouvé.",
                    "Aucune configuration n'a pu être chargé.", AlertType.INFORMATION);
        }
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
     * Valide et sauvegarde les nouvelles valeurs spécifiées dans l'interface
     * utilisateur
     * vers le fichier de configuration.
     * Affiche une alerte en cas de succès ou d'échec de l'opération.
     */
    @FXML
    private void doValider() {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(confFilePath))) {

            // Récupère les nouvelles valeurs spécifiées dans l'interface utilisateur
            setNewValues();

            // Remet à vide le fichier de configuration avant l'écriture des données
            properties.clear();

            // Écrit les nouvelles valeurs dans le fichier de configuration
            writer.write("[MQTT]\n");
            writer.write("broker=" + host + "\n");
            writer.write("port=" + String.valueOf(port) + "\n");
            if (topic.equals("#")) {
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
            writer.write("fichier_alerte=" + alertFile + "\n");
            writer.write("fichier_donnees=" + dataFile + "\n");
            writer.write("fichier_logs=" + logsFile + "\n");
            String choixDonnee = "";
            if (cbTemperature.isSelected()) {
                choixDonnee += "temperature," + " ";
            }
            if (cbHumidity.isSelected()) {
                choixDonnee += "humidity," + " ";
            }
            if (cbActivity.isSelected()) {
                choixDonnee += "activity," + " ";
            }
            if (cbCo2.isSelected()) {
                choixDonnee += "co2," + " ";
            }
            writer.write("choix_donnees=" + choixDonnee + "\n");

            tpTemps = cbTimeUnit.getValue();
            if (tpTemps == "minute(s)") {
                frequency = getIntFromString(txtFrequency.getText()) * 60;
            }
            if (tpTemps == "heure(s)") {
                frequency = getIntFromString(txtFrequency.getText()) * 3600;
            }
            if (tpTemps == "jour(s)") {
                frequency = getIntFromString(txtFrequency.getText()) * 86400;
            }
            writer.write("typeTemps=" + tpTemps + "\n");
            writer.write("frequence_affichage=" + frequency + "\n");
            writer.write("[ALERT]\n");
            writer.write("seuil_Temperature=" + maxTemperature + "\n");
            writer.write("seuil_Humidity=" + maxHumidity + "\n");
            writer.write("seuil_CO2=" + maxCo2 + "\n");
            writer.write("seuil_Activity=" + maxActivity + "\n");
            // Affiche une alerte en cas de succès de la sauvegarde
            AlertUtilities.showAlert(primaryStage, "Opération réussie.",
                    "Sauvegarde effectuée !",
                    "La configuration a bien été sauvegardé.", AlertType.INFORMATION);
        } catch (IOException e) {
            AlertUtilities.showAlert(primaryStage, "Opération échouée.",
                    "Une erreur est survenue !",
                    "Une erreur est survenue lors de la sauvegarde.", AlertType.INFORMATION);
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
            this.txtMaxTemperature.setText("0");
            this.txtMaxHumidity.setText("0");
            this.txtMaxActivity.setText("0");
            this.txtMaxCo2.setText("0");

            // Applique les nouvelles valeurs
            this.setNewValues();
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
        port = getIntFromString(txtPort.getText().trim());
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

    /**
     * Convertit une chaîne en entier.
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

    /**
     * Initialise les écouteurs de champs texte.
     * Configure les validateurs pour différents types de champs texte.
     * Configure les actions lorsque le texte change dans les champs d'hôte, de
     * port, de fichiers, de fréquence et de seuils.
     */
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
            updateStyle(txtHost, hostIsFilled.getValue(), imgUndefinedHost);
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
            updateStyle(txtPort, portIsFilled.getValue(), imgUndefinedPort);
        });

        setupTextValidation(txtAlertFile, 15, "^[a-zA-Z]*$", alertFile);
        setupTextValidation(txtDataFile, 15, "^[a-zA-Z]*$", dataFile);
        setupTextValidation(txtLogsFile, 15, "^[a-zA-Z]*$", logsFile);

        setupNumberTextValidation(txtFrequency, 7, "\\d*", frequency);
        setupNumberTextValidation(txtMaxTemperature, 7, "-?\\d*", maxTemperature);
        setupNumberTextValidation(txtMaxHumidity, 7, "-?\\d*", maxHumidity);
        setupNumberTextValidation(txtMaxActivity, 7, "-?\\d*", maxActivity);
        setupNumberTextValidation(txtMaxCo2, 7, "-?\\d*", maxCo2);
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
    private void setupNumberTextValidation(TextField textField, int maxLength, String regex, Object value) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && (newValue.length() > maxLength || !newValue.matches(regex))) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Méthode associée au bouton FXML qui permet de fermer la fenêtre.
     * Initialise et affiche le menu principal lors de l'action de quitter.
     */
    @FXML
    private void doLeave() {
        MainMenu menu = new MainMenu();
        menu.start(primaryStage);
        menu.show();
    }
}