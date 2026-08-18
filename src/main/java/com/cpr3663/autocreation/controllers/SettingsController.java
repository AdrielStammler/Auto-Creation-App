package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.MiscHelper;
import com.cpr3663.autocreation.util.PopUpHelper;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.Units;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SettingsController {
    @FXML private CheckBox darkModeBox;
    @FXML private TextField robotRepoLabel;
    @FXML private ComboBox<AprilTagFields> aprilTagDropdown;
    @FXML public ComboBox<DistanceUnit> unitsDropDown;
    @FXML private TextField robotSizeXText;
    @FXML private TextField robotSizeYText;
    @FXML private TextField fieldScaleText;
    @FXML private ListView<String> extraTypesNameList;
    @FXML private ListView<String> extraTypesParamList;
    private Stage stage;
    private Image fieldImage;
    private List<Event.Type> extraTypes;
    private int selectedIndex = -1;

    @FXML
    private void initialize() {
        extraTypes = new ArrayList<>(AppStateManager.getInstance().getExtraTypes());
        darkModeBox.setSelected(AppStateManager.getInstance().isDarkMode());
        robotRepoLabel.setText(AppStateManager.getInstance().getRobotRepoPath());
        fieldImage = AppStateManager.getInstance().getFieldImage();
        aprilTagDropdown.getItems().addAll(AprilTagFields.values());
        aprilTagDropdown.getSelectionModel().select(AppStateManager.getInstance().getAprilTagField());
        unitsDropDown.getItems().addAll(Units.Meters, Units.Feet, Units.Inches, Units.Millimeters, Units.Centimeters);
        unitsDropDown.getSelectionModel().select(AppStateManager.getInstance().getDisplayUnits());
        robotSizeXText.setText(Double.toString(AppStateManager.getInstance().getDisplayUnits().fromBaseUnits(AppStateManager.getInstance().getRobotSize().getX())));
        robotSizeXText.setTextFormatter(MiscHelper.doubleFormater());
        robotSizeYText.setText(Double.toString(AppStateManager.getInstance().getDisplayUnits().fromBaseUnits(AppStateManager.getInstance().getRobotSize().getY())));
        robotSizeYText.setTextFormatter(MiscHelper.doubleFormater());
        fieldScaleText.setText(Double.toString(AppStateManager.getInstance().getFieldScale()));
        fieldScaleText.setTextFormatter(MiscHelper.doubleFormater());
        makeEditable(extraTypesNameList);
        makeEditable(extraTypesParamList);

        extraTypesNameList.getItems().addAll(extraTypes.stream().map(Event.Type::name).toList());
        extraTypesNameList.getItems().addListener((ListChangeListener<String>) change -> {
            List<Event.Type> oldTypes = List.copyOf(extraTypes);
            extraTypes.clear();
            extraTypes.addAll(extraTypesNameList.getItems().stream()
                    .map(name -> oldTypes.stream()
                            .filter(t -> t.name().equals(name))
                            .findFirst()
                            .map(existing -> new Event.Type(name, existing.parameters()))
                            .orElse(new Event.Type(name)))
                    .toList());
        });

        extraTypesNameList.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            selectedIndex = newIndex.intValue();
            if (oldIndex.intValue() == -1) return;

            saveParams(oldIndex.intValue());
            
            extraTypesParamList.getItems().clear();
            if (newIndex.intValue() >= 0)
                extraTypesParamList.getItems().addAll(extraTypes.get(newIndex.intValue()).parameters());
        });
    }

    private void saveParams() {
        if (selectedIndex == -1) return;
        saveParams(selectedIndex);
    }

    private void saveParams(int i) {
        Event.Type type = extraTypes.get(i);
        extraTypes.set(i, new Event.Type(type.name(), extraTypesParamList.getItems()));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void makeEditable(ListView<String> listView) {
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnEditCommit(event -> {
            int index = event.getIndex();
            String newValue = event.getNewValue();
            listView.getItems().set(index, newValue);
            listView.getSelectionModel().select(index);
        });
    }

    @FXML
    private void browseRobotRepo(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Folder");

        directoryChooser.setInitialDirectory(new File(robotRepoLabel.getText()));

        Window window = ((Node) event.getSource()).getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(window);

        if (selectedDirectory != null) robotRepoLabel.setText(selectedDirectory.getAbsolutePath());
    }

    @FXML
    private void uploadFieldImage(ActionEvent e) {
        Image image = PopUpHelper.getImage(stage);
        if  (image == null || image.isError()) return;
        fieldImage = image;
    }

    @FXML
    public void addName(ActionEvent actionEvent) {
        extraTypesNameList.getItems().add("New Type");
        extraTypesNameList.getSelectionModel().selectLast();
    }

    @FXML
    public void deleteName(ActionEvent actionEvent) {
        extraTypesNameList.getItems().remove(extraTypesNameList.getSelectionModel().getSelectedItem());
        extraTypesNameList.getSelectionModel().selectFirst();
    }

    @FXML
    public void addParam(ActionEvent actionEvent) {
        extraTypesParamList.getItems().add("New Param");
        extraTypesParamList.getSelectionModel().selectLast();
    }

    @FXML
    public void deleteParam(ActionEvent actionEvent) {
        extraTypesParamList.getItems().remove(extraTypesParamList.getSelectionModel().getSelectedItem());
        extraTypesParamList.getSelectionModel().selectFirst();
    }

    @FXML
    private void cancel() {
        stage.close();
    }

    @FXML
    private void save() {
        AppStateManager stateManager = AppStateManager.getInstance();
        stateManager.setIsDarkMode(darkModeBox.isSelected());
        stateManager.setRobotRepoPath(robotRepoLabel.getText());
        stateManager.setFieldImage(fieldImage);
        stateManager.setAprilTagField(aprilTagDropdown.getSelectionModel().getSelectedItem());
        stateManager.setDisplayUnits(unitsDropDown.getSelectionModel().getSelectedItem());
        stateManager.setRobotSize(stateManager.getDisplayUnits().toBaseUnits(Double.parseDouble(robotSizeXText.getText())),
                stateManager.getDisplayUnits().toBaseUnits(Double.parseDouble(robotSizeYText.getText())));
        stateManager.setFieldScale(Double.parseDouble(fieldScaleText.getText()));
        saveParams();
        extraTypes.sort(Comparator.comparing(Event.Type::name));
        stateManager.setExtraTypes(extraTypes.stream().map(type -> new Event.Type(type.name(), type.parameters())).toArray(Event.Type[]::new));

        stateManager.eventsProperty().forceRefresh();
        stateManager.saveState();
        stage.close();
    }
}
