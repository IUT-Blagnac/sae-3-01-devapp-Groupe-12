package application.tools;

import javafx.scene.Node;
import javafx.scene.Scene;
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
     * Centre la fenêtre secondaire (primary) sur la fenêtre parente.
     *
     * @param parent  Fenêtre parente sur laquelle la fenêtre principale est
     *                centrée.
     * @param primary Fenêtre à centrer par rapport à la fenêtre parente.
     */
    public static void manageCenteringStage(Stage parent, Stage primary) {

        // Calcul des coordonnées du centre de la fenêtre parente
        double centerXPosition = parent.getX() + parent.getWidth() / 2d;
        double centerYPosition = parent.getY() + parent.getHeight() / 2d;

        // Cachage de la fenêtre secondaire avant de la repositionner
        primary.setOnShowing(ev -> primary.hide());

        // Repositionnement de la fenêtre secondaire pour la centrer
        primary.setOnShown(ev -> {
            primary.setX(centerXPosition - primary.getWidth() / 2d);
            primary.setY(centerYPosition - primary.getHeight() / 2d);
            primary.show();
        });
    }

    /**
     * Centre la fenêtre sur l'écran principal.
     *
     * @param _Stage Fenêtre à centrer.
     * @param _scene Scène de la fenêtre à centrer.
     */
    public static void setCenterStageOnStage(Stage _Stage, Scene _scene) {

        // Récupérez la taille de l'écran
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        // Calculez les coordonnées x et y pour centrer la fenêtre
        double windowX = (screenWidth) / 2;
        double windowY = (screenHeight) / 2;

        // Définition des coordonnées de la fenêtre pour la centrer
        _Stage.setX(windowX);
        _Stage.setY(windowY);
    }

    /**
     * Centre la fenêtre sur l'écran principal en utilisant les dimensions de la
     * scène.
     *
     * @param _Stage Fenêtre à centrer.
     * @param _scene Scène de la fenêtre à centrer.
     */
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

        // Définition des coordonnées de la fenêtre pour la centrer
        _Stage.setX(windowX);
        _Stage.setY(windowY);
    }

    /**
     * Désactive tous les éléments de la scène.
     *
     * @param _scene   Scène dont les éléments doivent être désactivés.
     * @param _disable true pour désactiver les éléments, false pour les activer.
     */
    public static void disableItems(Scene _scene, boolean _disable) {
        // Désactivation ou activation des éléments de la scène en fonction du paramètre
        for (Node node : _scene.getRoot().getChildrenUnmodifiable()) {
            node.setDisable(_disable);
        }
    }
}