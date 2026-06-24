package com.cpr3663.autocreation;

import javafx.application.HostServices;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppStateManager {
    // Single instance of the state manager
    private static final AppStateManager INSTANCE = new AppStateManager();

    private AppStateManager() {}

    public static AppStateManager getInstance() {
        return INSTANCE;
    }

    // Creating Properties
    private final ObjectProperty<HostServices> hostServices = new SimpleObjectProperty<>(null);
    private final BooleanProperty isSaved = new SimpleBooleanProperty(true);

    // Getting properties, Getting values, and Setting values
    public ObjectProperty<HostServices> hostServicesProperty() {
        return hostServices;
    }

    public HostServices getHostServices() {
        return hostServices.get();
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices.set(hostServices);
    }

    public BooleanProperty isSavedProperty() {
        return isSaved;
    }

    public boolean isSaved() {
        return isSaved.get();
    }

    public void setIsSaved(boolean isSaved) {
        this.isSaved.set(isSaved);
    }
}
