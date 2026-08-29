package com.cpr3663.autocreation;

import com.cpr3663.autocreation.nodes.Field;
import com.cpr3663.autocreation.nodes.TopBar;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.FileHelper;
import com.cpr3663.autocreation.util.MiscHelper;
import com.cpr3663.autocreation.util.PopUpHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        AppStateManager.getInstance().setHostServices(getHostServices());

        Node topBar = TopBar.getTopBar(stage);
        FXMLLoader eventsFxml = new FXMLLoader(Main.class.getResource("events-view.fxml"));
        FXMLLoader editorFxml = new FXMLLoader(Main.class.getResource("editor-view.fxml"));
        Pane field = Field.getFieldPane();

        // Creating and defining the VBox and SplitPane
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(eventsFxml.load(), field, editorFxml.load());
        Platform.runLater(() -> splitPane.setDividerPositions(0.1, 0.85));
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        VBox vBox = new VBox(topBar, splitPane);

        Rectangle overlay = new Rectangle();
        overlay.widthProperty().bind(vBox.widthProperty());
        overlay.heightProperty().bind(vBox.heightProperty());
        overlay.setFill(Color.BLACK);
        overlay.setMouseTransparent(true);
        overlay.setOpacity(0);

        StackPane root = new StackPane(vBox, overlay);
        AppStateManager.getInstance().setRoot(root);

        // Creating scene and setting stage properties
        Scene scene = new Scene(root, Constants.Stage.WIDTH, Constants.Stage.HEIGHT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        MiscHelper.addResizeListener(stage);
        stage.show();
        stage.toFront();
        stage.requestFocus();
        Image icon = new Image(Objects.requireNonNull(PopUpHelper.class.getResource(Constants.Paths.APP_ICON)).toExternalForm());
        stage.getIcons().add(icon);
        MiscHelper.setDarkMode(stage);

        stage.setOnCloseRequest(e -> {
            e.consume();
            MiscHelper.closeRequest();
        });

        // Add Listeners
        AppStateManager.getInstance().isDarkModeProperty().addListener(run(() -> MiscHelper.setDarkMode(stage)));
        AppStateManager.getInstance().fieldImageProperty().addListener(run(refreshField(splitPane)));
        AppStateManager.getInstance().openAutoNameProperty().addListener(run(() -> {
            FileHelper.open();
            AppStateManager.getInstance().saveState();
        }));
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
            if (AppStateManager.getInstance().isNotFieldEditing()) {
                refreshField(splitPane).run();
            }

        });
        AppStateManager.getInstance().selectedIndexProperty().addListener(run(() -> {
            if (AppStateManager.getInstance().isNotFieldEditing()) Field.Helper.updateSelection();
            if (AppStateManager.getInstance().isNotEditorEditing()) {
                FXMLLoader editorFxml2 = new FXMLLoader(Main.class.getResource("editor-view.fxml"));
                try {
                    splitPane.getItems().set(2, editorFxml2.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        FileHelper.open();
        AppStateManager.getInstance().setIsSaved(true);
    }

    private static Runnable refreshField(SplitPane splitPane) {
        return () -> splitPane.getItems().set(1, Field.getFieldPane());
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
