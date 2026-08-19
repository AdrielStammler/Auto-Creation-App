package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.objects.DriveEvent;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.MiscHelper;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;

import java.util.Arrays;
import java.util.Objects;

public class EditorController {
    @FXML private CheckBox afterPrevCheck;
    @FXML private CheckBox delayCheck;
    @FXML private ChoiceBox<Event.DelayTypes> delayTypeChoice;
    @FXML private TextField delayAmountText;

    @FXML private GridPane paramGridPane;

    private Event event;

    private Event.DelayTypes prevDelayType;

    @FXML
    private void initialize() {
        event = AppStateManager.getInstance().getSelectedEvent();
        if (event == null) {
            afterPrevCheck.setDisable(true);
            delayCheck.setDisable(true);
            delayTypeChoice.setDisable(true);
            delayAmountText.setDisable(true);
            return;
        }

        afterPrevCheck.selectedProperty().bindBidirectional(event.afterPrevProperty());
        delayCheck.setSelected(event.getDelayType().equals(Event.DelayTypes.NONE));
        delayCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                delayTypeChoice.setValue(Objects.requireNonNullElse(prevDelayType, Event.DelayTypes.TIME));
            } else {
                prevDelayType = delayTypeChoice.getValue();
                delayTypeChoice.setValue(Event.DelayTypes.NONE);
            }
        });

        delayTypeChoice.getItems().addAll(Arrays.copyOfRange(Event.DelayTypes.values(), 1, Event.DelayTypes.values().length));
        delayTypeChoice.valueProperty().bindBidirectional(event.delayTypeProperty());
        delayTypeChoice.selectionModelProperty().addListener((obs, old, newV) -> {
            Event.DelayTypes type = newV.getSelectedItem();
            if (type.equals(Event.DelayTypes.TIME))
                delayAmountText.setTextFormatter(MiscHelper.intFormater());
            else
                delayAmountText.setTextFormatter(MiscHelper.doubleFormater());
        });
        Bindings.bindBidirectional(delayAmountText.textProperty(), event.delayProperty(), new NumberStringConverter());

        delayTypeChoice.disableProperty().bind(delayCheck.selectedProperty().not());
        delayAmountText.disableProperty().bind(delayCheck.selectedProperty().not());

        if (event instanceof DriveEvent)
            setupDriveParams();
        else
            setupCustomParams();
    }

    private void setupDriveParams() {
        DriveEvent driveEvent = (DriveEvent) event;
        // TODO implement maybe with fxml
    }

    private void setupCustomParams() {
        Event.Type type = event.getType();
        if (type == null) throw new RuntimeException("ERROR: Event type is null for a non-DriveEvent");

        String[] params = type.parameters();

        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            Label label = new Label(param);
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional(event.parameterProperty(i));

            paramGridPane.add(label, 0, i);
            paramGridPane.add(textField, 1, i);
        }
    }
}
