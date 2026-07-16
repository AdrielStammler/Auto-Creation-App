package com.cpr3663.autocreation;

import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.objects.RefreshableListProperty;
import edu.wpi.first.apriltag.AprilTagFields;
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
        private static final String ROBOT_SIZE = "robotSize";
        private static final String DISPLAY_UNIT = "displayUnits";
    }

    public void saveState() {
        prefs.putBoolean(Keys.DARK_MODE, isDarkMode());
        prefs.put(Keys.OPEN_AUTO, getOpenAutoName());
        prefs.put(Keys.ROBOT_REPO, getRobotRepoPath());
        prefs.put(Keys.TAG_FIELD, getAprilTagField().name());
        prefs.putDouble(Keys.ROBOT_SIZE, getRobotSize());
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
        setRobotSize(prefs.getDouble(Keys.ROBOT_SIZE, getRobotSize()));
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
    private final ObjectProperty<Event> selectedEvent = new SimpleObjectProperty<>();

    private final RefreshableListProperty<Event> events = new RefreshableListProperty<>(FXCollections.observableArrayList());

    // Persistent Ones      MUST HAVE DEFAULT VALUE OR WILL CRASH APP UPON THE ATTEMPT TO SAVE IT INTO PREFERENCES
    private final BooleanProperty isDarkMode = new SimpleBooleanProperty(false);
    private final StringProperty openAutoName = new SimpleStringProperty("");
    private final StringProperty robotRepoPath = new SimpleStringProperty(System.getProperty("user.home"));
    private final ObjectProperty<Image> fieldImage = new SimpleObjectProperty<>(null);
    private final ObjectProperty<AprilTagFields> aprilTagField = new SimpleObjectProperty<>(AprilTagFields.kDefaultField);
    private final DoubleProperty robotSize = new SimpleDoubleProperty(Units.Inches.toBaseUnits(27));
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

    public DoubleProperty robotSizeProperty() {
        return robotSize;
    }

    public double getRobotSize() {
        return robotSize.get();
    }

    public void setRobotSize(double robotSize) {
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
