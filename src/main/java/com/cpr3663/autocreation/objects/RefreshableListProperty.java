package com.cpr3663.autocreation.objects;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;

public class RefreshableListProperty<T> extends SimpleListProperty<T> {
    public RefreshableListProperty(ObservableList<T> initialValue) {
        super(initialValue);
    }

    public void forceRefresh() {
        this.fireValueChangedEvent();
    }
}
