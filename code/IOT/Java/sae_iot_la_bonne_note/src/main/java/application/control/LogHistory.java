package application.control;

import application.Main;
import application.tools.AlertUtilities;
import application.view.MainMenuController;
import application.view.LogHistoryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Classe de contrôleur de dialogue de la fenêtre de l'historique.
 * Cette classe gère l'affichage et la manipulation de la fenêtre de
 * l'historique.
 */
public class LogHistory {

    // Stage de la fenêtre principale
    private Stage primaryStage;
    // Controller
    private LogHistoryController controller;

    /**
     * Constructeur de la classe LogHistory.
     * 
     * @param _primaryStage Le stage de la fenêtre principale.
     */
    public LogHistory(Stage _primaryStage) {
        this.primaryStage = _primaryStage;

        try {
            // Chargement du fichier FXML de l'historique
            FXMLLoader loader = new FXMLLoader(
                    MainMenuController.class.getResource("LogHistory.fxml"));
            BorderPane root = loader.load();

            // Création de la scène et configuration du stage
            Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
            scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Historique");
            primaryStage.setResizable(false);

            // Récupération du contrôleur associé au fichier FXML chargé
            this.controller = loader.getController();
            // Initialisation du contexte du contrôleur
            this.controller.initContext(this, primaryStage);

        } catch (Exception e) {
            AlertUtilities.showAlert(primaryStage, "Erreur", "Échec du chargement du fichier FXML History.fxml",
                    "Merci de réessayer.", AlertType.ERROR);
                    e.printStackTrace();
            System.exit(-1); // En cas d'erreur, arrêt brutal de l'application
        }
    }

    /**
     * Méthode permettant de démarrer l'affichage de la fenêtre de l'historique.
     */
    public void show() {
        this.primaryStage.show(); // Affichage de la fenêtre de l'historique
    }
}
