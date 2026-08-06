package com.cpr3663.autocreation.nodes;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.util.FileHelper;
import com.cpr3663.autocreation.util.MiscHelper;
import com.cpr3663.autocreation.util.PopUpHelper;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import java.io.IOException;

public class Menus {
    // TODO Make an entire custom top bar rather than just a menu bar
    public static MenuBar getMenuBar() {
        // Create MenuBar
        MenuBar menuBar = new MenuBar();

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
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
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
        MenuItem settings = new MenuItem("S_ettings");
        // TODO add a shortcuts page/popup

        // Setting Accelerators
        settings.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, KeyCombination.SHORTCUT_DOWN));

        // Creating Events
        settings.setOnAction(e -> PopUpHelper.showSettings());

        // Create menu and add items
        Menu menu = new Menu("_App");
        menu.getItems().addAll(settings);

        return menu;

    }

    private static Menu getAutoMenu() {
        // Create Menu Items
        MenuItem save = new MenuItem("_Save");
        save.disableProperty().bind(AppStateManager.getInstance().isSavedProperty());
        MenuItem rename = new MenuItem("_Rename");
        MenuItem duplicate = new MenuItem("_Duplicate");

        // Setting Accelerators
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        rename.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        duplicate.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));

        // Creating Events
        save.setOnAction(e -> FileHelper.save());
        rename.setOnAction(e -> FileHelper.rename());
        duplicate.setOnAction(e -> FileHelper.duplicate());

        // Create menu and add items
        Menu menu = new Menu("_Auto");
        menu.getItems().addAll(save, rename, duplicate);

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
