package application.control;

import application.Main;
import application.view.MainMenuController;
import application.view.ConfigurationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Classe de controleur de dialogue de la fenêtre de configuration
 */
public class Configuration {

    // Stage de la fenêtre principale
    private Stage primaryStage;
    // controller
    private ConfigurationController controller;

    /**
     * Méthode de démarrage (JavaFX).
     */
    public Configuration(Stage _primaryStage) {

        this.primaryStage = _primaryStage;

        try {

            // Chargement du source fxml
            FXMLLoader loader = new FXMLLoader(
                    MainMenuController.class.getResource("MainMenu.fxml"));
            BorderPane root = loader.load();

            // Paramétrage du Stage : feuille de style, titre
            Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
            scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle("Configuration");
            primaryStage.setResizable(false);

            this.controller = loader.getController();
            this.controller.initContext(this, primaryStage);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Méthode principale de lancement de l'application.
     */
    public void runApp() {
        this.controller.displayDialog();
    }
}
