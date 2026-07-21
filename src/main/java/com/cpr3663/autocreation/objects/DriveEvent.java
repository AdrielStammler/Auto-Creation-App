package com.cpr3663.autocreation.objects;

import com.cpr3663.autocreation.Constants;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;

public class DriveEvent extends Event {
    // TODO make pose2d and so make cleaner implementation
    private double xPos;
    private double yPos;
    private double theta;
    private double maxVelocity;
    private double maxAcceleration;

    // TODO implement
    // IF ID = -1 then it's not a tag it's other (e.g. origin)
    private AprilTag relativeFrom = new AprilTag(-1, new Pose3d());

    public DriveEvent(double xPos, double yPos, double theta, boolean afterPrev, DelayTypes delayType, int delay) {
        this(xPos, yPos, theta, -1, -1, afterPrev, delayType, delay);
    }

    public DriveEvent(double xPos, double yPos, double theta, double maxVel, double maxAccel, boolean afterPrev, DelayTypes delayType, int delay) {
        super(Constants.Events.DRIVE_NAME, new String[]{Double.toString(xPos), Double.toString(yPos), Double.toString(theta), Double.toString(maxVel), Double.toString(maxAccel)}, afterPrev, delayType, delay);
        this.xPos = xPos;
        this.yPos = yPos;
        this.theta = theta;
        this.maxVelocity = maxVel;
        this.maxAcceleration = maxAccel;
    }

    public DriveEvent() {
        this(0, 0, 0, -1, -1, true, DelayTypes.NONE, 0);
    }

    public AprilTag getRelativeFrom() {
        return relativeFrom;
    }

    public void setRelativeFrom(AprilTag relativeFrom) {
        this.relativeFrom = relativeFrom;
    }

    public double getRelativeX() {
        return xPos - relativeFrom.pose.getX();
    }

    public double getRelativeY() {
        return yPos - relativeFrom.pose.getY();
    }

    public double getRelativeTheta() {
        return theta - relativeFrom.pose.getRotation().toRotation2d().getDegrees();
    }

    public void setPosition(Translation2d position) {
        setXPos(position.getX());
        setYPos(position.getY());
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
        updateParams();
    }

    public double getYPos() {
        return yPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
        updateParams();
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
        updateParams();
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
        super.setParameters(new String[]{Double.toString(xPos), Double.toString(yPos), Double.toString(theta), Double.toString(maxVelocity), Double.toString(maxAcceleration)});
        if (super.onChangeCallback != null) super.onChangeCallback.run();
    }

    @Override
    public boolean isDriveEvent() {
        return true;
    }

    @Override
    public String toString() {
        return "Drive to (" + round(xPos) + ", " + round(yPos) + ")";
    }


    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
