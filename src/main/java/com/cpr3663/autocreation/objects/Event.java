package com.cpr3663.autocreation.objects;

import com.cpr3663.autocreation.Constants;
import javafx.beans.property.*;

import java.util.Arrays;
import java.util.List;

public class Event {
    protected Runnable onChangeCallback;

    private String name;
    private String[] parameters;
    private final BooleanProperty afterPrev;
    private final ObjectProperty<DelayTypes> delayType;
    private final DoubleProperty delay;

    public Event(String name, String[] parameters, boolean afterPrev, DelayTypes delayType, double delay) {
        this.name = name;
        this.parameters = parameters;
        this.afterPrev = new SimpleBooleanProperty(afterPrev);
        this.delayType = new SimpleObjectProperty<>(DelayTypes.NONE);
        setDelayType(delayType);
        this.delay = new SimpleDoubleProperty(delay);
    }

    public Event(String name, String[] parameters) {
        this(name, parameters, true, DelayTypes.NONE, 0.0);
    }

    public void setOnChangeCallback(Runnable onChangeCallback) {
        this.onChangeCallback = onChangeCallback;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (onChangeCallback != null) onChangeCallback.run();
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
        if (onChangeCallback != null) onChangeCallback.run();
    }

    public BooleanProperty afterPrevProperty() {
        return afterPrev;
    }

    public boolean isAfterPrev() {
        return afterPrev.get();
    }

    public void setAfterPrev(boolean afterPrev) {
        this.afterPrev.set(afterPrev);
        if (onChangeCallback != null) onChangeCallback.run();
    }

    public ObjectProperty<DelayTypes> delayTypeProperty() {
        return delayType;
    }

    public DelayTypes getDelayType() {
        return delayType.get();
    }

    public void setDelayType(DelayTypes delayType) {
        if (!afterPrev.get() || delayType.worksAfterPrev()) {
            this.delayType.set(delayType);
            if (onChangeCallback != null) onChangeCallback.run();
        }
    }

    public DoubleProperty delayProperty() {
        return delay;
    }

    public double getDelay() {
        return delay.get();
    }

    public void setDelay(double delay) {
        this.delay.set(delay);
        if (onChangeCallback != null) onChangeCallback.run();
    }

    @Override
    public String toString() {
        return name;
    }

    public String toFileRow() {
        String params = String.join(Constants.Events.PARAM_DELIMITER, parameters);
        return String.join(Constants.Events.DELIMITER, name, params, Boolean.toString(isAfterPrev()), getDelayType().name(), Double.toString(getDelay()));
    }

    public boolean isDriveEvent() {
        return false;
    }

    public enum DelayTypes {
        NONE(true),
        TIME(true),
        PROGRESS(false),
        DISTANCE(false);

        private final boolean worksAfterPrev;

        DelayTypes(boolean worksAfterPrev) {
            this.worksAfterPrev = worksAfterPrev;
        }

        public boolean worksAfterPrev() {
            return worksAfterPrev;
        }
    }

    public record Type(String name, String... parameters) {
        public Type(String name, List<String> parameters) {
            this(name, sortedArray(parameters));
        }

        private static String[] sortedArray(List<String> parameters) {
            if (parameters == null) {
                return new String[0];
            }
            return parameters.stream()
                    .sorted()
                    .toArray(String[]::new);
        }

        @Override
        public String toString() {
            return name + (parameters.length == 0 ? "" : ": " + Arrays.toString(parameters));
        }
    }
}
