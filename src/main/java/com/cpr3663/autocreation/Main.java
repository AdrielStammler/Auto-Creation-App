package com.cpr3663.autocreation;

import com.cpr3663.autocreation.util.Menus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> AppStateManager.getInstance().saveState()));

        this.stage = stage;
        AppStateManager.getInstance().setHostServices(getHostServices());

        MenuBar menus = Menus.getMenuBar();
        FXMLLoader fieldFxml = new FXMLLoader(Main.class.getResource("field-view.fxml"));
        FXMLLoader eventsFxml = new FXMLLoader(Main.class.getResource("events-view.fxml"));

        // Creating and defining the BorderPane
        BorderPane root = new BorderPane();
        root.setTop(menus);
        root.setCenter(fieldFxml.load());
        root.setLeft(eventsFxml.load());

        // Creating scene and setting stage properties
        Scene scene = new Scene(root, Constants.Stage.WIDTH, Constants.Stage.HEIGHT);
        stage.setTitle(Constants.Stage.TITLE);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        stage.requestFocus();

        setDarkMode(AppStateManager.getInstance().isDarkMode());
        AppStateManager.getInstance().isDarkModeProperty().addListener((observable, oldTheme, newTheme) -> setDarkMode(newTheme));
    }

    @Override
    public void stop() {
        AppStateManager.getInstance().saveState();
    }

    private void setDarkMode(boolean isDarkMode) {
        Scene scene = stage.getScene();
        scene.getStylesheets().clear();
        if (isDarkMode) {
            scene.getStylesheets().add(Constants.Paths.DARK_THEME);
        } else {
//                scene.getStylesheets().add(Constants.Paths.LIGHT_THEME);
        }
    }
}
