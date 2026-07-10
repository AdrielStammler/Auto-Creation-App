package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.Main;
import com.cpr3663.autocreation.objects.Event;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class FileHelper {
    public static void create() throws IOException {
        Optional<String> optName = PopUpHelper.chooseAutoName();
        if (optName.isEmpty()) {
            if (Main.mustCreateAuto()) FileHelper.create();
            return;
        }
        String name = optName.get() + ".dsv";

        File autoFolder = new File(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER);
        File newAuto = new File(autoFolder, name).getAbsoluteFile();

        if (!Files.exists(newAuto.toPath())) {
            Files.createFile(newAuto.toPath());
            AppStateManager.getInstance().setOpenAutoName(name);
            return;
        }

        ButtonType overwrite = new ButtonType("Overwrite");
        ButtonType open = new ButtonType("Open");
        ButtonType cancel = new ButtonType("Cancel");
        Optional<ButtonType> result = PopUpHelper.showAlert(Alert.AlertType.WARNING, "The Auto \"" + name + "\" already exists.",
                "Would you like to overwrite, open \"" + name + "\", or cancel?", overwrite, open, cancel);

        if (result.isPresent() && !result.get().equals(cancel)) {
            if (result.get().equals(overwrite)) {
                Files.delete(newAuto.toPath());
                Files.createFile(newAuto.toPath());
            }
            AppStateManager.getInstance().setOpenAutoName(name);
        }
    }

    public static void open() {
        String autoName = AppStateManager.getInstance().getOpenAutoName();
        Path auto = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, autoName);
        // TODO
    }

    public static void save() {
        AppStateManager.getInstance().saveState();
        String autoString = getAuto();
        System.out.println(autoString);
        // TODO
        AppStateManager.getInstance().setIsSaved(true);
    }

    public static void rename() {
        Optional<String> optName = PopUpHelper.chooseAutoName();
        if (optName.isEmpty()) {
            return;
        }
        String newName = optName.get() + ".dsv";

        Path oldAuto = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, AppStateManager.getInstance().getOpenAutoName());
        Path newAuto = oldAuto.resolveSibling(newName);

        if (Files.exists(newAuto)) {
            PopUpHelper.showAlert(Alert.AlertType.ERROR, "Name in Use!", "There is already a auto named \"" + newName + "\"!", ButtonType.OK);
            return;
        }

        try {
            Files.move(oldAuto, newAuto, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        AppStateManager.getInstance().setOpenAutoName(newName);
    }

    private static String getAuto() {
        ObservableList<Event> events = AppStateManager.getInstance().getEvents();
        String[] stringEvents = events.stream().map(Event::toFileRow).toArray(String[]::new);
        return String.join(Constants.Events.NEW_LINE, stringEvents);
    }
}
