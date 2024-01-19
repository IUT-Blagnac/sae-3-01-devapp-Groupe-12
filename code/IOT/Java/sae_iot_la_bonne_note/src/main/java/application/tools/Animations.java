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
        ScaleTransition scAnim = new ScaleTransition(Duration.millis(_duration),
                _butt);
        scAnim.setToX(_scaleIn);
        scAnim.setToY(_scaleIn);
        _butt.setOnMouseEntered(event -> {
            scAnim.playFromStart();
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
        FadeTransition fdAnim = new FadeTransition(Duration.millis(_duration), _butt);
        fdAnim.setFromValue(_fadeFrom);
        fdAnim.setToValue(_fadeTo);
        fdAnim.setAutoReverse(true);
        fdAnim.setCycleCount(FadeTransition.INDEFINITE);
        fdAnim.play();
    }

    /**
     * Lance une animation de chargement en faisant tourner l'ImageView spécifié.
     *
     * @param _img L'ImageView à animer.
     * @return L'objet RotateTransition contrôlant l'animation.
     */
    public static RotateTransition startLoadingAnimation(ImageView _img) {
        _img.setVisible(true);
        RotateTransition rtAnim = new RotateTransition(Duration.seconds(2), _img);
        rtAnim.setByAngle(360);
        rtAnim.setCycleCount(RotateTransition.INDEFINITE);
        rtAnim.setInterpolator(Interpolator.LINEAR);
        rtAnim.play();
        return rtAnim;
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
     * @return retourne l'animation crée
     */
    public static FadeTransition startConnectedAnimation(ImageView _img) {
        if (_img != null) {
            FadeTransition fdAnim = new FadeTransition(Duration.millis(1000), _img);
            fdAnim.setFromValue(1.0);
            fdAnim.setToValue(0.3);
            fdAnim.setCycleCount(Animation.INDEFINITE);
            fdAnim.setAutoReverse(true);

            fdAnim.play();

            return fdAnim;
        } else {
            return null;
        }
    }
}