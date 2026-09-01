package com.cpr3663.autocreation.nodes;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.util.FileHelper;
import com.cpr3663.autocreation.util.MiscHelper;
import com.cpr3663.autocreation.util.PopUpHelper;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class TopBar {
    private final static String normalCss = "-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 11px; -fx-min-width: 35px; -fx-min-height: 28px; -fx-pref-width: 35px; -fx-pref-height: 28px;";
    private final static String selectedCss = "-fx-background-color: #353535; -fx-cursor: hand; -fx-font-size: 11px; -fx-min-width: 35px; -fx-min-height: 28px; -fx-pref-width: 35px; -fx-pref-height: 28px;";

    private static double xOffset = 0;
    private static double yOffset = 0;

    public static Node getTopBar(Stage mainStage) {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(4, 8, 4, 12));

        Image icon = new Image(Objects.requireNonNull(PopUpHelper.class.getResource(Constants.Paths.APP_ICON)).toExternalForm());
        ImageView iconView = new ImageView(icon);
        iconView.setFitHeight(25);
        iconView.setPreserveRatio(true);
        iconView.setStyle("-fx-background-color: transparent;");

        Label openAuto = new Label();
        openAuto.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        openAuto.textProperty().bind(Bindings.createStringBinding(() -> {
                    String val = AppStateManager.getInstance().getOpenAutoName();
                    return (val == null || val.isBlank()) ? "No Open Auto" : val;
                }, AppStateManager.getInstance().openAutoNameProperty())
        );

        Button min = createWindowButton("—", e -> mainStage.setIconified(true));
        Button max = createWindowButton("🗖", e -> mainStage.setMaximized(!mainStage.isMaximized()));
        mainStage.maximizedProperty().addListener((observable, oldValue, newValue) -> max.setText(newValue ? " \uD83D\uDDD7" : "🗖"));
        Button close = createWindowButton("✕", e -> MiscHelper.closeRequest());
        close.setOnMouseEntered(e -> close.setStyle(selectedCss + "-fx-background-color: #e81123;"));
        close.setOnMouseExited(e -> close.setStyle(normalCss));

        topBar.getChildren().addAll(iconView, getMenuBar(), getSpacer(), openAuto, getSpacer(), min, max, close);

        topBar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();

            if (event.getClickCount() == 2) mainStage.setMaximized(!mainStage.isMaximized());
        });

        topBar.setOnMouseDragged((MouseEvent event) -> {
            mainStage.setX(event.getScreenX() - xOffset);
            mainStage.setY(event.getScreenY() - yOffset);
        });

        Separator separator = new Separator();
        separator.setHalignment(HPos.CENTER);
        separator.setValignment(VPos.CENTER);
        VBox.setVgrow(separator, Priority.NEVER);

        return new VBox(topBar, separator);
    }

    private static Button createWindowButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button(text);
        btn.setOnAction(action);
        btn.setStyle(normalCss);
        btn.setOnMouseEntered(e -> btn.setStyle(selectedCss));
        btn.setOnMouseExited(e -> btn.setStyle(normalCss));
        return btn;
    }

    private static Region getSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private static MenuBar getMenuBar() {
        // Create MenuBar
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: transparent;");

        // Add Menus to MenuBar
        menuBar.getMenus().addAll(getFileMenu(), getAppMenu(), getAutoMenu(), getHelpMenu());

        return menuBar;
    }

    private static Menu getFileMenu() {
        // Create Menu Items
        MenuItem newFile = new MenuItem("_New");
        MenuItem open = new MenuItem("_Open");
        MenuItem delete = new MenuItem("_Delete");
        MenuItem exit = new MenuItem("Exit");

        SeparatorMenuItem separator = new SeparatorMenuItem();

        // Setting Accelerators
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        delete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));

        // Creating Events
        newFile.setOnAction(e -> {
            if (PopUpHelper.checkForSaving()) return;
            try {
                FileHelper.create();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        open.setOnAction(e -> {
            if (PopUpHelper.checkForSaving()) return;
            PopUpHelper.selectAutoToOpen();
        });
        delete.setOnAction(e -> FileHelper.delete());
        exit.setOnAction(e -> MiscHelper.closeRequest());

        // Create menu and add items
        Menu menu = new Menu("_File");
        menu.getItems().addAll(newFile, open, delete, separator, exit);

        return menu;
    }

    private static Menu getAppMenu() {
        // Create Menu Items
        MenuItem settings = new MenuItem("_Settings");
        MenuItem shortcuts = new MenuItem("Short_cuts");

        // Setting Accelerators
        settings.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.SHORTCUT_DOWN));
        shortcuts.setAccelerator(new KeyCodeCombination(KeyCode.SLASH, KeyCombination.SHORTCUT_DOWN));

        // Creating Events
        settings.setOnAction(e -> PopUpHelper.showSettings());
        shortcuts.setOnAction(e -> PopUpHelper.showShortcuts());

        // Create menu and add items
        Menu menu = new Menu("A_pp");
        menu.getItems().addAll(settings, shortcuts);

        return menu;

    }

    private static Menu getAutoMenu() {
        // Create Menu Items
        MenuItem save = new MenuItem("_Save");
        save.disableProperty().bind(AppStateManager.getInstance().isSavedProperty());
        MenuItem rename = new MenuItem("_Rename");
        MenuItem duplicate = new MenuItem("_Duplicate");
        MenuItem openDir = new MenuItem("Open Auto _Directory");
        MenuItem openFile = new MenuItem("Open Auto Fi_le");

        SeparatorMenuItem separator = new SeparatorMenuItem();

        // Setting Accelerators
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        rename.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        duplicate.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN));

        // Creating Events
        save.setOnAction(e -> FileHelper.save());
        rename.setOnAction(e -> FileHelper.rename());
        duplicate.setOnAction(e -> FileHelper.duplicate());
        openDir.setOnAction(e -> MiscHelper.openFile(Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER)));
        openFile.setOnAction(e -> MiscHelper.openFile(Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, AppStateManager.getInstance().getOpenAutoName() + Constants.FILE_SUFFIX)));

        // Create menu and add items
        Menu menu = new Menu("_Auto");
        menu.getItems().addAll(save, rename, duplicate, separator, openDir, openFile);

        return menu;
    }

    private static Menu getHelpMenu() {
        // Create Menu Items
        MenuItem reportIssue = new MenuItem("_Report Issue");
        MenuItem openGitHub = new MenuItem("Open _GitHub");
        MenuItem about = new MenuItem("A_bout");

        // Setting Accelerators
        about.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));

        // Creating Events
        reportIssue.setOnAction(e -> AppStateManager.getInstance().getHostServices().showDocument(Constants.Links.CREATE_ISSUE));
        openGitHub.setOnAction(e -> AppStateManager.getInstance().getHostServices().showDocument(Constants.Links.GITHUB));
        about.setOnAction(e -> PopUpHelper.showAbout());

        // Create menu and add items
        Menu menu = new Menu("_Help");
        menu.getItems().addAll(reportIssue, openGitHub, about);

        return menu;
    }
}
