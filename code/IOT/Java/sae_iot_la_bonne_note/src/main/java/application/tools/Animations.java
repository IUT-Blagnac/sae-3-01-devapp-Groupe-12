package application.tools;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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
    public static void setAnimatedButton(Button _butt, double _scaleIn, double _scaleOut, double _duration) {
        ScaleTransition scaleInTransition = new ScaleTransition(Duration.millis(_duration),
                _butt);
        scaleInTransition.setToX(_scaleIn);
        scaleInTransition.setToY(_scaleIn);
        _butt.setOnMouseEntered(event -> {
            scaleInTransition.playFromStart();
        });

        ScaleTransition scaleOutTransition = new ScaleTransition(Duration.millis(150),
                _butt);
        scaleOutTransition.setToX(_scaleOut);
        scaleOutTransition.setToY(_scaleOut);

        _butt.setOnMouseExited(event -> {
            scaleOutTransition.play();
        });
    }

    public static void setSelectedMenuAnimation(Button _butt, double _fadeFrom, double _fadeTo, double _duration) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(_duration), _butt);
        fadeTransition.setFromValue(_fadeFrom);
        fadeTransition.setToValue(_fadeTo);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
        fadeTransition.play();
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

    /**
     * Démarre une animation de transition en fondu pour l'image spécifiée.
     *
     * @param _img L'imageView pour laquelle démarrer l'animation.
     */
    public static void startConnectedAnimation(ImageView _img) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), _img);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.3);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true);

        fadeTransition.play();
    }
}
