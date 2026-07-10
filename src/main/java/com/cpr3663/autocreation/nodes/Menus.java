package com.cpr3663.autocreation.nodes;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.util.FileHelper;
import com.cpr3663.autocreation.util.PopUpHelper;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;

public class Menus {
    public static MenuBar getMenuBar() {
        // Create MenuBar
        MenuBar menuBar = new MenuBar();

        // Add Menus to MenuBar
        menuBar.getMenus().add(getFileMenu());
        menuBar.getMenus().add(getHelpMenu());

        return menuBar;
    }

    private static Menu getFileMenu() {
        // Create Menu Items
        MenuItem newFile = new MenuItem("_New");
        MenuItem open = new MenuItem("_Open");
        MenuItem save = new MenuItem("_Save");
        save.disableProperty().bind(AppStateManager.getInstance().isSavedProperty());
        MenuItem rename = new MenuItem("_Rename");
        MenuItem settings = new MenuItem("S_ettings");
        MenuItem exit = new MenuItem("Exit");

        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();

        // Setting Accelerators
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        rename.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        settings.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.SHORTCUT_DOWN));
        exit.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));

        // Creating Events
        newFile.setOnAction(e -> {
            if (PopUpHelper.checkForOverride()) return;
            try {
                FileHelper.create();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        open.setOnAction(e -> {
            if (PopUpHelper.checkForOverride()) return;
            PopUpHelper.selectAutoToOpen();
        });
        save.setOnAction(e -> FileHelper.save());
        rename.setOnAction(e -> FileHelper.rename());
        settings.setOnAction(e -> PopUpHelper.showSettings());
        exit.setOnAction(e -> Platform.exit());

        // Create menu and add items
        Menu menu = new Menu("_File");
        menu.getItems().addAll(newFile, open, save, rename, separator1, settings, separator2, exit);

        return menu;
    }

    private static Menu getHelpMenu() {
        // Create Menu Items
        MenuItem reportIssue = new MenuItem("_Report Issue");
        MenuItem openGitHub = new MenuItem("Open _GitHub");
        MenuItem about = new MenuItem("_About");

        // Creating Events
        reportIssue.setOnAction(e -> AppStateManager.getInstance().getHostServices().showDocument(Constants.Links.CREATE_GITHUB_ISSUE));
        openGitHub.setOnAction(e -> AppStateManager.getInstance().getHostServices().showDocument(Constants.Links.OPEN_GITHUB));

        // Create menu and add items
        Menu menu = new Menu("_Help");
        menu.getItems().addAll(reportIssue, openGitHub, about);

        return menu;
    }
}
