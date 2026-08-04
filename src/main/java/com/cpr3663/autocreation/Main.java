package com.cpr3663.autocreation;

import com.cpr3663.autocreation.nodes.Field;
import com.cpr3663.autocreation.nodes.Menus;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.FileHelper;
import com.cpr3663.autocreation.util.MiscHelper;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        AppStateManager.getInstance().setHostServices(getHostServices());

        MenuBar menus = Menus.getMenuBar();
        FXMLLoader eventsFxml = new FXMLLoader(Main.class.getResource("events-view.fxml"));
        FXMLLoader editorFxml = new FXMLLoader(Main.class.getResource("editor-view.fxml"));
        Pane field = Field.getFieldPane();

        // Creating and defining the BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menus);
        borderPane.setCenter(field);
        borderPane.setLeft(eventsFxml.load());
        borderPane.setRight(editorFxml.load());

        Rectangle overlay = new Rectangle();
        overlay.widthProperty().bind(borderPane.widthProperty());
        overlay.heightProperty().bind(borderPane.heightProperty());
        overlay.setFill(Color.BLACK);
        overlay.setMouseTransparent(true);
        overlay.setOpacity(0);

        StackPane root = new StackPane(borderPane, overlay);
        AppStateManager.getInstance().setRoot(root);

        // Creating scene and setting stage properties
        Scene scene = new Scene(root, Constants.Stage.WIDTH, Constants.Stage.HEIGHT);
        stage.setTitle(Constants.Stage.TITLE);
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        stage.requestFocus();
        MiscHelper.setDarkMode(stage);

        stage.setOnCloseRequest(e -> {
            e.consume();
            MiscHelper.closeRequest();
        });

        AppStateManager.getInstance().isDarkModeProperty().addListener(run(() -> MiscHelper.setDarkMode(stage)));
        AppStateManager.getInstance().openAutoNameProperty().addListener(run(FileHelper::open));
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
        AppStateManager.getInstance().eventsProperty().addListener((ListChangeListener<Event>) change -> {
            if (AppStateManager.getInstance().isNotFieldEditing())
                refreshField(borderPane).run();
        });
        AppStateManager.getInstance().selectedIndexProperty().addListener(run(() -> {
            if (AppStateManager.getInstance().isNotFieldEditing()) Field.Helper.updateSelection();
            else if (AppStateManager.getInstance().isNotEditorEditing()) {
                FXMLLoader editorFxml2 = new FXMLLoader(Main.class.getResource("editor-view.fxml"));
                try {
                    borderPane.setRight(editorFxml2.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        AppStateManager.getInstance().currentEditorProperty().addListener((observable, oldValue, newValue) -> System.out.println("Current Editor: " + newValue));
        FileHelper.open();
    }

    private static Runnable refreshField(BorderPane borderPane) {
        return () -> {
            System.out.println("Refreshing field | " + AppStateManager.getInstance().isNotFieldEditing() + " | " + Math.round(Math.random() * 100));
            borderPane.setCenter(Field.getFieldPane());
        };
    }

    private static <T> ChangeListener<T> run(Runnable runnable) {
        return (observable, oldValue, newValue) -> runnable.run();
    }

    public enum Sections {
        MENU,
        EVENTS,
        FIELD,
        EDITOR
    }
}
