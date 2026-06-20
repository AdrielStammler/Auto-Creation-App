package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

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
        MenuItem m1 = new MenuItem("New");
        MenuItem m2 = new MenuItem("Open");
        MenuItem m3 = new MenuItem("Exit");

        // Creating Events
        m3.setOnAction(e -> Platform.exit());

        // Create menu and add items
        Menu menu = new Menu("File");
        menu.getItems().add(m1);
        menu.getItems().add(m2);
        menu.getItems().add(m3);

        return menu;
    }

    private static Menu getHelpMenu() {
        // Create Menu Items
        MenuItem m1 = new MenuItem("Report Issue");
        MenuItem m2 = new MenuItem("Show Shortcuts");
        MenuItem m3 = new MenuItem("Go To GitHub");
        MenuItem m4 = new MenuItem("About");

        // Creating Events
        m3.setOnAction(e -> AppStateManager.getInstance().getHostServices().showDocument("https://github.com/AdrielStammler/Auto-Creation-App"));

        // Create menu and add items
        Menu menu = new Menu("Help");
        menu.getItems().add(m1);
        menu.getItems().add(m2);
        menu.getItems().add(m3);
        menu.getItems().add(m4);

        return menu;
    }
}
