package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.objects.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.OptionalInt;

public class EventTypeController {
    @FXML private Label label;
    @FXML private ListView<Event.Type> listView;
    private Stage stage;
    private Integer userInput;

    @FXML
    private void initialize() {
        listView.getItems().add(new Event.Type("Drive Event"));
        listView.getItems().addAll(AppStateManager.getInstance().getExtraTypes());

        listView.getSelectionModel().selectFirst();
        listView.setPrefHeight(listView.getItems().size() * 25 + 25);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public OptionalInt getUserInput() {
        return Optional.ofNullable(userInput).map(OptionalInt::of).orElse(OptionalInt.empty());
    }

    @FXML
    public void handleSubmit() {
        userInput = listView.getSelectionModel().getSelectedIndex() - 1;
        stage.close();
    }

    @FXML
    public void handleCancel() {
        userInput = null;
        stage.close();
    }
}
