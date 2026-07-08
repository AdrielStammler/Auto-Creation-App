package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import edu.wpi.first.apriltag.AprilTagFields;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;

public class SettingsController {
    @FXML private CheckBox darkModeBox;
    @FXML private TextField robotRepoLabel;
    @FXML private TextField fieldImageLabel;
    @FXML private ComboBox<AprilTagFields> aprilTagDropdown;
    private Stage stage;

    @FXML
    private void initialize() {
        darkModeBox.setSelected(AppStateManager.getInstance().isDarkMode());
        robotRepoLabel.setText(AppStateManager.getInstance().getRobotRepoPath());
        fieldImageLabel.setText(AppStateManager.getInstance().getFieldImagePath());
        aprilTagDropdown.getItems().addAll(AprilTagFields.values());
        aprilTagDropdown.getSelectionModel().select(AppStateManager.getInstance().getAprilTagField());
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
    private void browseFieldImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an Image");

        fileChooser.setInitialDirectory(new File(fieldImageLabel.getText()).getParentFile());

        Window window = ((Node) event.getSource()).getScene().getWindow();
        File selectedImage = fileChooser.showOpenDialog(window);

        if (selectedImage != null) fieldImageLabel.setText(selectedImage.getAbsolutePath());
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
        stateManager.setFieldImagePath(fieldImageLabel.getText());
        stateManager.setAprilTagField(aprilTagDropdown.getSelectionModel().getSelectedItem());
        stage.close();
    }
}
