package com.cpr3663.autocreation.nodes;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static javafx.scene.input.KeyCode.*;

public class Shortcuts {
    public record Shortcut(String name, KeyCodeCombination shortcut, String customCombo) {
        public Shortcut(String name, KeyCode key, KeyCombination.Modifier... modifiers) {
            this(name, new KeyCodeCombination(key, modifiers), "");
        }

        public static Shortcut withSc(String name, KeyCode key) {
            return new Shortcut(name, key, KeyCombination.SHORTCUT_DOWN);
        }

        public static Shortcut withShiftCtrl(String name, KeyCode key) {
            return new Shortcut(name, key, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        }

        public Shortcut(String name, KeyCodeCombination shortcut) {
            this(name, shortcut, "");
        }

        public Shortcut(String name, String customCombo) {
            this(name, null, customCombo);
        }

        public String getShortcutText() {
            if (shortcut != null)
                return shortcut.getDisplayText();
            return customCombo;
        }
    }

    private static final Shortcut[] shortcuts = {
            new Shortcut("Add Event", PLUS),
            new Shortcut("Delete Event", DELETE),
            new Shortcut("Select Previous Event", UP),
            new Shortcut("Select Next Event", DOWN),
            new Shortcut("Select/Move Event", "Left Mouse"),
            new Shortcut("Force Rotate Event", "Right Mouse"),
            new Shortcut("Force Move Event", "Shift + Left Mouse"),
            Shortcut.withSc("New Auto", N),
            Shortcut.withSc("Open Auto", O),
            Shortcut.withShiftCtrl("Delete Auto", DELETE),
            Shortcut.withSc("Settings", COMMA),
            Shortcut.withSc("Shortcuts", SLASH),
            Shortcut.withSc("Save Auto", S),
            Shortcut.withSc("Rename Auto", R),
            Shortcut.withSc("Duplicate Auto", D),
            new Shortcut("Open About", A, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN),
    };

    private static final int columns = 2;
    private static final int rows = (int) Math.ceil(1.0 * shortcuts.length / columns);

    public static GridPane getShortcutsPage(Stage popupStage) {
        GridPane pane = new GridPane();
        pane.setHgap(3);
        pane.setVgap(15);
        pane.setPadding(new Insets(30));

        int totalGridCols = columns * 4 - 1;
        for (int i = 0; i < totalGridCols; i++) {
            pane.getColumnConstraints().add(createCol(i));
        }

        Label title = new Label("Application Shortcuts");
        title.setAlignment(Pos.CENTER);
        title.setFont(Font.font("System", FontWeight.BOLD, 20.0));
        title.setMinWidth(Region.USE_PREF_SIZE);
        GridPane.setHalignment(title, HPos.CENTER);
        pane.add(title, 0, 0, totalGridCols, 1);

        for (int c = 0; c < columns; c++) {
            final int rowI = c * rows;
            for (int r = 1; r <= rows; r++) {
                final int i = rowI + r - 1;
                if (i >= shortcuts.length) break;

                Label label1 = new Label(shortcuts[i].name());
                Label label2 = new Label(shortcuts[i].getShortcutText());

                pane.add(label1, c * 4, r);
                pane.add(label2, c * 4 + 2, r);
            }
            if (c < columns - 1) {
                makeSeparator(pane, c * 4 + 3);
            }
            makeSeparator(pane, c * 4 + 1);
        }

        Button exit = new Button("Exit");
        exit.setAlignment(Pos.BOTTOM_RIGHT);
        exit.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                KeyCombination combo = new KeyCodeCombination(SLASH, KeyCombination.SHORTCUT_DOWN);
                newValue.getAccelerators().put(combo, exit::fire);
            }
        });
        exit.setOnAction(event -> popupStage.close());
        pane.add(exit, 0, shortcuts.length + 1, totalGridCols, 1);
        return pane;
    }

    private static ColumnConstraints createCol(int i) {
        ColumnConstraints col = new ColumnConstraints();
        if (i % 4 == 0) {
            col.setHalignment(HPos.RIGHT);
            col.setHgrow(Priority.ALWAYS);
        } else if (i % 4 == 1) {
            col.setHalignment(HPos.CENTER);
            col.setHgrow(Priority.NEVER);
        } else if (i % 4 == 2) {
            col.setHalignment(HPos.LEFT);
            col.setHgrow(Priority.ALWAYS);
        } else {
            col.setHalignment(HPos.CENTER);
            col.setHgrow(Priority.NEVER);
            col.setPrefWidth(50);
        }
        return col;
    }

    private static void makeSeparator(GridPane pane, int columnIndex) {
        Separator separator = new Separator(Orientation.VERTICAL);
        pane.add(separator, columnIndex, 1, 1, rows);
    }
}
