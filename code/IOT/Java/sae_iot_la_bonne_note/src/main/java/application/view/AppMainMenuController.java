package application.view;

import application.control.AppMainMenu;
import application.visualEffects.Animations;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AppMainMenuController {

    private AppMainMenu mainMenu;
    private Stage primaryStage;

    @FXML
    private ImageView imgHommeArbre;

    public void initContext(AppMainMenu _mainMenu, Stage _primaryStage) {
        this.mainMenu = _mainMenu;
        this.primaryStage = _primaryStage;
    }

    public void displayDialog() {
        this.primaryStage.show();
    }

    @FXML
    private void doPlanterArbre(){
        Animations.planterArbreAnimation(imgHommeArbre);
    }
}
