package com.cpr3663.autocreation;

import com.cpr3663.autocreation.nodes.Field;
import com.cpr3663.autocreation.nodes.TopBar;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.FileHelper;
import com.cpr3663.autocreation.util.MiscHelper;
import com.cpr3663.autocreation.util.PopUpHelper;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

        // Creating and defining the BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topBar);
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
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(Constants.Stage.TITLE);
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
        AppStateManager.getInstance().fieldScaleProperty().addListener(run(refreshField(borderPane)));
        AppStateManager.getInstance().fieldImageProperty().addListener(run(refreshField(borderPane)));
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
            // TODO The threshold circle disappeared when position was changed with the editor
            if (AppStateManager.getInstance().isNotFieldEditing()) {
                refreshField(borderPane).run();
            }

        });
        AppStateManager.getInstance().selectedIndexProperty().addListener(run(() -> {
            if (AppStateManager.getInstance().isNotFieldEditing()) Field.Helper.updateSelection();
            if (AppStateManager.getInstance().isNotEditorEditing()) {
                FXMLLoader editorFxml2 = new FXMLLoader(Main.class.getResource("editor-view.fxml"));
                try {
                    borderPane.setRight(editorFxml2.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        FileHelper.open();
    }

    private static Runnable refreshField(BorderPane borderPane) {
        return () -> borderPane.setCenter(Field.getFieldPane());
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
