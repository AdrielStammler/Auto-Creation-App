package com.cpr3663.autocreation.objects;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class DragDropCell extends ListCell<Event> {
    private static final DataFormat REORDER_MIME_TYPE = new DataFormat("application/x-task-reorder-index");

    public DragDropCell() {
        setOnDragDetected(event -> {
            if (getItem() == null) return;

            Dragboard db = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.put(REORDER_MIME_TYPE, getIndex());
            db.setContent(content);
            event.consume();
        });


        setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasContent(REORDER_MIME_TYPE)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });


        setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasContent(REORDER_MIME_TYPE)) {
                int draggedIndex = (Integer) db.getContent(REORDER_MIME_TYPE);
                int thisIndex = getIndex();

                // Get the list directly from the ListView (which updates your bound ListProperty)
                ObservableList<Event> items = getListView().getItems();

                Event draggedItem = items.remove(draggedIndex);

                if (thisIndex == -1 || thisIndex >= items.size()) {
                    items.add(draggedItem);
                } else {
                    items.add(thisIndex, draggedItem);
                }

                getListView().getSelectionModel().select(draggedItem);
                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }
            event.consume();
        });
    }

    @Override
    protected void updateItem(Event item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText((getIndex() + 1) + ") " + item);
        }
    }
}
