package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.objects.DriveEvent;
import com.cpr3663.autocreation.objects.Event;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class FileHelper {
    public static void create() throws IOException {
        Optional<String> optName = PopUpHelper.chooseNewAutoName();
        if (optName.isEmpty()) return;
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
        Path autoPath = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, autoName);
        String content;
        try {
            content = Files.readString(autoPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stream<Event> lines = Arrays.stream(content.split(Constants.Events.NEW_LINE))
                .map(row -> row.split(Constants.Events.DELIMITER))
                .filter(fields -> fields.length == 5)
                .map(fields -> {
                    if (fields[0].equals(Constants.Events.DRIVE_NAME)) {
                        String[] paramsStr = fields[1].split(Constants.Events.PARAM_DELIMITER);
                        double[] params = Arrays.stream(paramsStr).mapToDouble(Double::parseDouble).toArray();
                        return new DriveEvent(params[0], params[1], params[2], params[3], params[4], params[5], Boolean.parseBoolean(fields[2]), Event.DelayTypes.valueOf(fields[3]), Integer.parseInt(fields[4]));
                    } else
                        return new Event(fields[0], fields[1].split(Constants.Events.PARAM_DELIMITER), Boolean.parseBoolean(fields[2]), Event.DelayTypes.valueOf(fields[3]), Integer.parseInt(fields[4]));
                });

        ObservableList<Event> events = FXCollections.observableArrayList(lines.toList());

        AppStateManager.getInstance().setEvents(events);
        AppStateManager.getInstance().setSelectedIndex(events.size() - 1);
        Toast.show("Opened " + autoName);
    }

    public static void save() {
        AppStateManager.getInstance().saveState();
        String autoString = getAuto();
        String name = AppStateManager.getInstance().getOpenAutoName();
        if (name == null || name.isEmpty()) {
            Optional<String> optName = PopUpHelper.chooseAutoName();
            if (optName.isEmpty())
                return;
            name = optName.get() + ".dsv";
        }

        Path path = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, name);

        try {
            Files.createDirectories(path.getParent());
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
            Files.writeString(path, autoString, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AppStateManager.getInstance().setIsSaved(true);
        AppStateManager.getInstance().setOpenAutoName(name);
        Toast.show("Successfully Saved");
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
            Files.move(oldAuto, newAuto);
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
