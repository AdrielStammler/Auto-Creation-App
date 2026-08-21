package com.cpr3663.autocreation.objects;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;

public class RefreshableListProperty<T> extends SimpleListProperty<T> {
    public RefreshableListProperty(ObservableList<T> initialValue) {
        super(initialValue);
    }

    public void forceRefresh() {
        ObservableList<T> list = get();
        if (list == null || list.isEmpty()) {
            super.fireValueChangedEvent();
            return;
        }

        super.fireValueChangedEvent(new ListChangeListener.Change<>(list) {
            private boolean iterated;

            @Override
            public boolean next() {
                if (!iterated) {
                    iterated = true;
                    return true;
                }
                return false;
            }

            @Override
            public void reset() {
                iterated = false;
            }

            @Override
            public int getFrom() {
                return 0;
            }

            @Override
            public int getTo() {
                return list.size();
            }

            @Override
            public List<T> getRemoved() {
                return Collections.emptyList();
            }

            @Override
            protected int[] getPermutation() {
                return new int[0];
            }

            @Override
            public boolean wasUpdated() {
                return true;
            }
        });
    }
}
