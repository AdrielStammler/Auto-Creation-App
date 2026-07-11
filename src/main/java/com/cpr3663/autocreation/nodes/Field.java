package com.cpr3663.autocreation.nodes;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.objects.DriveEvent;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.MiscHelper;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.Objects;

public class Field {
    private static final double PIXELS_PER_METER = 50.0;

    public static Pane getFieldPane() {
        AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AppStateManager.getInstance().getAprilTagField());
        Pane pane = getPane(fieldLayout);
        drawAprilTags(pane, fieldLayout);
        drawRobotPoses(pane);
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
        double fieldWidthPixels = fieldLayout.getFieldWidth() * PIXELS_PER_METER;

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
            position = position.times(PIXELS_PER_METER);
            position = new Translation2d(position.getX(), fieldWidthPixels - position.getY());

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

        // Define dimension boundaries for standard rendering (~8-inch/0.2m scaled representation)
        double size = 20.0;
        tagNode.setPrefSize(size, size);

        // Representing the physical tag boundary
        Rectangle square = new Rectangle(size, size);
        square.setFill(Color.BLACK);
        square.setStroke(Color.WHITE);
        square.setStrokeWidth(1.5);

        // Add a directional indicator line pointing 'forward' from the face of the tag
        Rectangle pointer = new Rectangle(size / 2 - 1, 0, 2, size / 2);
        pointer.setFill(Color.RED);

        // Overlay text displaying the Target Identification string
        Label idLabel = new Label(Integer.toString(id));
        idLabel.setTextFill(Color.WHITE);
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 9px;");
        idLabel.layoutXProperty().bind(tagNode.widthProperty().divide(2).add(-4));
        idLabel.layoutYProperty().bind(tagNode.heightProperty().divide(2).add(-6));

        tagNode.getChildren().addAll(square, pointer, idLabel);

        // Apply 2D transforms around the visual node center point
        Rotate rotate = new Rotate();
        rotate.setAngle(-rotationDeg);
        rotate.pivotXProperty().bind(tagNode.widthProperty().divide(2));
        rotate.pivotYProperty().bind(tagNode.heightProperty().divide(2));
        tagNode.getTransforms().add(rotate);

        if (isSelected) {
            // TODO once is using Icon apply highlight
//            MiscHelper.highlightImage(icon);
        }

        return tagNode;
    }

    private static void drawRobotPoses(Pane fieldPane) {
        for (Event event : AppStateManager.getInstance().getEvents())
            if (event.getName().equals(Constants.Events.DRIVE_NAME)) {
                DriveEvent driveEvent = (DriveEvent) event;
                Image robot = new Image(Objects.requireNonNull(Field.class.getResource(Constants.Paths.ROBOT_ICON)).toExternalForm());
                ImageView robotView = new ImageView(robot);

                robotView.setX(driveEvent.getXPos() * PIXELS_PER_METER);
                robotView.setY(driveEvent.getYPos() * PIXELS_PER_METER);
                robotView.setRotate(driveEvent.getTheta());

                MiscHelper.highlightImage(robotView);

                fieldPane.getChildren().add(robotView);
            }
    }
}
