package com.cpr3663.autocreation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    private int timesClicked = 0;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        timesClicked++;
        welcomeText.setText("Welcome to JavaFX Application!\nNum: " + timesClicked);
    }
}
