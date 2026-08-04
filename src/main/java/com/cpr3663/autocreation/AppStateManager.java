package com.cpr3663.autocreation;

import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.objects.RefreshableListProperty;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.Units;
import javafx.application.HostServices;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class AppStateManager {
    // Create a preferences and methods to save and load certain variables/settings
    private static final Preferences prefs = Preferences.userNodeForPackage(AppStateManager.class);

    private static final class Keys {
        private static final String DARK_MODE = "isDarkMode";
        private static final String OPEN_AUTO = "openAutoName";
        private static final String ROBOT_REPO = "robotRepoPath";
        private static final String TAG_FIELD = "aprilTagField";
        private static final String ROBOT_SIZE_X = "robotSizeX";
        private static final String ROBOT_SIZE_Y = "robotSizeY";
        private static final String DISPLAY_UNIT = "displayUnits";
    }

    public void saveState() {
        prefs.putBoolean(Keys.DARK_MODE, isDarkMode());
        prefs.put(Keys.OPEN_AUTO, getOpenAutoName());
        prefs.put(Keys.ROBOT_REPO, getRobotRepoPath());
        prefs.put(Keys.TAG_FIELD, getAprilTagField().name());
        prefs.putDouble(Keys.ROBOT_SIZE_X, getRobotSize().getX());
        prefs.putDouble(Keys.ROBOT_SIZE_Y, getRobotSize().getY());
        prefs.put(Keys.DISPLAY_UNIT, getDisplayUnits().symbol());
        File file = new File(Constants.Paths.FIELD_IMAGE);
        file.getParentFile().mkdirs();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(getFieldImage(), null), "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadState() {
        setIsDarkMode(prefs.getBoolean(Keys.DARK_MODE, isDarkMode()));
        setOpenAutoName(prefs.get(Keys.OPEN_AUTO, getOpenAutoName()));
        setRobotRepoPath(prefs.get(Keys.ROBOT_REPO, getRobotRepoPath()));
        setAprilTagField(AprilTagFields.valueOf(prefs.get(Keys.TAG_FIELD, getAprilTagField().name())));
        setRobotSize(prefs.getDouble(Keys.ROBOT_SIZE_X, getRobotSize().getX()), prefs.getDouble(Keys.ROBOT_SIZE_Y, getRobotSize().getY()));
        setDisplayUnits(switch(prefs.get(Keys.DISPLAY_UNIT, getDisplayUnits().symbol())) {
            case "cm" -> Units.Centimeters;
            case "in" -> Units.Inches;
            case "ft" -> Units.Feet;
            case "mm" -> Units.Millimeters;
            default -> Units.Meters;
        });
        File file = new File(Constants.Paths.FIELD_IMAGE);
        if (file.exists()) setFieldImage(new Image(file.toURI().toString()));
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
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(-1);
    private final ObjectProperty<Main.Sections> currentEditor = new SimpleObjectProperty<>(Main.Sections.EVENTS);

    private final RefreshableListProperty<Event> events = new RefreshableListProperty<>(FXCollections.observableArrayList());

    // Persistent Ones      MUST HAVE DEFAULT VALUE OR WILL CRASH APP UPON THE ATTEMPT TO SAVE IT INTO PREFERENCES
    private final BooleanProperty isDarkMode = new SimpleBooleanProperty(false);
    private final StringProperty openAutoName = new SimpleStringProperty("");
    private final StringProperty robotRepoPath = new SimpleStringProperty(System.getProperty("user.home"));
    private final ObjectProperty<Image> fieldImage = new SimpleObjectProperty<>(null);
    private final ObjectProperty<AprilTagFields> aprilTagField = new SimpleObjectProperty<>(AprilTagFields.kDefaultField);
    private final ObjectProperty<Translation2d> robotSize = new SimpleObjectProperty<>(new Translation2d(1, 1));
    private final ObjectProperty<DistanceUnit> displayUnits = new SimpleObjectProperty<>(Units.Meters);

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

    public IntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex.set(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex.get();
    }

    public void setSelectedEvent(Event event) {
        setSelectedIndex(events.indexOf(event));
    }

    public Event getSelectedEvent() {
        if (getSelectedIndex() == -1) return null;
        return events.get(getSelectedIndex());
    }

    public ObjectProperty<Main.Sections> currentEditorProperty() {
        return currentEditor;
    }

    public boolean isNotFieldEditing() {
        return currentEditor.get() != Main.Sections.FIELD;
    }

    public boolean isNotEditorEditing() {
        return currentEditor.get() != Main.Sections.EDITOR;
    }

    public void setFieldEditing() {
        this.currentEditor.set(Main.Sections.FIELD);
    }

    public void setEditorEditing() {
        this.currentEditor.set(Main.Sections.EDITOR);
    }

    public void setEventsEditing() {
        this.currentEditor.set(Main.Sections.EVENTS);
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

    public void setEvents(ObservableList<Event> events) {
        this.events.set(events);
    }

    public void addEvent(Event event) {
        this.events.add(event);
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

    public ObjectProperty<Image> fieldImageProperty() {
        return fieldImage;
    }

    public Image getFieldImage() {
        return fieldImage.get();
    }

    public void setFieldImage(Image fieldImage) {
        this.fieldImage.set(fieldImage);
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

    public ObjectProperty<Translation2d> robotSizeProperty() {
        return robotSize;
    }

    public Translation2d getRobotSize() {
        return robotSize.get();
    }

    public void setRobotSize(double x, double y) {
        setRobotSize(new Translation2d(x, y));
    }

    public void setRobotSize(Translation2d robotSize) {
        this.robotSize.set(robotSize);
    }

    public ObjectProperty<DistanceUnit> displayUnitsProperty() {
        return displayUnits;
    }

    public DistanceUnit getDisplayUnits() {
        return displayUnits.get();
    }

    public void setDisplayUnits(DistanceUnit displayUnits) {
        this.displayUnits.set(displayUnits);
    }
}
