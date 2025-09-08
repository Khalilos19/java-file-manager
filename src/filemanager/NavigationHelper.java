package filemanager;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class NavigationHelper {

    public static void applyFadeTransition(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(800), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }
}