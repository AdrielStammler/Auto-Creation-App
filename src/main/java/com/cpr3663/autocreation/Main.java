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
    @Override
    public void start(Stage stage) throws IOException {
        AppStateManager.getInstance().setHostServices(getHostServices());

        FXMLLoader fieldFxml = new FXMLLoader(Main.class.getResource("field-view.fxml"));
        MenuBar menus = Menus.getMenuBar();

        // Creating and defining the BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(fieldFxml.load());
        root.setTop(menus);

        // Creating scene and setting stage properties
        Scene scene = new Scene(root, Constants.Stage.WIDTH, Constants.Stage.HEIGHT);
        stage.setTitle(Constants.Stage.TITLE);
        stage.setScene(scene);
        stage.show();
    }
}
