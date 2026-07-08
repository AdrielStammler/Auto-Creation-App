package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.objects.Event;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class FileHelper {
    public static void create() throws IOException {
        Optional<String> optName = PopUpHelper.chooseNewAutoName();
        if (optName.isEmpty()) return;
        String name = optName.get() + ".dsv";

        File deploy = new File(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_DEPLOY);
        File newAuto = new File(deploy, name);

        if (!Files.exists(deploy.toPath())) {
            Files.createFile(newAuto.getAbsoluteFile().toPath());
            AppStateManager.getInstance().setOpenAutoName(name);
            return;
        }

        ButtonType buttonOverwrite = new ButtonType("Overwrite");
        ButtonType buttonOpen = new ButtonType("Open");
        ButtonType buttonCancel = new ButtonType("Cancel");

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("The Auto \"" + name + "\" already exists.");
        alert.setHeaderText("Would you like to overwrite, open \"" + name + "\", or cancel?");
        alert.getButtonTypes().setAll(buttonOverwrite, buttonOpen, buttonCancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && !result.get().equals(buttonCancel)) {
            if (result.get().equals(buttonOverwrite)) {
                Files.delete(newAuto.getAbsoluteFile().toPath());
                Files.createFile(newAuto.getAbsoluteFile().toPath());
            }
            AppStateManager.getInstance().setOpenAutoName(name);
        }
    }

    public static void open() {
        String autoName = AppStateManager.getInstance().getOpenAutoName();
        File deployFolder = new File(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_DEPLOY);
        File auto = new File(deployFolder, autoName);
        // TODO
    }

    public static void save() {
        String autoString = getAuto();
        System.out.println(autoString);
        // TODO
        AppStateManager.getInstance().setIsSaved(true);
    }

    private static String getAuto() {
        ObservableList<Event> events = AppStateManager.getInstance().getEvents();
        String[] stringEvents = events.stream().map(Event::toFileRow).toArray(String[]::new);
        return String.join(Constants.Events.NEW_LINE, stringEvents);
    }
}
