package com.cpr3663.autocreation;

import javafx.application.HostServices;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;


import java.util.prefs.Preferences;

public class AppStateManager {
    // Create a preferences and methods to save and load certain variables/settings
    private static final Preferences prefs = Preferences.userNodeForPackage(AppStateManager.class);

    public void saveState() {
        System.out.println("Saved state");
        prefs.putBoolean("isDarkMode", this.isDarkMode.get());
    }

    public void loadState() {
        // Syntax: prefs.get("KEY", "DEFAULT_VALUE_IF_NOT_FOUND");
        System.out.println("Loaded state");
        setIsDarkMode(prefs.getBoolean("isDarkMode", isDarkMode()));
    }

    // Single instance of the state manager
    private static final AppStateManager INSTANCE = new AppStateManager();

    private AppStateManager() {
        loadState();
    }

    public static AppStateManager getInstance() {
        return INSTANCE;
    }

    // Creating Properties
    private final ObjectProperty<HostServices> hostServices = new SimpleObjectProperty<>(null);
    private final BooleanProperty isSaved = new SimpleBooleanProperty(true);
    private final BooleanProperty isDarkMode = new SimpleBooleanProperty(false);

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

    public BooleanProperty isDarkModeProperty() {
        return isDarkMode;
    }

    public boolean isDarkMode() {
        return isDarkMode.get();
    }

    public void setIsDarkMode(boolean isDarkMode) {
        this.isDarkMode.set(isDarkMode);
    }
}
