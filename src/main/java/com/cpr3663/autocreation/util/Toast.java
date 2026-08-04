package com.cpr3663.autocreation.util;
import com.cpr3663.autocreation.AppStateManager;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public final class Toast {
    public static void show(String message) {
        show(message, 2000, 500);
    }

    public static void show(String message, int duration, int fadeDuration) {
        show(message, duration, fadeDuration, fadeDuration);
    }

    public static void show(String message, int toastDelay, int fadeInDuration, int fadeOutDuration) {
        show(AppStateManager.getInstance().getWindow(), message, toastDelay, fadeInDuration, fadeOutDuration);
    }

    private static void show(Window owner, String message, int duration, int fadeInDuration, int fadeOutDuration) {
        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(message);
        text.setFont(Font.font("Verdana", 14));
        text.setFill(Color.WHITE);

        StackPane root = new StackPane(text);
        root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.75); -fx-padding: 10px 20px;");
        root.setOpacity(0);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        // Position toast near the bottom center of the owner window
        toastStage.show();
        toastStage.setX(owner.getX() + (owner.getWidth() / 2) - (toastStage.getWidth() / 2));
        toastStage.setY(owner.getY() + owner.getHeight() - toastStage.getHeight() - 50);

        // Fade In
        FadeTransition fadeIn = new FadeTransition(Duration.millis(fadeInDuration), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Fade Out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(fadeOutDuration), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> toastStage.close());

        // Display Timeline
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration), e -> fadeOut.play()));

        fadeIn.setOnFinished(e -> timeline.play());
        fadeIn.play();
    }
}
