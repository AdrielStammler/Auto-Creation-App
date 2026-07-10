package com.cpr3663.autocreation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class autoNameController {
    @FXML private TextField inputField;
    private Stage stage;
    private String userInput = null;

    public void setStage(Stage stage) {
        this.stage = stage;
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
