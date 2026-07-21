package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.util.PopUpHelper;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.Units;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class SettingsController {
    @FXML private CheckBox darkModeBox;
    @FXML private TextField robotRepoLabel;
    @FXML private ComboBox<AprilTagFields> aprilTagDropdown;
    @FXML public ComboBox<DistanceUnit> unitsDropDown;
    @FXML private TextField robotSizeXText;
    @FXML private TextField robotSizeYText;
    private Stage stage;
    private Image fieldImage;

    @FXML
    private void initialize() {
        darkModeBox.setSelected(AppStateManager.getInstance().isDarkMode());
        robotRepoLabel.setText(AppStateManager.getInstance().getRobotRepoPath());
        fieldImage = AppStateManager.getInstance().getFieldImage();
        aprilTagDropdown.getItems().addAll(AprilTagFields.values());
        aprilTagDropdown.getSelectionModel().select(AppStateManager.getInstance().getAprilTagField());
        unitsDropDown.getItems().addAll(Units.Meters, Units.Feet, Units.Inches, Units.Millimeters, Units.Centimeters);
        unitsDropDown.getSelectionModel().select(AppStateManager.getInstance().getDisplayUnits());
        robotSizeXText.setText(Double.toString(AppStateManager.getInstance().getDisplayUnits().fromBaseUnits(AppStateManager.getInstance().getRobotSize().getX())));
        robotSizeXText.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*\\.?\\d*")) return change;
            return null;
        }));
        robotSizeYText.setText(Double.toString(AppStateManager.getInstance().getDisplayUnits().fromBaseUnits(AppStateManager.getInstance().getRobotSize().getY())));
        robotSizeYText.setTextFormatter(new TextFormatter<>(change -> {
            String text = change.getControlNewText();
            if (text.matches("\\d*\\.?\\d*")) return change;
            return null;
        }));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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
        stateManager.setIsSaved(false);
        stage.close();
    }
}
