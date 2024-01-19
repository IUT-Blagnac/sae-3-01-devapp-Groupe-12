package application.control;

import application.tools.AlertUtilities;
import application.view.MainMenuController;
import application.view.ConfigurationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Classe de contrôleur de dialogue de la fenêtre de configuration.
 * Cette classe gère l'affichage et la manipulation de la fenêtre de
 * configuration.
 */
public class Configuration {

    // Stage de la fenêtre principale
    private Stage primaryStage;
    // Controller
    private ConfigurationController controller;

    /**
     * Constructeur de la classe Configuration.
     * 
     * @param _primaryStage Le stage de la fenêtre principale.
     */
    public Configuration(Stage _primaryStage) {
        this.primaryStage = _primaryStage;

        try {
            // Chargement du fichier FXML de configuration
            FXMLLoader loader = new FXMLLoader(
                    MainMenuController.class.getResource("Configuration.fxml"));
            BorderPane root = loader.load();

            primaryStage.getScene().setRoot(root);
            primaryStage.setTitle("Configuration");

            // Récupération du contrôleur associé au fichier FXML chargé
            this.controller = loader.getController();
            // Initialisation du contexte du contrôleur
            this.controller.initContext(this, primaryStage);

        } catch (Exception e) {
            AlertUtilities.showAlert(primaryStage, "Erreur", "Échec du chargement du fichier FXML Configuration.fxml",
                    "Merci de réessayer.\nCode d'erreur :" + e, AlertType.ERROR);
            System.exit(-1); // En cas d'erreur, arrêt brutal de l'application
        }
    }

    /**
     * Méthode permettant de démarrer l'affichage de la fenêtre de configuration.
     */
    public void show() {
        this.primaryStage.show(); // Affichage de la fenêtre de configuration
    }
}