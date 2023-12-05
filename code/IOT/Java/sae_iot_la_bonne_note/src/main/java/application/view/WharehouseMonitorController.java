package application.view;

import application.control.Configuration;
import application.control.LogHistory;
import application.control.MainMenu;
import application.control.WharehouseMonitor;
import application.tools.AlertUtilities;
import application.visualEffects.Animations;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WharehouseMonitorController {

    // Référence à la classe de surveillance de l'entrepôt
    private WharehouseMonitor wharehouseMonitor;

    // Référence au stage de la fenêtre principale
    private Stage primaryStage;

    @FXML
    private Button buttMenu;

    @FXML
    private Button buttCheckWhareHouse;

    @FXML
    private Button buttCheckHistory;

    @FXML
    private Button buttConfiguration;

    /**
     * Initialise le contrôleur de vue WharehouseMonitorController.
     *
     * @param _mainMenu     L'instance du menu principal.
     * @param _primaryStage La scène principale associée au contrôleur.
     */
    public void initContext(WharehouseMonitor _wharehouse, Stage _primaryStage) {
        this.wharehouseMonitor = _wharehouse;
        this.primaryStage = _primaryStage;
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        // Configure les animations pour les boutons
        Animations.setAnimatedButton(buttMenu, 1.1, 1, 100);
        Animations.setAnimatedButton(buttCheckHistory, 1.1, 1, 100);
        Animations.setAnimatedButton(buttConfiguration, 1.1, 1, 100);
        Animations.setSelectedMenuAnimation(buttCheckWhareHouse, 0.5, 0.8, 1000);
    }

    /**
     * Méthode de fermeture de la fenêtre par la croix.
     *
     * @param _e L'événement de fermeture de fenêtre.
     */
    private void closeWindow(WindowEvent _e) {
        if (AlertUtilities.confirmYesCancel(this.primaryStage, "Quitter l'application",
                "Etes-vous sûr de vouloir quitter l'application ?", null, AlertType.CONFIRMATION)) {
            this.primaryStage.close();
        }
        _e.consume();
    }

    @FXML
    private void doCheckHistory() {
        Animations.sceneSwapAnimation(buttCheckHistory, 1.15, 100, () -> {
            LogHistory history = new LogHistory(primaryStage);
            history.show();
        });
    }

    /**
     * Gère l'action liée au bouton de configuration.
     * Lance une animation de changement de scène vers la configuration.
     */
    @FXML
    private void doConfiguration() {
        Animations.sceneSwapAnimation(buttConfiguration, 1.15, 50, () -> {
            Configuration conf = new Configuration(primaryStage);
            conf.show();
        });
    }

    /**
     * Méthode associée au bouton FXML qui permet de fermer la fenêtre.
     * Initialise et affiche le menu principal lors de l'action de quitter.
     */
    @FXML
    private void doMenu() {
        Animations.sceneSwapAnimation(buttMenu, 1.15, 100, () -> {
            MainMenu menu = new MainMenu();
            menu.start(primaryStage);
            menu.show();
        });
    }
}
