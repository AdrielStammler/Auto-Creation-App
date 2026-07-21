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
import javafx.geometry.Bounds;
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
    private static ImageView selectedImageView;
    private static boolean isDrag = false;
    private static final double[] initials = new double[2];
    private static final boolean[] isRotating = new boolean[1];

    public static Pane getFieldPane() {
        AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AppStateManager.getInstance().getAprilTagField());
        FIELD_WIDTH = fieldLayout.getFieldWidth();
        Pane pane = getPane(fieldLayout);
        drawAprilTags(pane, fieldLayout);
        drawRobotPoses(pane);
        pane.setOnMousePressed(Helper::pressPane);
        pane.setOnMouseDragged(Helper::drag);
        pane.setOnMouseReleased((MouseEvent event) -> {
            if (isDrag) return;
            AppStateManager.getInstance().setFieldEditing();
            Event selected = AppStateManager.getInstance().getSelectedEvent();
            if (selected != null && selected.isDriveEvent()) {
                DriveEvent driveEvent = (DriveEvent) selected;
                driveEvent.setPosition(Helper.fromPixels(event.getX(), event.getY()));

                Translation2d visualPosition = Helper.centerRobotPixels(event.getX(), event.getY());

                selectedImageView.setX(visualPosition.getX());
                selectedImageView.setY(visualPosition.getY());
            }
        });
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
        if (event != null && event.isDriveEvent())
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

        tagNode.setOnMouseClicked(e -> AppStateManager.getInstance().setFieldEditing());

        return tagNode;
    }

    private static void drawRobotPoses(Pane fieldPane) {
        Event selectedEvent = AppStateManager.getInstance().getSelectedEvent();
        double sizeX = AppStateManager.getInstance().getRobotSize().getX();

        for (Event event : AppStateManager.getInstance().getEvents())
            if (event.isDriveEvent()) {
                DriveEvent driveEvent = (DriveEvent) event;
                Image robot = new Image(Objects.requireNonNull(Field.class.getResource(Constants.Paths.ROBOT_ICON)).toExternalForm());
                ImageView robotView = new ImageView(robot);

                Translation2d position = Helper.centerRobotPixels(Helper.toPixels(driveEvent.getXPos(), driveEvent.getYPos()));

                robotView.setX(position.getX());
                robotView.setY(position.getY());
                robotView.setRotate(driveEvent.getTheta() + 90.0);

                robotView.setPreserveRatio(true);
                robotView.setSmooth(true);
                robotView.setFitWidth(sizeX * PIXELS_PER_METER);

                if (event.equals(selectedEvent)) {
                    Helper.highlightImage(robotView);
                    selectedImageView = robotView;
                }
                else Helper.colorRobot(robotView);

                addRobotListeners(driveEvent, robotView);

                fieldPane.getChildren().add(robotView);
            }
    }

    private static void addRobotListeners(DriveEvent event, ImageView robot) {
        robot.setPickOnBounds(true);
        robot.setOnMousePressed(e -> Helper.press(e, event, robot, true));
        robot.setOnMouseDragged(e -> Helper.drag(e, event, robot));
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

        public static Translation2d centerRobotPixels(Translation2d position) {
            return centerRobotPixels(position.getX(), position.getY());
        }

        private static Translation2d centerRobotPixels(double x, double y) {
            Translation2d robotSize = AppStateManager.getInstance().getRobotSize().times(PIXELS_PER_METER);
            return new Translation2d(x - (robotSize.getX() / 2), y - (robotSize.getY() / 2) - Constants.ROBOT_IMAGE_Y_EXTRA_PIXELS);
        }

        private static Translation2d toPixels(Translation2d position) {
            return toPixels(position.getX(), position.getY());
        }

        private static Translation2d toPixels(double x, double y) {
            Translation2d position = new Translation2d(x, FIELD_WIDTH - y);
            return position.times(PIXELS_PER_METER);
        }

        private static Translation2d fromPixels(Translation2d pos) {
            return fromPixels(pos.getX(), pos.getY());
        }

        private static Translation2d fromPixels(double x, double y) {
            Translation2d position = new Translation2d(x, (FIELD_WIDTH * PIXELS_PER_METER) - y);
            return position.div(PIXELS_PER_METER);
        }

        private static void pressPane(MouseEvent e) {
            press(e, null, selectedImageView, false);
        }

        private static void press(MouseEvent e, DriveEvent event, ImageView robot, boolean isRobot) {
            AppStateManager.getInstance().setFieldEditing();
            isDrag = false;
            e.consume();
            if (selectedImageView == null) return;
            if (event != null && !selectedImageView.equals(robot)) {
                AppStateManager.getInstance().setSelectedEvent(event);
                Helper.colorRobot(selectedImageView);
                selectedImageView = robot;
                Helper.highlightImage(selectedImageView);
            }
            if (isRobot) {
                double width = robot.getBoundsInLocal().getWidth();
                double height = robot.getBoundsInLocal().getHeight();
                Translation2d scenePos = new Translation2d(e.getSceneX(), e.getSceneY());
                Bounds bounds = robot.localToScene(robot.getBoundsInLocal());
                Translation2d pos = scenePos.minus(new Translation2d(bounds.getCenterX(), bounds.getCenterY()));
                Translation2d target = new Translation2d(0, -(height / 2) * 9 / 10);
                target = target.rotateBy(Rotation2d.fromDegrees(robot.getRotate()));

                if (pos.getDistance(target) <= width / 4) {
                    isRotating[0] = true;
                    return;
                }
            }
            isRotating[0] = false;
            initials[0] = e.getSceneX();
            initials[1] = e.getSceneY();
        }

        private static void drag(MouseEvent e) {
            drag(e, null, selectedImageView);
        }

        private static void drag(MouseEvent e, DriveEvent event, ImageView robot) {
            AppStateManager.getInstance().setFieldEditing();
            isDrag = true;
            // TODO fix
            e.consume();
            if (robot == null) return;
            if (event != null && !AppStateManager.getInstance().getSelectedEvent().equals(event)) return;

            if (event == null) {
                Event tempEvent = AppStateManager.getInstance().getSelectedEvent();
                if (tempEvent.isDriveEvent()) event = (DriveEvent) tempEvent;
                else return;
            }

            if (isRotating[0]) {
                Bounds bounds = robot.localToScene(robot.getBoundsInLocal());
                Translation2d center = new Translation2d(bounds.getCenterX(), bounds.getCenterY());

                double currentAngle = Math.toDegrees(Math.atan2(e.getSceneY() - center.getY(), e.getSceneX() - center.getX()));

                event.setTheta(currentAngle);
                robot.setRotate(currentAngle + 90.0);
            } else {
                Translation2d delta = new Translation2d(e.getSceneX() - initials[0], e.getSceneY() - initials[1]);
                robot.setTranslateX(robot.getTranslateX() + delta.getX());
                robot.setTranslateY(robot.getTranslateY() + delta.getY());

                Translation2d currPos = new Translation2d(robot.getX() + robot.getTranslateX(), robot.getY() + robot.getTranslateY());
                Translation2d currPosM = fromPixels(centerRobotPixels(currPos));

                System.out.println("delta = " + delta);
                System.out.println("currPos = " + currPos);
                System.out.println("currPosM = " + currPosM);

                event.setXPos(currPosM.getX());
                event.setYPos(currPosM.getY());

                initials[0] = e.getSceneX();
                initials[1] = e.getSceneY();
            }
        }
    }
}
