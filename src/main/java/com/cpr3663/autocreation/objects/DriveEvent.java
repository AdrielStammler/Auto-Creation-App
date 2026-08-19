package com.cpr3663.autocreation.objects;

import com.cpr3663.autocreation.Constants;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.math.geometry.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DriveEvent extends Event {
    private final Property<Pose2d> pose;
    private final DoubleProperty threshold;
    private final DoubleProperty maxVelocity;
    private final DoubleProperty maxAcceleration;

    // IF ID = -1 then it's not a tag it's other (e.g. origin)
    private final Property<AprilTag> relativeFrom = new SimpleObjectProperty<>(new AprilTag(-1, new Pose3d()));

    public DriveEvent(double xPos, double yPos, double theta, double threshold, boolean afterPrev, DelayTypes delayType, double delay) {
        this(xPos, yPos, theta, threshold, -1, -1, afterPrev, delayType, delay);
    }

    public DriveEvent(double xPos, double yPos, double theta, double threshold, double maxVel, double maxAccel, boolean afterPrev, DelayTypes delayType, double delay) {
        super(Constants.Events.DRIVE_NAME, new String[]{}, null, afterPrev, delayType, delay);
        this.pose = new SimpleObjectProperty<>(new Pose2d(xPos, yPos, Rotation2d.fromDegrees(theta)));
        this.threshold = new SimpleDoubleProperty(threshold);
        this.maxVelocity = new SimpleDoubleProperty(maxVel);
        this.maxAcceleration = new SimpleDoubleProperty(maxAccel);
        updateParams();
    }

    public DriveEvent() {
        this(0, 0, 0, 1, -1, -1, true, DelayTypes.NONE, 0);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public Property<AprilTag> relativeFromProperty() {
        return relativeFrom;
    }

    public AprilTag getRelativeFrom() {
        return relativeFrom.getValue();
    }

    public void setRelativeFrom(AprilTag relativeFrom) {
        this.relativeFrom.setValue(relativeFrom);
    }

    public Property<Pose2d> poseProperty() {
        return pose;
    }

    public Pose2d getPose() {
        return pose.getValue();
    }

    public Transform2d getRelativePose() {
        return getPose().minus(getRelativeFrom().pose.toPose2d());
    }

    public Translation2d getRelativePosition() {
        return getRelativeFrom().pose.toPose2d().getTranslation().minus(getPose().getTranslation());
    }

    public double getRelativeTheta() {
        return getPose().getRotation().minus(getRelativeFrom().pose.getRotation().toRotation2d()).getDegrees();
    }

    public void setPosition(double x, double y) {
        setPosition(new Translation2d(x, y));
    }

    public void setPosition(Translation2d position) {
        pose.setValue(new Pose2d(position, getPose().getRotation()));
        updateParams();
    }

    public double getXPos() {
        return getPose().getX();
    }

    public void setXPos(double xPos) {
        pose.setValue(new Pose2d(xPos, getPose().getY(), getPose().getRotation()));
        updateParams();
    }

    public double getYPos() {
        return getPose().getY();
    }

    public void setYPos(double yPos) {
        pose.setValue(new Pose2d(getPose().getX(), yPos, getPose().getRotation()));
        updateParams();
    }

    public double getTheta() {
        return getPose().getRotation().getDegrees();
    }

    public void setTheta(double theta) {
        pose.setValue(new Pose2d(getPose().getTranslation(), Rotation2d.fromDegrees(theta)));
        updateParams();
    }

    public DoubleProperty thresholdProperty() {
        return threshold;
    }

    public double getThreshold() {
        return threshold.getValue();
    }

    public void setThreshold(double threshold) {
        this.threshold.set(threshold);
    }

    public DoubleProperty maxVelocityProperty() {
        return maxVelocity;
    }

    public double getMaxVelocity() {
        return maxVelocity.getValue();
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity.set(maxVelocity);
        updateParams();
    }

    public DoubleProperty maxAccelerationProperty() {
        return maxAcceleration;
    }

    public double getMaxAcceleration() {
        return maxAcceleration.get();
    }

    public void setMaxAcceleration(double maxAcceleration) {
        this.maxAcceleration.set(maxAcceleration);
        updateParams();
    }

    private void updateParams() {
        super.setParameters(new String[]{Double.toString(pose.getValue().getX()), Double.toString(pose.getValue().getY()), Double.toString(getTheta()), Double.toString(getThreshold()), Double.toString(getMaxVelocity()), Double.toString(getMaxAcceleration())});
    }

    @Override
    public String toString() {
        return "Drive to (" + round(getXPos()) + ", " + round(getYPos()) + ")";
    }
}
