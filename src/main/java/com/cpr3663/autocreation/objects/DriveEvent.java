package com.cpr3663.autocreation.objects;

import com.cpr3663.autocreation.Constants;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.math.geometry.*;

public class DriveEvent extends Event {
    private Pose2d pose;
    private double threshold;
    private double maxVelocity;
    private double maxAcceleration;

    // IF ID = -1 then it's not a tag it's other (e.g. origin)
    private AprilTag relativeFrom = new AprilTag(-1, new Pose3d());

    public DriveEvent(double xPos, double yPos, double theta, double threshold, boolean afterPrev, DelayTypes delayType, int delay) {
        this(xPos, yPos, theta, threshold, -1, -1, afterPrev, delayType, delay);
    }

    public DriveEvent(double xPos, double yPos, double theta, double threshold, double maxVel, double maxAccel, boolean afterPrev, DelayTypes delayType, int delay) {
        super(Constants.Events.DRIVE_NAME, new String[]{}, afterPrev, delayType, delay);
        this.pose = new Pose2d(xPos, yPos, Rotation2d.fromDegrees(theta));
        this.threshold = threshold;
        this.maxVelocity = maxVel;
        this.maxAcceleration = maxAccel;
        updateParams();
    }

    public DriveEvent() {
        this(0, 0, 0, 1, -1, -1, true, DelayTypes.NONE, 0);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public AprilTag getRelativeFrom() {
        return relativeFrom;
    }

    public void setRelativeFrom(AprilTag relativeFrom) {
        this.relativeFrom = relativeFrom;
    }

    public Pose2d getPose() {
        return pose;
    }

    public Transform2d getRelativePose() {
        return pose.minus(relativeFrom.pose.toPose2d());
    }

    public Translation2d getRelativePosition() {
        return relativeFrom.pose.toPose2d().getTranslation().minus(pose.getTranslation());
    }

    public double getRelativeTheta() {
        return pose.getRotation().minus(relativeFrom.pose.getRotation().toRotation2d()).getDegrees();
    }

    public void setPosition(double x, double y) {
        setPosition(new Translation2d(x, y));
    }

    public void setPosition(Translation2d position) {
        pose = new Pose2d(position, pose.getRotation());
        updateParams();
    }

    public double getXPos() {
        return pose.getX();
    }

    public void setXPos(double xPos) {
        pose = new Pose2d(xPos, pose.getY(), pose.getRotation());
        updateParams();
    }

    public double getYPos() {
        return pose.getY();
    }

    public void setYPos(double yPos) {
        pose = new Pose2d(pose.getX(), yPos, pose.getRotation());
        updateParams();
    }

    public double getTheta() {
        return pose.getRotation().getDegrees();
    }

    public void setTheta(double theta) {
        pose = new Pose2d(pose.getTranslation(), Rotation2d.fromDegrees(theta));
        updateParams();
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(int maxVelocity) {
        this.maxVelocity = maxVelocity;
        updateParams();
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMaxAcceleration(int maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
        updateParams();
    }

    private void updateParams() {
        super.setParameters(new String[]{Double.toString(pose.getX()), Double.toString(pose.getY()), Double.toString(getTheta()), Double.toString(threshold), Double.toString(maxVelocity), Double.toString(maxAcceleration)});
    }

    @Override
    public boolean isDriveEvent() {
        return true;
    }

    @Override
    public String toString() {
        return "Drive to (" + round(getXPos()) + ", " + round(getYPos()) + ")";
    }
}
