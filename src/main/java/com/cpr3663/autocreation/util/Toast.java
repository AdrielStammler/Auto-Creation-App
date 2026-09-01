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

    public static class Builder {
        private final String message;
        private int toastDelay = 2000;
        private int fadeInDuration = 500;
        private int fadeOutDuration = 500;
        private Color bkgdColor = Color.BLACK;

        private Builder(String message) {
            this.message = message;
        }

        public static Builder of(String message) {
            return new Builder(message);
        }

        public Builder duration(int duration) {
            this.toastDelay = duration;
            return this;
        }

        public Builder fade(int fadeDuration) {
            this.fadeInDuration = fadeDuration;
            this.fadeOutDuration = fadeDuration;
            return this;
        }

        public Builder fadeIn(int fadeInDuration) {
            this.fadeInDuration = fadeInDuration;
            return this;
        }

        public Builder fadeOut(int fadeOutDuration) {
            this.fadeOutDuration = fadeOutDuration;
            return this;
        }

        public Builder bkgdColor(Color color) {
            this.bkgdColor = color;
            return this;
        }

        public void show() {
            Toast.show(AppStateManager.getInstance().getWindow(),
                    message, toastDelay, fadeInDuration, fadeOutDuration, bkgdColor);
        }
    }

    public static void show(String message) {
        Builder.of(message).show();
    }

    private static void show(Window owner, String message, int duration, int fadeInDuration, int fadeOutDuration, Color bkgdColor) {
        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(message);
        text.setFont(Font.font("Verdana", 14));
        text.setFill(Color.WHITE);

        StackPane root = new StackPane(text);
        root.setStyle(String.format("-fx-background-radius: 20; -fx-background-color: rgba(%d, %d, %d, 0.65); -fx-padding: 10px 20px;",
                (int)(bkgdColor.getRed() * 255),
                (int)(bkgdColor.getGreen() * 255),
                (int)(bkgdColor.getBlue() * 255)
                ));
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
