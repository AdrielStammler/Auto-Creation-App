package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.objects.DriveEvent;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.objects.DragDropCell;
import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;

public class EventsController {
    @FXML private ListView<Event> listView;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    @FXML
    public void initialize() {
        listView.itemsProperty().bind(AppStateManager.getInstance().eventsProperty());
        listView.setCellFactory(lv -> new DragDropCell());
        customBindBidirectional();

        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> AppStateManager.getInstance().setEventsEditing());

        addButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                setupAccelerator(newScene);
            }
        });
        deleteButton.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
    }

    private void setupAccelerator(Scene scene) {
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ADD), () -> addButton.fire());
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.A, KeyCombination.SHIFT_DOWN), () -> addButton.fire());

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.DELETE), () -> deleteButton.fire());
    }

    private void customBindBidirectional() {
        // TODO figure out why this fires 4 times upon selecting position on screen AND ASK JACOB
        IntegerProperty eventProperty = AppStateManager.getInstance().selectedIndexProperty();
        listView.getSelectionModel().selectedIndexProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("Selection changed (ListView): " + oldSelection.intValue() + " -> " + newSelection.intValue());
            if (newSelection.intValue() == -1 && !AppStateManager.getInstance().getEvents().isEmpty()) return;
            eventProperty.set(newSelection.intValue());
        });

        eventProperty.addListener((obs, oldVal, newVal) -> {
            System.out.println("Selection changed (AppStateManager): " + oldVal.intValue() + " -> " + newVal.intValue());
            if (newVal.intValue() == -1)
                listView.getSelectionModel().clearSelection();
            else
                listView.getSelectionModel().select(newVal.intValue());
        });
    }

    @FXML
    private void addEvent() {
        AppStateManager.getInstance().setEventsEditing();
        Event event = new DriveEvent();
        // TODO ask for event type
        AppStateManager.getInstance().addEvent(event);
        AppStateManager.getInstance().setSelectedIndex(AppStateManager.getInstance().getEvents().size() - 1);
    }

    @FXML
    private void deleteEvent() {
        AppStateManager.getInstance().setEventsEditing();
        Event selectedEvent = listView.getSelectionModel().getSelectedItem();
        int index = listView.getSelectionModel().getSelectedIndex();
        AppStateManager.getInstance().getEvents().remove(selectedEvent);
        if (index == listView.getItems().size())
            index--;
        AppStateManager.getInstance().setSelectedIndex(index);
    }
}
