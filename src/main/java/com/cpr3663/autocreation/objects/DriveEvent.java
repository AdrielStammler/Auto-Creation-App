package com.cpr3663.autocreation.objects;

import com.cpr3663.autocreation.Constants;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.math.geometry.*;
import javafx.beans.property.*;

import java.util.function.DoubleSupplier;

public class DriveEvent extends Event {
    private final DoubleProperty x;
    private final DoubleProperty y;
    private final DoubleProperty theta;
    private final DoubleProperty threshold;
    private final DoubleProperty maxVelocity;
    private final DoubleProperty maxAcceleration;

    // IF ID = -1 then it's not a tag it's other (e.g. origin)
    private final Property<AprilTag> aprilTag = new SimpleObjectProperty<>(new AprilTag(-1, new Pose3d()));

    public DriveEvent(double xPos, double yPos, double theta, double threshold, boolean afterPrev, DelayTypes delayType, double delay) {
        this(xPos, yPos, theta, threshold, -1, -1, afterPrev, delayType, delay);
    }

    public DriveEvent(double xPos, double yPos, double theta, double threshold, double maxVel, double maxAccel, boolean afterPrev, DelayTypes delayType, double delay) {
        super(Constants.Events.DRIVE_NAME, new String[]{}, null, afterPrev, delayType, delay);
        this.x = new SimpleDoubleProperty(xPos);
        this.y = new SimpleDoubleProperty(yPos);
        this.theta = new SimpleDoubleProperty(theta);
        this.threshold = new SimpleDoubleProperty(threshold);
        this.maxVelocity = new SimpleDoubleProperty(maxVel);
        this.maxAcceleration = new SimpleDoubleProperty(maxAccel);
        updateParams();

        addParamChangeListener(this.x);
        addParamChangeListener(this.y);
        addParamChangeListener(this.theta);
        addParamChangeListener(this.threshold);
        addParamChangeListener(this.maxVelocity);
        addParamChangeListener(this.maxAcceleration);
    }

    public DriveEvent() {
        this(0, 0, 0, 1, -1, -1, true, DelayTypes.NONE, 0);
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private <T> void addParamChangeListener(Property<T> property) {
        property.addListener((obs, old, newV) -> updateParams());
    }

    public Property<AprilTag> aprilTagProperty() {
        return aprilTag;
    }

    public AprilTag getAprilTag() {
        return aprilTag.getValue();
    }

    public void setAprilTag(AprilTag relativeFrom) {
        this.aprilTag.setValue(relativeFrom);
    }

    public Pose2d getPose() {
        return new Pose2d(getX(), getY(), Rotation2d.fromDegrees(getTheta()));
    }

    public DoubleProperty relativeXProperty() {
        return new RelativeDoubleProperty(this, "relativeX", x, aprilTag, () -> aprilTag.getValue().pose.getX());
    }

    public DoubleProperty relativeYProperty() {
        return new RelativeDoubleProperty(this, "relativeY", y, aprilTag, () -> aprilTag.getValue().pose.getY());
    }

    public DoubleProperty relativeThetaProperty() {
        return new RelativeDoubleProperty(this, "relativeTheta", theta, aprilTag, () -> aprilTag.getValue().pose.getRotation().getZ());
    }

    public Transform2d getRelativePose() {
        return getPose().minus(getAprilTag().pose.toPose2d());
    }

    public Translation2d getRelativePosition() {
        return getRelativePose().getTranslation();
    }

    public void setPosition(double x, double y) {
        this.x.set(x);
        this.y.set(y);
    }

    public void setPosition(Translation2d position) {
        setPosition(position.getX(), position.getY());
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getX() {
        return x.get();
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public double getY() {
        return y.get();
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public DoubleProperty thetaProperty() {
        return theta;
    }

    public double getTheta() {
        return theta.get();
    }

    public void setTheta(double theta) {
        this.theta.set(theta);
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
    }

    public DoubleProperty maxAccelerationProperty() {
        return maxAcceleration;
    }

    public double getMaxAcceleration() {
        return maxAcceleration.get();
    }

    public void setMaxAcceleration(double maxAcceleration) {
        this.maxAcceleration.set(maxAcceleration);
    }

    private void updateParams() {
        super.setParameters(new String[]{Double.toString(getX()), Double.toString(getY()), Double.toString(getTheta()), Double.toString(getThreshold()), Double.toString(getMaxVelocity()), Double.toString(getMaxAcceleration())});
    }

    @Override
    public String toString() {
        return "Drive to (" + round(getX()) + ", " + round(getY()) + ")";
    }

    private static class RelativeDoubleProperty extends DoublePropertyBase {
        private final DoubleProperty absoluteProperty;
        private final Property<?> offsetProperty;
        private final DoubleSupplier offsetSupplier;
        private final Object bean;
        private final String name;

        /**
         * Creates a bidirectional relative property wrapper.
         *
         * @param bean             The bean that owns this property.
         * @param name             The name of this property.
         * @param absoluteProperty The source/absolute property.
         * @param offsetProperty      The observable offset object property.
         * @param offsetSupplier   A Supplier for the dynamic offset that isn't a property.
         */
        public RelativeDoubleProperty(Object bean, String name,
                                      DoubleProperty absoluteProperty,
                                      Property<?> offsetProperty,
                                      DoubleSupplier offsetSupplier) {
            this.bean = bean;
            this.name = name;
            this.absoluteProperty = absoluteProperty;
            this.offsetProperty = offsetProperty;
            this.offsetSupplier = offsetSupplier;

            this.absoluteProperty.addListener(obs -> this.fireValueChangedEvent());
            this.offsetProperty.addListener(obs -> this.fireValueChangedEvent());
        }

        @Override
        public double get() {
            return absoluteProperty.get() - getOffset();
        }

        @Override
        public void set(double newValue) {
            absoluteProperty.set(newValue + getOffset());
        }

        private double getOffset() {
            if (offsetProperty.getValue() == null) {
                return 0.0;
            }
            return offsetSupplier.getAsDouble();
        }

        @Override
        public Object getBean() {
            return bean;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
