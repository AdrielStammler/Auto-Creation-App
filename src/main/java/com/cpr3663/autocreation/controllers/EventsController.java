package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.objects.DriveEvent;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.DragDropCell;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class EventsController {
    @FXML private ListView<Event> listView;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    @FXML
    public void initialize() {
        listView.itemsProperty().bind(AppStateManager.getInstance().eventsProperty());
        listView.setCellFactory(lv -> new DragDropCell());

        // Wait for the button to be attached to a scene
        addButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                setupAccelerator(newScene);
            }
        });

        deleteButton.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());

        AppStateManager.getInstance().eventsProperty().addListener((ListChangeListener<Event>) change -> {
            while (change.next()) {
                // Structural change or reorder detected
                if (change.wasAdded() || change.wasRemoved() || change.wasPermutated()) {
                    AppStateManager.getInstance().setIsSaved(false);
                }

                // Attach callbacks to new events
                if (change.wasAdded()) {
                    for (Event event : change.getAddedSubList()) {
                        event.setOnChangeCallback(() -> {
                            AppStateManager.getInstance().setIsSaved(false);
                            listView.refresh();
                        });
                    }
                }

                // Clean up callbacks from removed events to prevent potential memory leaks
                if (change.wasRemoved()) {
                    for (Event event : change.getRemoved()) event.setOnChangeCallback(null);
                }
            }
        });
    }

    private void setupAccelerator(Scene scene) {
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.ADD), () -> addButton.fire());
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.A, KeyCombination.SHIFT_DOWN), () -> addButton.fire());

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.DELETE), () -> deleteButton.fire());
    }

    @FXML
    protected void addEvent() {
        // TODO ask for event type
        AppStateManager.getInstance().addEvent(new DriveEvent());
    }

    @FXML
    protected void deleteEvent() {
        Event selectedEvent = listView.getSelectionModel().getSelectedItem();
        int index = listView.getSelectionModel().getSelectedIndex();
        AppStateManager.getInstance().getEvents().remove(selectedEvent);
        if (listView.getItems().size() <= index)
            listView.getSelectionModel().clearSelection();
        else
            listView.getSelectionModel().select(index);
    }
}
