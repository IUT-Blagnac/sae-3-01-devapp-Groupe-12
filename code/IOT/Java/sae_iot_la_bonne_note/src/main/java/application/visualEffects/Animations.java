package application.visualEffects;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Animations {

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

    public static void sceneSwapAnimation(Button button, double scale, double duration, Runnable onFinished) {
        // Création de l'animation de zoom sur le bouton
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), button);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.setOnFinished(event -> {
            // À la fin de l'animation, exécute le callback onFinished
            if (onFinished != null) {
                onFinished.run();
            }
        });
        scaleTransition.play(); // Lancement de l'animation
    }

    /**
     * Starts a loading animation by rotating the specified ImageView.
     *
     * @param _img The ImageView to animate.
     * @return The RotateTransition object controlling the animation.
     */
    public static RotateTransition startLoadingAnimation(ImageView _img) {
        _img.setVisible(true);
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), _img);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);

        // Start the rotation animation
        rotateTransition.play();

        return rotateTransition;
    }

    /**
     * Stops a running loading animation.
     *
     * @param _img              The ImageView being animated.
     * @param _loadingAnimation The RotateTransition object controlling the
     *                          animation.
     */
    public static void stopLoadingAnimation(ImageView _img, RotateTransition _loadingAnimation) {
        _img.setVisible(false);
        _loadingAnimation.stop();
    }
}