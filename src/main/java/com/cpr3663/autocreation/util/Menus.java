package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

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
        MenuItem newFile = new MenuItem("New");
        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");

        // Setting Accelerators
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        exit.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));

        // Creating Events
        newFile.setOnAction(e -> {
            if (!PopUpHelper.confirmOverride()) return;
            FileHelper.create();
        });
        open.setOnAction(e -> {
            if (!PopUpHelper.confirmOverride()) return;
            FileHelper.open();
        });
        save.setOnAction(e -> FileHelper.save());

        exit.setOnAction(e -> Platform.exit());

        // Create menu and add items
        Menu menu = new Menu("File");
        menu.getItems().add(newFile);
        menu.getItems().add(open);
        menu.getItems().add(save);
        menu.getItems().add(exit);

        return menu;
    }

    private static Menu getHelpMenu() {
        // Create Menu Items
        MenuItem reportIssue = new MenuItem("Report Issue");
        MenuItem showShortcuts = new MenuItem("Show Shortcuts");
        MenuItem openGitHub = new MenuItem("Open GitHub");
        MenuItem about = new MenuItem("About");

        // Creating Events
        reportIssue.setOnAction(e -> AppStateManager.getInstance().getHostServices().showDocument(Constants.Websites.CREATE_GITHUB_ISSUE));
        openGitHub.setOnAction(e -> AppStateManager.getInstance().getHostServices().showDocument(Constants.Websites.OPEN_GITHUB));

        // Create menu and add items
        Menu menu = new Menu("Help");
        menu.getItems().add(reportIssue);
        menu.getItems().add(showShortcuts);
        menu.getItems().add(openGitHub);
        menu.getItems().add(about);

        return menu;
    }
}
