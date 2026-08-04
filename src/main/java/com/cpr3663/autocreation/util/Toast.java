package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import edu.wpi.first.math.MathUtil;
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

import java.util.ArrayList;

public final class Toast {
    private static final ArrayList<Stage> activeToasts = new ArrayList<>();
    private static final int TOAST_SPACING = 15;

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

        toastStage.show();
        toastStage.setX(owner.getX() + (owner.getWidth() / 2) - (toastStage.getWidth() / 2));
        toastStage.setY(owner.getY() + owner.getHeight() - toastStage.getHeight() - 50);

        shiftExistingToastsUpward(toastStage.getHeight());
        activeToasts.add(toastStage);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(fadeInDuration), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Fade Out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(fadeOutDuration), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            toastStage.close();
            activeToasts.remove(toastStage);
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(duration), e -> fadeOut.play()));

        fadeIn.setOnFinished(e -> timeline.play());
        fadeIn.play();
    }

    private static void shiftExistingToastsUpward(double newToastHeight) {
        double shiftAmount = newToastHeight + TOAST_SPACING;

        for (Stage toast : activeToasts) {
            final double startY = toast.getY();
            final double targetY = startY - shiftAmount;

            final double totalFrames = 12.0;
            Timeline slideTimeline = new Timeline();

            for (int frame = 1; frame <= totalFrames; frame++) {
                final double progress = frame / totalFrames;
                final double currentFrameY = MathUtil.interpolate(startY, targetY, progress);

                Duration frameTime = Duration.millis(200 * progress);

                KeyFrame keyFrame = new KeyFrame(frameTime, event -> toast.setY(currentFrameY));

                slideTimeline.getKeyFrames().add(keyFrame);
            }

            slideTimeline.play();
        }
    }
}
