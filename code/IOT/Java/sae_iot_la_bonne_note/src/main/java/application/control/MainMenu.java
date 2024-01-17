package application.control;

import application.Main;
import application.tools.AlertUtilities;
import application.view.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Classe de contrôleur de dialogue de la fenêtre du menu principal de
 * l'application.
 * Cette classe gère l'affichage et la manipulation de la fenêtre du menu
 * principal.
 */
public class MainMenu extends Application {

    // Stage de la fenêtre principale construite par DailyBankMainFrame
    private Stage primaryStage;

    /**
     * Méthode de démarrage (JavaFX).
     * Cette méthode est appelée lors du démarrage de l'application JavaFX.
     * Elle initialise la fenêtre du menu principal en chargeant le fichier FXML
     * correspondant.
     * 
     * @param _primaryStage Le stage de la fenêtre principale.
     */
    @Override
    public void start(Stage _primaryStage) {

        this.primaryStage = _primaryStage;

        try {

            // Chargement du fichier FXML du menu principal
            FXMLLoader loader = new FXMLLoader(
                    MainMenuController.class.getResource("MainMenu.fxml"));
            BorderPane root = loader.load();

            if (primaryStage.getScene() == null) {
                // Création de la scène une seule fois
                Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
                scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
                primaryStage.setScene(scene);
            } else {
                // Changement de la scène si celle ci n'était pas nulle
                primaryStage.getScene().setRoot(root);
            }

            primaryStage.setTitle("Menu Principal");
            primaryStage.setMinHeight(850);
            primaryStage.setMinWidth(1200);
            primaryStage.setResizable(true);
            primaryStage.getIcons().add(new Image("/application/images/entrepot_logo.png"));

            // Récupération du contrôleur associé au fichier FXML chargé
            MainMenuController mainMenu = loader.getController();
            // Initialisation du contexte du contrôleur
            mainMenu.initContext(this, primaryStage);

            this.show(); // Affichage de la fenêtre du menu principal

            // Utilisé lors du développement

            // LogHistory a = new LogHistory(_primaryStage);
            // b.show();

            // WharehouseMonitor b = new WharehouseMonitor(_primaryStage);
            // a.show();

            // Configuration c = new Configuration(_primaryStage);
            // a.show();

        } catch (Exception e) {
            AlertUtilities.showAlert(primaryStage, "Erreur", "Échec du chargement du fichier FXML MainMenu.fxml",
                    "Merci de réessayer.\nCode d'erreur :" + e, AlertType.ERROR);
            System.exit(-1); // En cas d'erreur, arrêt brutal de l'application
        }
    }

    /**
     * Méthode permettant d'afficher la fenêtre du menu principal.
     */
    public void show() {
        this.primaryStage.show();
    }

    /**
     * Méthode principale de lancement de l'application.
     * Cette méthode est appelée pour lancer l'application.
     */
    public static void runApp() {
        Application.launch(); // Lancement de l'application JavaFX
    }
}
