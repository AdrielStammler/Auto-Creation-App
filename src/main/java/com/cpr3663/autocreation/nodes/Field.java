package com.cpr3663.autocreation.nodes;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.objects.DriveEvent;
import com.cpr3663.autocreation.objects.Event;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import javafx.scene.control.Label;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class Field {
    private static final double PIXELS_PER_METER = 75.0;
    private static double FIELD_WIDTH;

    public static Pane getFieldPane() {
        AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AppStateManager.getInstance().getAprilTagField());
        FIELD_WIDTH = fieldLayout.getFieldWidth();
        Pane pane = getPane(fieldLayout);
        drawAprilTags(pane, fieldLayout);
        drawRobotPoses(pane);
        pane.setOnMouseClicked((MouseEvent event) -> System.out.println("Pane clicked at coordinates: X=" + event.getX() + ", Y=" + event.getY()));
        return pane;
    }

    private static Pane getPane(AprilTagFieldLayout fieldLayout) {
        Pane pane = new Pane();
        double xSize = fieldLayout.getFieldLength() * PIXELS_PER_METER;
        double ySize = fieldLayout.getFieldWidth() * PIXELS_PER_METER;
        pane.setMinSize(xSize, ySize);
        pane.setPrefSize(xSize, ySize);
        pane.setMaxSize(xSize, ySize);

        Image image = AppStateManager.getInstance().getFieldImage();
        if (image == null || image.isError()) return pane;
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(100, 100, true, true, true, false)
        );
        pane.setBackground(new Background(backgroundImage));
        return pane;
    }

    private static void drawAprilTags(Pane fieldPane, AprilTagFieldLayout fieldLayout) {
        Event event = AppStateManager.getInstance().getSelectedEvent();
        AprilTag selected;
        if (event != null && event.getName().equals(Constants.Events.DRIVE_NAME))
            selected = ((DriveEvent) event).getRelativeFrom();
        else selected = new AprilTag(-2, new Pose3d());

        for (AprilTag tag : fieldLayout.getTags()) {
            Pose2d pose = tag.pose.toPose2d();
            int id = tag.ID;
            boolean isSelected = selected.equals(tag);

            Translation2d position = pose.getTranslation();
            position = Helper.toPixels(position);

            Rotation2d rotation = pose.getRotation();
            rotation = rotation.plus(Rotation2d.fromDegrees(-90));

            // Build the visual node for individual tags
            Pane tagGroup = createTagVisual(id, rotation.getDegrees(), isSelected);

            // Relocate node so its absolute center sits on the coordinate point
            tagGroup.layoutXProperty().bind(tagGroup.widthProperty().divide(-2).add(position.getX()));
            tagGroup.layoutYProperty().bind(tagGroup.heightProperty().divide(-2).add(position.getY()));

            fieldPane.getChildren().add(tagGroup);
        }
    }

    private static Pane createTagVisual(int id, double rotationDeg, boolean isSelected) {
        Pane tagNode = new Pane();

        // TODO get image
        double size = 20.0;
        tagNode.setPrefSize(size, size);

        // Representing the physical tag boundary
        Rectangle square = new Rectangle(size, size);
        square.setFill(Color.BLACK);
        square.setStroke(Color.WHITE);
        square.setStrokeWidth(1.5);

        Rectangle pointer = new Rectangle(size / 2 - 1, 0, 2, size / 2);
        pointer.setFill(Color.RED);

        Label idLabel = new Label(Integer.toString(id));
        idLabel.setTextFill(Color.WHITE);
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 9px;");
        idLabel.layoutXProperty().bind(tagNode.widthProperty().divide(2).add(-4));
        idLabel.layoutYProperty().bind(tagNode.heightProperty().divide(2).add(-6));

        tagNode.getChildren().addAll(square, pointer, idLabel);

        Rotate rotate = new Rotate();
        rotate.setAngle(-rotationDeg);
        rotate.pivotXProperty().bind(tagNode.widthProperty().divide(2));
        rotate.pivotYProperty().bind(tagNode.heightProperty().divide(2));
        tagNode.getTransforms().add(rotate);

        if (isSelected) {
            // TODO once is using Icon apply highlight
//            Helper.highlightImage(icon);
        }

        return tagNode;
    }

    private static void drawRobotPoses(Pane fieldPane) {
        Event selectedEvent = AppStateManager.getInstance().getSelectedEvent();
        double sizeX = AppStateManager.getInstance().getRobotSize();
        double sizeY = AppStateManager.getInstance().getRobotSize();

        for (Event event : AppStateManager.getInstance().getEvents())
            if (event.getName().equals(Constants.Events.DRIVE_NAME)) {
                DriveEvent driveEvent = (DriveEvent) event;
                Image robot = new Image(Objects.requireNonNull(Field.class.getResource(Constants.Paths.ROBOT_ICON)).toExternalForm());
                ImageView robotView = new ImageView(robot);

                Translation2d position = Helper.toPixels(driveEvent.getXPos() - (sizeX / 2), driveEvent.getYPos() + (sizeX / 2));

                robotView.setX(position.getX());
                robotView.setY(position.getY());
                robotView.setRotate(driveEvent.getTheta());

                robotView.setPreserveRatio(true);
                robotView.setSmooth(true);
                robotView.setFitWidth(sizeX * PIXELS_PER_METER);

                if (event.equals(selectedEvent)) Helper.highlightImage(robotView);
                else Helper.colorRobot(robotView);

                fieldPane.getChildren().add(robotView);
            }
    }

    private static class Helper {
        private static final Color HIGHLIGHT_COLOR = Color.ORANGE;
        private static final Color NORMAL_ROBOT_COLOR = Color.WHITESMOKE;

        private static void highlightImage(ImageView image) {
            changeColor(image, HIGHLIGHT_COLOR);
        }

        private static void colorRobot(ImageView image) {
            changeColor(image, NORMAL_ROBOT_COLOR);
        }

        private static void changeColor(ImageView image, Color color) {
            Light.Distant distantLight = new Light.Distant();
            distantLight.setElevation(90);
            distantLight.setColor(color);
            Lighting lighting = new Lighting();
            lighting.setLight(distantLight);
            lighting.setSurfaceScale(0.0);
            image.setEffect(lighting);
        }

        private static Translation2d toPixels(Translation2d position) {
            return toPixels(position.getX(), position.getY());
        }

        private static Translation2d toPixels(double x, double y) {
            Translation2d position = new Translation2d(x, FIELD_WIDTH - y);
            return position.times(PIXELS_PER_METER);
        }

        private static Translation2d fromPixels(Translation2d position) {
            return fromPixels(position.getX(), position.getY());
        }

        private static Translation2d fromPixels(double x, double y) {
            Translation2d position = new Translation2d(x, (FIELD_WIDTH * PIXELS_PER_METER) - y);
            return position.div(PIXELS_PER_METER);
        }
    }
}
