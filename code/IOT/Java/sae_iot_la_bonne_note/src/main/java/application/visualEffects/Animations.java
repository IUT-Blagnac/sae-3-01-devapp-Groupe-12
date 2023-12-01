package application.visualEffects;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * La classe Animations fournit des utilitaires pour créer et contrôler des
 * animations dans une interface utilisateur JavaFX.
 * Elle offre des fonctionnalités pour animer les boutons, changer les scènes
 * avec des effets visuels, ainsi que pour démarrer et arrêter des animations de
 * chargement.
 */
public class Animations {

    /**
     * Applique une animation de zoom sur le bouton lors du survol de la souris.
     *
     * @param _butt     Le bouton à animer.
     * @param _scale    L'échelle de l'animation.
     * @param _duration La durée de l'animation en millisecondes.
     */
    public static void setAnimatedButton(Button _butt, double _scale, double _duration) {
        ScaleTransition scaleInTransition = new ScaleTransition(Duration.millis(_duration),
                _butt);
        scaleInTransition.setToX(_scale);
        scaleInTransition.setToY(_scale);
        _butt.setOnMouseEntered(event -> {
            scaleInTransition.playFromStart();
        });

        ScaleTransition scaleOutTransition = new ScaleTransition(Duration.millis(150),
                _butt);
        scaleOutTransition.setToX(1);
        scaleOutTransition.setToY(1);

        _butt.setOnMouseExited(event -> {
            scaleOutTransition.play();
        });
    }

    /**
     * Anime le changement de scène en agrandissant le bouton spécifié.
     *
     * @param button     Le bouton à animer.
     * @param scale      L'échelle de l'animation.
     * @param duration   La durée de l'animation en millisecondes.
     * @param onFinished Action à exécuter à la fin de l'animation.
     */
    public static void sceneSwapAnimation(Button button, double scale, double duration, Runnable onFinished) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.setOnFinished(event -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        scaleTransition.play();
    }

    /**
     * Lance une animation de chargement en faisant tourner l'ImageView spécifié.
     *
     * @param _img L'ImageView à animer.
     * @return L'objet RotateTransition contrôlant l'animation.
     */
    public static RotateTransition startLoadingAnimation(ImageView _img) {
        _img.setVisible(true);
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), _img);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
        rotateTransition.play();
        return rotateTransition;
    }

    /**
     * Arrête une animation de chargement en cours.
     *
     * @param _img              L'ImageView en cours d'animation.
     * @param _loadingAnimation L'objet RotateTransition contrôlant l'animation.
     */
    public static void stopLoadingAnimation(ImageView _img, RotateTransition _loadingAnimation) {
        _img.setVisible(false);
        _loadingAnimation.stop();
    }
}
