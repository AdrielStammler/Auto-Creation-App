package com.cpr3663.autocreation.objects;

public class Event {
    protected Runnable onChangeCallback;

    private String name;
    private String[] parameters;
    private boolean afterPrev;
    private DelayTypes delayType;
    private int delay;

    public Event(String name, String[] parameters, boolean afterPrev, DelayTypes delayType, int delay) {
        this.name = name;
        this.parameters = parameters;
        this.afterPrev = afterPrev;
        this.delayType = DelayTypes.NONE;
        setDelayType(delayType);
        this.delay = delay;
    }

    public Event(String name, String[] parameters) {
        this(name, parameters, true, DelayTypes.NONE, 0);
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
        onChangeCallback.run();
    }

    public boolean isAfterPrev() {
        return afterPrev;
    }

    public void setAfterPrev(boolean afterPrev) {
        this.afterPrev = afterPrev;
        if (onChangeCallback != null) onChangeCallback.run();
    }

    public DelayTypes getDelayType() {
        return delayType;
    }

    public void setDelayType(DelayTypes delayType) {
        if (!afterPrev || delayType.worksAfterPrev()) {
            this.delayType = delayType;
            if (onChangeCallback != null) onChangeCallback.run();
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
        if (onChangeCallback != null) onChangeCallback.run();
    }

    @Override
    public String toString() {
        return name;
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
}
