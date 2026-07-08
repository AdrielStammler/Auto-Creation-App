package com.cpr3663.autocreation;

import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.objects.RefreshableListProperty;
import edu.wpi.first.apriltag.AprilTagFields;
import javafx.application.HostServices;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.prefs.Preferences;

public class AppStateManager {
    // Create a preferences and methods to save and load certain variables/settings
    private static final Preferences prefs = Preferences.userNodeForPackage(AppStateManager.class);

    public void saveState() {
        prefs.putBoolean("isDarkMode", isDarkMode());
        prefs.put("openAutoName", getOpenAutoName());
        prefs.put("robotRepoPath", getRobotRepoPath());
        prefs.put("fieldImagePath", getFieldImagePath());
        prefs.put("aprilTagField", getAprilTagField().name());
    }

    public void loadState() {
        setIsDarkMode(prefs.getBoolean("isDarkMode", isDarkMode()));
        setOpenAutoName(prefs.get("openAutoName", getOpenAutoName()));
        setRobotRepoPath(prefs.get("robotRepoPath", getRobotRepoPath()));
        setFieldImagePath(prefs.get("fieldImagePath", getFieldImagePath()));
        setAprilTagField(AprilTagFields.valueOf(prefs.get("aprilTagField", getAprilTagField().name())));
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
    private StackPane root;
    private final BooleanProperty isSaved = new SimpleBooleanProperty(true);
    private final ObjectProperty<Event> selectedEvent = new SimpleObjectProperty<>();

    private final RefreshableListProperty<Event> events = new RefreshableListProperty<>(FXCollections.observableArrayList());

    // Persistent Ones      MUST HAVE DEFAULT VALUE OR WILL CRASH APP UPON THE ATTEMPT TO SAVE IT INTO PREFERENCES
    private final BooleanProperty isDarkMode = new SimpleBooleanProperty(false);
    private final StringProperty openAutoName = new SimpleStringProperty("");
    private final StringProperty robotRepoPath = new SimpleStringProperty(System.getProperty("user.home"));
    private final StringProperty fieldImagePath = new SimpleStringProperty(System.getProperty("user.home"));
    private final ObjectProperty<AprilTagFields> aprilTagField = new SimpleObjectProperty<>(AprilTagFields.kDefaultField);

    // Getters and Setters
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

    public ObjectProperty<Event> selectedEventProperty() {
        return selectedEvent;
    }

    public Event getSelectedEvent() {
        return selectedEvent.get();
    }

    public void setRoot(StackPane root) {
        this.root = root;
    }

    public StackPane getRoot() {
        return root;
    }

    public Window getWindow() {
        return getRoot().getScene().getWindow();
    }

    public RefreshableListProperty<Event> eventsProperty() {
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

    public StringProperty openAutoNameProperty() {
        return openAutoName;
    }

    public String getOpenAutoName() {
        return openAutoName.get();
    }

    public void setOpenAutoName(String openAutoName) {
        this.openAutoName.set(openAutoName);
    }

    public StringProperty robotRepoPathProperty() {
        return robotRepoPath;
    }

    public String getRobotRepoPath() {
        return robotRepoPath.get();
    }

    public void setRobotRepoPath(String robotRepoPath) {
        this.robotRepoPath.set(robotRepoPath);
    }

    public StringProperty fieldImagePathProperty() {
        return fieldImagePath;
    }

    public String getFieldImagePath() {
        return fieldImagePath.get();
    }

    public void setFieldImagePath(String fieldImagePath) {
        this.fieldImagePath.set(fieldImagePath);
    }

    public ObjectProperty<AprilTagFields> aprilTagFieldProperty() {
        return aprilTagField;
    }

    public AprilTagFields getAprilTagField() {
        return aprilTagField.get();
    }

    public void setAprilTagField(AprilTagFields aprilTagField) {
        this.aprilTagField.set(aprilTagField);
    }
}
