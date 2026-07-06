package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;

public class FieldController {
    private int timesClicked = 0;
    @FXML private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        timesClicked++;
        welcomeText.setText("Welcome to JavaFX Application!\nNum: " + timesClicked);
        AppStateManager.getInstance().setIsSaved(false);
    }

    @FXML
    private void selectFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Folder");

        // (Optional) Set the initial directory
        directoryChooser.setInitialDirectory(new File(AppStateManager.getInstance().getRobotRepoPath()));

        Window stage = ((Node) event.getSource()).getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) AppStateManager.getInstance().setRobotRepoPath(selectedDirectory.getAbsolutePath());
    }

    @FXML
    private void openFolder() {
        File fullPath = new File(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_DEPLOY);
        AppStateManager.getInstance().getHostServices().showDocument(fullPath.toURI().toString());
    }
}
