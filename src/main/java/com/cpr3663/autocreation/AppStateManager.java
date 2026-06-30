package com.cpr3663.autocreation;

import com.cpr3663.autocreation.objects.Event;
import javafx.application.HostServices;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
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
    private HostServices hostServices;
    private final BooleanProperty isSaved = new SimpleBooleanProperty(true);

    private final ListProperty<Event> events = new SimpleListProperty<>(FXCollections.observableArrayList());

    // Persistent Ones
    private final BooleanProperty isDarkMode = new SimpleBooleanProperty(false);

    public HostServices getHostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
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

    public ListProperty<Event> eventsProperty() {
        return events;
    }

    public ObservableList<Event> getEvents() {
        return events.get();
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void setEvents(ArrayList<Event> events) {
        setEvents(FXCollections.observableArrayList(events));
    }

    public void setEvents(ObservableList<Event> events) {
        this.events.set(events);
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
