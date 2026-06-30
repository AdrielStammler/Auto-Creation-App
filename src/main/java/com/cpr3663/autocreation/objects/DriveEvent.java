package com.cpr3663.autocreation.objects;

import com.cpr3663.autocreation.Constants;

public class DriveEvent extends Event {
    // TODO TEMP (Makes events distinguishable because they are identical currently)
    private final int rand;

    private int xPos;
    private int yPos;
    private int theta;
    private int maxVelocity;
    private int maxAcceleration;

    public DriveEvent(int xPos, int yPos, int theta, boolean afterPrev, DelayTypes delayType, int delay) {
        this(xPos, yPos, theta, -1, -1, afterPrev, delayType, delay);
    }

    public DriveEvent(int xPos, int yPos, int theta, int maxVel, int maxAccel, boolean afterPrev, DelayTypes delayType, int delay) {
        super(Constants.DRIVE_NAME, new String[]{Integer.toString(xPos), Integer.toString(yPos), Integer.toString(theta), Integer.toString(maxVel), Integer.toString(maxAccel)}, afterPrev, delayType, delay);
        this.xPos = xPos;
        this.yPos = yPos;
        this.theta = theta;
        this.maxVelocity = maxVel;
        this.maxAcceleration = maxAccel;


        this.rand = Math.toIntExact(Math.round(Math.random() * 1000));
    }

    public DriveEvent() {
        super(Constants.DRIVE_NAME, new String[]{"0", "0", "0", "-1", "-1"});
        this.xPos = 0;
        this.yPos = 0;
        this.theta = 0;
        this.maxVelocity = -1;
        this.maxAcceleration = -1;


        this.rand = Math.toIntExact(Math.round(Math.random() * 1000));
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
        updateParams();
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
        updateParams();
    }

    public int getTheta() {
        return theta;
    }

    public void setTheta(int theta) {
        this.theta = theta;
        updateParams();
    }

    public int getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(int maxVelocity) {
        this.maxVelocity = maxVelocity;
        updateParams();
    }

    public int getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMaxAcceleration(int maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
        updateParams();
    }

    private void updateParams() {
        super.setParameters(new String[]{Integer.toString(xPos), Integer.toString(yPos), Integer.toString(theta), Integer.toString(maxVelocity), Integer.toString(maxAcceleration)});
    }

    @Override
    public String toString() {
        return "Drive " + rand;
    }
}
