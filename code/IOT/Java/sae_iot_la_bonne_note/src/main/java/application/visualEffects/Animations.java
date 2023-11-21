package application.visualEffects;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class Animations {

    public static void setAnimatedButton(Button _butt, double _scale) {
        ScaleTransition scaleInTransition = new ScaleTransition(Duration.millis(100),
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
            // _butt.setScaleX(1);
            // _butt.setScaleY(1);
        });
    }
}