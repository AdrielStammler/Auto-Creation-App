package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FieldController {
    private int timesClicked = 0;
    @FXML private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        timesClicked++;
        welcomeText.setText("Welcome to JavaFX Application!\nNum: " + timesClicked);
        AppStateManager.getInstance().setIsSaved(false);
    }
}
