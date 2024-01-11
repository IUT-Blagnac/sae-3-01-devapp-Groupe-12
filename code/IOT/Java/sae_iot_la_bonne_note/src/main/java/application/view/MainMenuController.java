package application.view;

import application.control.Configuration;
import application.control.LogHistory;
import application.control.MainMenu;
import application.control.WharehouseMonitor;
import application.tools.AlertUtilities;
import application.tools.Animations;
import application.tools.PythonAndThreadManagement;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Contrôleur pour la vue du menu principal de l'application.
 * Gère les actions et les interactions associées aux éléments de cette vue.
 */
public class MainMenuController {

    // Référence à la classe du menu principal
    private MainMenu mainMenu;

    // Référence au stage de la fenêtre principale
    private Stage primaryStage;

    @FXML
    private Button buttCheckWhareHouse;

    @FXML
    private Button buttCheckHistory;

    @FXML
    private Button buttConfiguration;

    @FXML
    private Button buttLeave;

    /**
     * Initialise le contrôleur de vue MainMenuController.
     *
     * @param _mainMenu     L'instance du menu principal.
     * @param _primaryStage La scène principale associée au contrôleur.
     */
    public void initContext(MainMenu _mainMenu, Stage _primaryStage) {
        this.mainMenu = _mainMenu;
        this.primaryStage = _primaryStage;
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        // Configure les animations pour les boutons
        Animations.setAnimatedButton(buttCheckWhareHouse, 1.12, 1, 100);
        Animations.setAnimatedButton(buttCheckHistory, 1.12, 1, 100);
        Animations.setAnimatedButton(buttConfiguration, 1.12, 1, 100);
        Animations.setAnimatedButton(buttLeave, 1.12, 1, 100);
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

    /**
     * Gère l'action liée au bouton de surveillance de l'entrepôt.
     * Crée une instance de WharehouseMonitor et affiche la fenêtre.
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
     * Gère l'action liée au bouton de configuration.
     * Lance une animation de changement de scène vers la configuration.
     */
    @FXML
    private void doConfiguration() {
        Configuration conf = new Configuration(primaryStage);
        conf.show();
    }

    /**
     * Gère l'action liée au bouton de sortie.
     * Lance une animation de changement de scène et ferme la fenêtre principale.
     */
    @FXML
    private void doLeave() {
        PythonAndThreadManagement.stopPythonThread();
        this.primaryStage.close();
    }
}
