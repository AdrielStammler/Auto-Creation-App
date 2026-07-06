package com.cpr3663.autocreation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewAutoController {
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
    public void handleSubmit(ActionEvent actionEvent) {
        userInput = inputField.getText();
        stage.close();
    }

    @FXML
    public void handleCancel(ActionEvent actionEvent) {
        userInput = null;
        stage.close();
    }
}
