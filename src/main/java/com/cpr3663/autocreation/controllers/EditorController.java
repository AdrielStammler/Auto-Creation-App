package com.cpr3663.autocreation.controllers;

import javafx.scene.control.Button;

public class EditorController {
    public Button button;

    public void click() {
        button.setText(Math.random()+"");
    }
}
