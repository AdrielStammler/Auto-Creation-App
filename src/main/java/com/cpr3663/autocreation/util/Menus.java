package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
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
        menuBar.getMenus().add(getThemeMenu());
        menuBar.getMenus().add(getHelpMenu());

        return menuBar;
    }

    private static Menu getFileMenu() {
        // Create Menu Items
        MenuItem newFile = new MenuItem("_New");
        MenuItem open = new MenuItem("_Open");
        MenuItem save = new MenuItem("_Save");
        save.disableProperty().bind(AppStateManager.getInstance().isSavedProperty());
        MenuItem exit = new MenuItem("Exit");

        // Setting Accelerators
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        exit.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));

        // Creating Events
        newFile.setOnAction(e -> {
            if (!PopUpHelper.confirmOverride()) return;
            try {
                FileHelper.create(newFile.getParentPopup().getScene().getWindow());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        open.setOnAction(e -> {
            if (!PopUpHelper.confirmOverride()) return;
            PopUpHelper.selectAutoToOpen(open.getParentPopup().getScene().getWindow());
        });
        save.setOnAction(e -> FileHelper.save());

        exit.setOnAction(e -> Platform.exit());

        // Create menu and add items
        Menu menu = new Menu("_File");
        menu.getItems().add(newFile);
        menu.getItems().add(open);
        menu.getItems().add(save);
        menu.getItems().add(exit);

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
        menu.getItems().add(reportIssue);
        menu.getItems().add(openGitHub);
        menu.getItems().add(about);

        return menu;
    }

    private static Menu getThemeMenu() {
        // Create Menu Items
        MenuItem dark = new MenuItem("_Dark Theme");
        MenuItem light = new MenuItem("_Light Theme");

        // Creating Events
        dark.setOnAction(e -> AppStateManager.getInstance().setIsDarkMode(true));
        light.setOnAction(e -> AppStateManager.getInstance().setIsDarkMode(false));

        // Create menu and add items
        Menu menu = new Menu("_Theme");
        menu.getItems().add(dark);
        menu.getItems().add(light);

        return menu;
    }
}
