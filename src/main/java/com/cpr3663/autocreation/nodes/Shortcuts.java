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
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import static javafx.scene.input.KeyCode.*;


public class Shortcuts {
    public record Shortcut(String name, KeyCodeCombination shortcut) {
        public Shortcut(String name, KeyCode key, KeyCombination.Modifier... modifiers) {
            this(name, new KeyCodeCombination(key, modifiers));
        }
        public static Shortcut withSc(String name, KeyCode key) {
            return new Shortcut(name, key, KeyCombination.SHORTCUT_DOWN);
        }

        public static Shortcut withCtrl(String name, KeyCode key) {
            return new Shortcut(name, key, KeyCombination.CONTROL_DOWN);
        }
    }

    private static final Shortcut[] shortcuts = {
            // TODO make this the actual shortcuts
            Shortcut.withSc("Test1", A),
            Shortcut.withSc("Test2", B),
            new Shortcut("Test3", C),
    };
    private static final int columns = 2;
    private static final int rows = (int) Math.ceil(1.0 * shortcuts.length / columns);

    public static GridPane getShortcutsPage(Stage popupStage) {
        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(15);
        pane.setPadding(new Insets(30));

        int totalGridCols = columns * 3 - 1;
        final double percentWidth = 100.0 / totalGridCols;
        for (int i = 0; i < totalGridCols; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(percentWidth);
            if (i % 3 == 0) {
                col.setHalignment(HPos.RIGHT);
            } else if (i % 3 == 1) {
                col.setHalignment(HPos.LEFT);
            } else {
                col.setHalignment(HPos.CENTER);
            }
            pane.getColumnConstraints().add(col);
        }

        Label title = new Label("Application Shortcuts");
        title.setAlignment(Pos.CENTER);
        title.setFont(Font.font("System", FontWeight.BOLD, 20.0));
        title.setMinWidth(Region.USE_PREF_SIZE);
        pane.add(title, 0, 0, totalGridCols, 1);

        for (int c = 0; c < columns; c++) {
            final int rowI = c * rows;
            for (int r = 1; r <= rows; r++) {
                final int i = rowI + r - 1;
                if (i >= shortcuts.length) break;

                System.out.println("i = " + i);

                Label label1 = new Label(shortcuts[i].name());
                Label label2 = new Label(shortcuts[i].shortcut().getDisplayText());

                pane.add(label1, c * 3, r);
                pane.add(label2, c * 3 + 1, r);
            }
            if (c < columns - 1) {
                Separator separator = new Separator();
                separator.setOrientation(Orientation.VERTICAL);
                pane.add(separator, c * 3 + 2, 1, 1, rows);
            }
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
}
