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
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Field {
    private static final double[] initials = new double[2];
    private static final boolean[] isRotating = new boolean[1];
    private static double PIXELS_PER_METER;
    private static double FIELD_WIDTH;
    private static ImageView selectedImageView;
    private static Pane selectedAprilTag;
    private static Pane fieldPane;
    private static boolean isDrag;
    private static boolean mayMove;

    public static Pane getFieldPane() {
        AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AppStateManager.getInstance().getAprilTagField());
        PIXELS_PER_METER = 75.0 * AppStateManager.getInstance().getFieldScale();
        FIELD_WIDTH = fieldLayout.getFieldWidth();
        Pane pane = getPane(fieldLayout);
        drawAprilTags(pane, fieldLayout);
        drawRobotPoses(pane);
        pane.setOnMousePressed(Helper::pressPane);
        pane.setOnMouseDragged(Helper::drag);
        pane.setOnMouseReleased((MouseEvent event) -> {
            AppStateManager.getInstance().setFieldEditing();
            if (isDrag || !mayMove) return;

            Event selected = AppStateManager.getInstance().getSelectedEvent();
            if (selected instanceof DriveEvent driveEvent) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    Helper.rotate(event, driveEvent);
                    return;
                }

                driveEvent.setPosition(Helper.fromPixels(event.getX(), event.getY()));

                Translation2d visualPosition = Helper.centerRobotPixels(event.getX(), event.getY());

                Helper.updateRobotPos(visualPosition.getX(), visualPosition.getY());
            }
        });
        fieldPane = pane;
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
        if (event instanceof DriveEvent driveEvent)
            selected = driveEvent.getAprilTag();
        else selected = new AprilTag(-2, new Pose3d());

        for (AprilTag tag : fieldLayout.getTags()) {
            Pose2d pose = tag.pose.toPose2d();
            int id = tag.ID;
            boolean isSelected = selected.equals(tag);

            Translation2d position = pose.getTranslation();
            position = Helper.toPixels(position);

            Rotation2d rotation = pose.getRotation();
            rotation = rotation.plus(Rotation2d.fromDegrees(-90));

            StackPane tagPane = createTagVisual(id, rotation.getDegrees(), isSelected);
            tagPane.layoutXProperty().bind(tagPane.widthProperty().divide(-2).add(position.getX()));
            tagPane.layoutYProperty().bind(tagPane.heightProperty().divide(-2).add(position.getY()));

            tagPane.setPickOnBounds(true);
            tagPane.setOnMousePressed(e -> {
                AppStateManager.getInstance().setFieldEditing();
                e.consume();
                if (e.isShiftDown()) {
                    Helper.pressPane(e);
                } else {
                    mayMove = false;
                    Helper.setEventTag(fieldLayout, tagPane, id);
                }
            });

            fieldPane.getChildren().add(tagPane);
        }
    }

    private static StackPane createTagVisual(int id, double rotationDeg, boolean isSelected) {
        StackPane tagPane = new StackPane();

        Image tag = new Image(Objects.requireNonNull(Field.class.getResource(Constants.Paths.TAG_ICON)).toExternalForm());
        ImageView tagView = new ImageView(tag);

        Label label = new Label(Integer.toString(id));
        label.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 10px;");

        DropShadow outline = new DropShadow();
        outline.setColor(Color.WHITE);
        outline.setRadius(2);
        outline.setSpread(1.0);
        label.setEffect(outline);

        tagPane.getChildren().addAll(tagView, label);

        Rotate rotate = new Rotate(-rotationDeg);
        rotate.pivotXProperty().bind(tagPane.widthProperty().divide(2));
        rotate.pivotYProperty().bind(tagPane.heightProperty().divide(2));
        tagPane.getTransforms().add(rotate);

        if (isSelected) {
            Helper.highlightImage(tagPane);
        }

        tagPane.setId("Tag-" + id);

        return tagPane;
    }

    private static void drawRobotPoses(Pane fieldPane) {
        Event selectedEvent = AppStateManager.getInstance().getSelectedEvent();
        double sizeX = AppStateManager.getInstance().getRobotSize().getX();
        Line prevLine = null;

        ObservableList<Event> events = AppStateManager.getInstance().getEvents();
        ArrayList<ImageView> robots = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event instanceof DriveEvent driveEvent) {
                Image robot = new Image(Objects.requireNonNull(Field.class.getResource(Constants.Paths.ROBOT_ICON)).toExternalForm());
                ImageView robotView = new ImageView(robot);

                Translation2d position = Helper.centerRobotPixels(Helper.toPixels(driveEvent.getX(), driveEvent.getY()));

                robotView.setX(position.getX());
                robotView.setY(position.getY());
                robotView.setRotate(driveEvent.getTheta() + 90.0);

                robotView.setPreserveRatio(true);
                robotView.setSmooth(true);
                robotView.setFitWidth(sizeX * PIXELS_PER_METER);

                if (event.equals(selectedEvent)) {
                    Helper.highlightImage(robotView);
                    selectedImageView = robotView;

                    fieldPane.getChildren().add(Helper.createThreshold(robotView.getX(), robotView.getY(), driveEvent.getThreshold()));
                } else Helper.colorRobot(robotView);

                if (prevLine != null) {
                    Helper.setLinePos(prevLine, position, false);
                    fieldPane.getChildren().add(prevLine);
                }

                Line line = new Line();
                Helper.setLinePos(line, position, true);
                line.setId("Line-" + i);
                line.setStroke(Color.WHITE);
                line.setStrokeWidth(3);
                prevLine = line;

                robotView.setId("Event-" + i);
                addRobotListeners(driveEvent, robotView);
                robots.add(robotView);
            }
        }
        fieldPane.getChildren().addAll(robots);
    }

    private static void addRobotListeners(DriveEvent event, ImageView robot) {
        robot.setPickOnBounds(true);
        robot.setOnMousePressed(e -> {
            if (e.isShiftDown()) Helper.pressPane(e);
            else Helper.press(e, event, robot, true);
        });
        robot.setOnMouseDragged(e -> Helper.drag(e, event));
    }

    public static class Helper {
        private static final Color HIGHLIGHT_COLOR = Color.ORANGE;
        private static final Color NORMAL_ROBOT_COLOR = Color.WHITESMOKE;

        private static void highlightImage(Node image) {
            changeColor(image, HIGHLIGHT_COLOR);
        }

        private static void colorRobot(Node image) {
            changeColor(image, NORMAL_ROBOT_COLOR);
        }

        private static void changeColor(Node image, Color color) {
            Light.Distant distantLight = new Light.Distant();
            distantLight.setElevation(90);
            distantLight.setColor(color);
            Lighting lighting = new Lighting();
            lighting.setLight(distantLight);
            lighting.setSurfaceScale(0.0);
            image.setEffect(lighting);
        }

        private static void clearColor(Node image) {
            image.setEffect(null);
        }

        public static void updateSelection() {
            Event selectedEvent = AppStateManager.getInstance().getSelectedEvent();
            Circle threshold = (Circle) fieldPane.lookup("#Threshold");
            if (threshold != null) fieldPane.getChildren().remove(threshold);
            if (selectedImageView != null) colorRobot(selectedImageView);
            if (selectedAprilTag != null) clearColor(selectedAprilTag);
            if (!(selectedEvent instanceof DriveEvent)) {
                selectedImageView = null;
                selectedAprilTag = null;
                return;
            }
            selectedImageView = (ImageView) fieldPane.lookup("#Event-" + AppStateManager.getInstance().getSelectedIndex());
            if (selectedImageView == null) throw new IllegalStateException("Selected Event does not exist on the fieldPane");
            highlightImage(selectedImageView);

            fieldPane.getChildren().add(createThreshold(selectedImageView.getX(), selectedImageView.getY(), ((DriveEvent) selectedEvent).getThreshold()));

            AprilTag aprilTag = ((DriveEvent) selectedEvent).getAprilTag();
            if (aprilTag == null || aprilTag.ID == -1) return;
            selectedAprilTag = (Pane) fieldPane.lookup("#Tag-" + aprilTag.ID);
            highlightImage(selectedAprilTag);
        }

        public static Translation2d unCenterRobotPixels(Translation2d position) {
            return unCenterRobotPixels(position.getX(), position.getY());
        }

        private static Translation2d unCenterRobotPixels(double x, double y) {
            Translation2d robotSize = AppStateManager.getInstance().getRobotSize().times(PIXELS_PER_METER);
            return new Translation2d(x + (robotSize.getX() / 2), y + (robotSize.getY() / 2) + Constants.ROBOT_IMAGE_Y_EXTRA_PIXELS);
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
            mayMove = true;
            isDrag = false;
            e.consume();
            if (selectedImageView == null) return;
            if (event != null && !selectedImageView.equals(robot)) {
                AppStateManager.getInstance().setSelectedEvent(event);
                updateSelection();
            }
            boolean shouldRotate = e.isSecondaryButtonDown();
            if (isRobot && !shouldRotate) {
                double width = robot.getBoundsInLocal().getWidth();
                double height = robot.getBoundsInLocal().getHeight();
                Translation2d scenePos = new Translation2d(e.getSceneX(), e.getSceneY());
                Bounds bounds = robot.localToScene(robot.getBoundsInLocal());
                Translation2d pos = scenePos.minus(new Translation2d(bounds.getCenterX(), bounds.getCenterY()));
                Translation2d target = new Translation2d(0, -(height / 2) * 9 / 10);
                target = target.rotateBy(Rotation2d.fromDegrees(robot.getRotate()));

                shouldRotate = pos.getDistance(target) <= width / 4;
            }
            if (shouldRotate) {
                isRotating[0] = true;
                return;
            }

            isRotating[0] = false;
            initials[0] = e.getSceneX();
            initials[1] = e.getSceneY();
        }

        private static void drag(MouseEvent e) {
            drag(e, null);
        }

        private static void drag(MouseEvent e, DriveEvent event) {
            AppStateManager.getInstance().setFieldEditing();
            if (!mayMove) return;
            isDrag = true;
            e.consume();
            if (selectedImageView == null) return;
            if (event != null && !AppStateManager.getInstance().getSelectedEvent().equals(event)) return;

            if (event == null) {
                Event tempEvent = AppStateManager.getInstance().getSelectedEvent();
                if (tempEvent instanceof DriveEvent) event = (DriveEvent) tempEvent;
                else return;
            }

            if (isRotating[0]) {
                rotate(e, event);
            } else {
                Translation2d delta = new Translation2d(e.getSceneX() - initials[0], e.getSceneY() - initials[1]);
                Translation2d currPos = new Translation2d(selectedImageView.getX(), selectedImageView.getY());
                Translation2d newPos = currPos.plus(delta);

                updateRobotPos(newPos.getX(), newPos.getY());

                event.setPosition(fromPixels(unCenterRobotPixels(newPos)));

                initials[0] = e.getSceneX();
                initials[1] = e.getSceneY();
            }
        }

        private static void rotate(MouseEvent e, DriveEvent event) {
            Bounds bounds = selectedImageView.localToScene(selectedImageView.getBoundsInLocal());
            Translation2d center = new Translation2d(bounds.getCenterX(), bounds.getCenterY());

            double currentAngle = Math.toDegrees(Math.atan2(e.getSceneY() - center.getY(), e.getSceneX() - center.getX()));

            event.setTheta(currentAngle);
            selectedImageView.setRotate(currentAngle + 90.0);
        }

        private static Circle createThreshold(double x, double y, double thresholdM) {
            Translation2d pos = unCenterRobotPixels(x, y);
            Circle threshold = new Circle(pos.getX(), pos.getY(), thresholdM * PIXELS_PER_METER);
            threshold.setMouseTransparent(true);
            threshold.setFill(Color.TRANSPARENT);
            threshold.setStroke(HIGHLIGHT_COLOR);
            threshold.setStrokeWidth(3.0);
            final int totalDashes = 20;
            threshold.radiusProperty().addListener((observable, oldValue, newRadius) -> {
                double circumference = 2 * Math.PI * newRadius.doubleValue();
                double dashLength = circumference / (totalDashes * 2);
                if (dashLength == 0.0) dashLength = 1;

                threshold.getStrokeDashArray().setAll(dashLength, dashLength);
            });

            double initialLength = Math.max((2.0 * Math.PI * threshold.getRadius()) / (totalDashes * 2), 1.0);
            threshold.getStrokeDashArray().setAll(initialLength, initialLength);
            threshold.setId("Threshold");
            return threshold;
        }

        private static void updateRobotPos(double pixelsX, double pixelsY) {
            selectedImageView.setX(pixelsX);
            selectedImageView.setY(pixelsY);

            String indexStr = selectedImageView.getId().substring(6);

            int index = Integer.parseInt(indexStr);

            Line toLine = (Line) fieldPane.lookup("#Line-" + (index - 1));
            Line fromLine = (Line) fieldPane.lookup("#Line-" + (index));

            if (toLine != null) {
                setLinePos(toLine, pixelsX, pixelsY, false);
            }
            if (fromLine != null) {
                setLinePos(fromLine, pixelsX, pixelsY, true);
            }

            Circle threshold = (Circle) fieldPane.lookup("#Threshold");
            if (threshold == null) throw new IllegalStateException("Threshold could not be found");
            Translation2d pos = unCenterRobotPixels(pixelsX, pixelsY);
            threshold.setCenterX(pos.getX());
            threshold.setCenterY(pos.getY());
        }

        /**
         * @param line      the line to modify
         * @param x         the x position of the robot (uncentered)
         * @param y         the y position of the robot (uncentered)
         * @param isStart   if it should change the starting or ending pos of the line
         */
        private static void setLinePos(Line line, double x, double y, boolean isStart) {
            Translation2d pos = unCenterRobotPixels(x, y);
            if (isStart) {
                line.setStartX(pos.getX());
                line.setStartY(pos.getY());
            } else {
                line.setEndX(pos.getX());
                line.setEndY(pos.getY());
            }
        }

        private static void setLinePos(Line line, Translation2d pos, boolean isStart) {
            setLinePos(line, pos.getX(), pos.getY(), isStart);
        }

        private static void setEventTag(AprilTagFieldLayout fieldLayout, Pane tagView, int id) {
            Event event = AppStateManager.getInstance().getSelectedEvent();
            if (selectedAprilTag != null) {
                clearColor(selectedAprilTag);
                selectedAprilTag = null;
            }
            if (event instanceof DriveEvent driveEvent) {
                Optional<Pose3d> optPose = fieldLayout.getTagPose(id);
                AprilTag tag = optPose.map(pose -> new AprilTag(id, pose)).orElse(new AprilTag(-1, new Pose3d()));
                driveEvent.setAprilTag(tag);
                highlightImage(tagView);
                selectedAprilTag = tagView;
            }
        }
    }
}
