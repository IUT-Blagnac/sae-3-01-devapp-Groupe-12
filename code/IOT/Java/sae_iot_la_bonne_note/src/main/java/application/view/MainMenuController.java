package application.view;

import application.control.MainMenu;
import application.tools.AlertUtilities;
import application.visualEffects.Animations;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainMenuController {

    private MainMenu mainMenu;
    private Stage primaryStage;

    @FXML
    private Button buttCheckWhareHouse;

    @FXML
    private Button buttCheckHistory;

    @FXML
    private Button buttSettings;

    @FXML
    private Button buttLeave;

    /**
     * Initialisation du contrôleur de vue DailyBankMainFrameController.
     *
     * @param _containingStage Stage qui contient la fenêtre précédente.
     */
    public void initContext(MainMenu _mainMenu, Stage _primaryStage) {
        this.mainMenu = _mainMenu;
        this.primaryStage = _primaryStage;
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

        Animations.setAnimatedButton(buttCheckWhareHouse, 1.12);
        Animations.setAnimatedButton(buttCheckHistory, 1.12);
        Animations.setAnimatedButton(buttSettings, 1.12);
        Animations.setAnimatedButton(buttLeave, 1.12);

    }

    /*
     * Méthode qui affiche la scène.
     * 
     */
    public void displayDialog() {
        this.primaryStage.show();
    }

    /*
     * Méthode de fermeture de la fenêtre par la croix.
     *
     * @param e Evénement associé (inutilisé pour le moment)
     *
     */
    private void closeWindow(WindowEvent _e) {
        if (AlertUtilities.confirmYesCancel(this.primaryStage, "Quitter l'application",
                "Etes vous sur de vouloir quitter le jeu ?", null, AlertType.CONFIRMATION)) {
            this.primaryStage.close();
        }
        _e.consume();
    }

    /*
     * Méthode associé au bouton FXML qui permet de fermer la fenêtre.
     * 
     */
    @FXML
    private void doLeave() {
        this.primaryStage.close();
    }
}
