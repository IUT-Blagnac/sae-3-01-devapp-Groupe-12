package application.visualEffects;

import javafx.animation.RotateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Animations {

    public static void planterArbreAnimation(ImageView _hommeArbre) {
        RotateTransition rotation = new RotateTransition(Duration.millis(500), _hommeArbre);

        rotation.setByAngle(360);
        rotation.setCycleCount(1);
        rotation.play();
    }
}