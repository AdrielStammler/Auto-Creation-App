package com.cpr3663.autocreation;

import com.cpr3663.autocreation.nodes.Field;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.FileHelper;
import com.cpr3663.autocreation.nodes.Menus;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> AppStateManager.getInstance().saveState()));

        AppStateManager.getInstance().setHostServices(getHostServices());

        MenuBar menus = Menus.getMenuBar();
        FXMLLoader eventsFxml = new FXMLLoader(Main.class.getResource("events-view.fxml"));
        Pane field = Field.getFieldPane();

        // Creating and defining the BorderPane
        BorderPane root = new BorderPane();
        root.setTop(menus);
        root.setCenter(field);
        root.setLeft(eventsFxml.load());

        // Creating scene and setting stage properties
        Scene scene = new Scene(root, Constants.Stage.WIDTH, Constants.Stage.HEIGHT);
        stage.setTitle(Constants.Stage.TITLE);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        stage.requestFocus();

        setDarkMode(stage, AppStateManager.getInstance().isDarkMode());
        AppStateManager.getInstance().isDarkModeProperty().addListener((observable, oldTheme, newTheme) -> setDarkMode(stage, newTheme));
        AppStateManager.getInstance().openAutoNameProperty().addListener((observable, oldName, newName) -> FileHelper.open(newName));
        AppStateManager.getInstance().eventsProperty().addListener((ListChangeListener<Event>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasPermutated())
                    AppStateManager.getInstance().setIsSaved(false);
                if (change.wasAdded()) {
                    for (Event event : change.getAddedSubList())
                        event.setOnChangeCallback(() -> {
                            AppStateManager.getInstance().setIsSaved(false);
                            AppStateManager.getInstance().eventsProperty().forceRefresh();
                        });
                }
                if (change.wasRemoved()) for (Event event : change.getRemoved()) event.setOnChangeCallback(null);
            }
        });
        AppStateManager.getInstance().eventsProperty().addListener((ListChangeListener<Event>) change -> root.setCenter(Field.getFieldPane()));
    }

    @Override
    public void stop() {
        AppStateManager.getInstance().saveState();
    }

    public static void setDarkMode(Stage stage, boolean isDarkMode) {
        Scene scene = stage.getScene();
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(Constants.Paths.DARK_THEME);
        } else {
//                scene.getStylesheets().add(Constants.Paths.LIGHT_THEME);
        }
    }
}
