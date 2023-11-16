package application.tools;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Classe utilitaire pour centrer automatiquement une fenêtre sur une autre (2
 * stage en fait). <BR />
 *
 * Se fait en fait en calculant à l'ouverture la position de la fenêtre en
 * fonction de la position et de la taille de la fenêtre sur laquelle se
 * centrer.
 */
public class StageManagement {

    /**
     * Static method for centering primary Stage over its parent Stage.
     *
     * @param parent  Stage on which primary is centered on
     * @param primary Stage to be centered (with regards to parent)
     */
    public static void manageCenteringStage(Stage parent, Stage primary) {

        // Calculate the center position of the parent Stage
        double centerXPosition = parent.getX() + parent.getWidth() / 2d;
        double centerYPosition = parent.getY() + parent.getHeight() / 2d;

        // Hide the pop-up stage before it is shown and becomes relocated
        primary.setOnShowing(ev -> primary.hide());

        // Relocate the pop-up Stage
        primary.setOnShown(ev -> {
            primary.setX(centerXPosition - primary.getWidth() / 2d);
            primary.setY(centerYPosition - primary.getHeight() / 2d);
            primary.show();
        });
    }

    public static void setCenterStageOnStage(Stage _Stage, Scene _scene) {

        // Récupérez la taille de l'écran
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        // Calculez les coordonnées x et y pour centrer la fenêtre
        double windowX = (screenWidth) / 2;
        double windowY = (screenHeight) / 2;

        // Définissez les coordonnées de la fenêtre pour la centrer
        _Stage.setX(windowX);
        _Stage.setY(windowY);
    }

    public static void setCenterStageOnScreen(Stage _Stage, Scene _scene) {

        // Obtenez les dimensions de la scène au lieu de primaryStage
        double sceneWidth = _scene.getWidth();
        double sceneHeight = _scene.getHeight();

        // Récupérez la taille de l'écran
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        // Calculez les coordonnées x et y pour centrer la fenêtre
        double windowX = (screenWidth - sceneWidth) / 2;
        double windowY = (screenHeight - sceneHeight) / 2;

        // Définissez les coordonnées de la fenêtre pour la centrer
        _Stage.setX(windowX);
        _Stage.setY(windowY);
    }

    public static void removeKeysEvents(Scene _scene) {

        // Créez un gestionnaire d'événements pour les événements clavier
        EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                // Empêchez l'événement de se propager à d'autres composants
                event.consume();
            }
        };

        // Ajoutez le gestionnaire d'événements à la scène
        _scene.addEventFilter(KeyEvent.ANY, keyEventHandler);
    }

    public static void disableItems(Scene _scene, boolean _disable) {
        for (Node node : _scene.getRoot().getChildrenUnmodifiable()) {
            node.setDisable(_disable);
        }
    }
}