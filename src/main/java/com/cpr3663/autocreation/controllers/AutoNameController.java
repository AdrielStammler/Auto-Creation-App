package com.cpr3663.autocreation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AutoNameController {
    @FXML public Label label;
    @FXML private TextField inputField;
    private Stage stage;
    private String userInput = null;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setNew(boolean isNew) {
        label.setText(isNew ? "Enter a name for the new auto:" : "Choose a name for the auto:");
    }

    public String getUserInput() {
        return userInput;
    }

    @FXML
    public void handleSubmit() {
        userInput = inputField.getText();
        stage.close();
    }

    @FXML
    public void handleCancel() {
        userInput = null;
        stage.close();
    }
}
