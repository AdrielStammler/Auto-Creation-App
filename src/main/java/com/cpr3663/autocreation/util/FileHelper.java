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
    static boolean openingIsViaUser = true;

    public static void create() throws IOException {
        Optional<String> optName = PopUpHelper.chooseNewAutoName();
        if (optName.isEmpty()) return;
        String name = optName.get();
        String fileName = name + Constants.FILE_SUFFIX;

        File autoFolder = new File(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER);
        File newAuto = new File(autoFolder, fileName).getAbsoluteFile();

        if (!Files.exists(newAuto.toPath())) {
            Files.createFile(newAuto.toPath());
            openingIsViaUser = true;
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
            openingIsViaUser = true;
            AppStateManager.getInstance().setOpenAutoName(name);
        }
    }

    public static void open() {
        String autoName = AppStateManager.getInstance().getOpenAutoName();
        if (autoName.isBlank()) return;
        Path autoPath = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, autoName + Constants.FILE_SUFFIX);
        String content;
        if (!Files.exists(autoPath)) {
            Toast.show("Error: Open Auto Name is not valid.");
            AppStateManager.getInstance().setOpenAutoName("");
            return;
        }
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
                        // TODO: fix type here
                        return new Event(fields[0], fields[1].split(Constants.Events.PARAM_DELIMITER), null, Boolean.parseBoolean(fields[2]), Event.DelayTypes.valueOf(fields[3]), Integer.parseInt(fields[4]));
                });

        ObservableList<Event> events = FXCollections.observableArrayList(lines.toList());

        AppStateManager.getInstance().setEvents(events);
        AppStateManager.getInstance().setSelectedIndex(events.size() - 1);
        if (openingIsViaUser) Toast.show("Opened " + autoName);
    }

    public static void delete() {
        String name = AppStateManager.getInstance().getOpenAutoName();
        if (name == null || name.isBlank()) {
            Toast.show("Error: No Auto is opened");
            return;
        }

        Path path = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, name + Constants.FILE_SUFFIX);

        boolean existed;
        try {
            existed = Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Toast.show("\"" + path.getFileName() + "\" " + (existed ? "is deleted" : "did not exist"));
        AppStateManager.getInstance().setOpenAutoName("");
    }

    public static void save() {
        AppStateManager.getInstance().saveState();
        String autoString = getAuto();
        String name = AppStateManager.getInstance().getOpenAutoName();
        if (name == null || name.isBlank()) {
            Optional<String> optName = PopUpHelper.chooseAutoName();
            if (optName.isEmpty())
                return;
            name = optName.get();
        }
        String fileName = name + Constants.FILE_SUFFIX;

        Path path = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, fileName);

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
        Toast.show("Saved");
        openingIsViaUser = true;
        AppStateManager.getInstance().setOpenAutoName(name);
    }

    public static void rename() {
        Optional<String> optName = PopUpHelper.chooseAutoName();
        if (optName.isEmpty()) {
            return;
        }
        String newName = optName.get();
        String fileName = newName + Constants.FILE_SUFFIX;

        Path oldAuto = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, AppStateManager.getInstance().getOpenAutoName() + Constants.FILE_SUFFIX);
        Path newAuto = oldAuto.resolveSibling(fileName);

        if (Files.exists(newAuto)) {
            PopUpHelper.showAlert(Alert.AlertType.ERROR, "Name in Use!", "There is already a auto named \"" + newName + "\"!", ButtonType.OK);
            return;
        }

        try {
            Files.move(oldAuto, newAuto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        openingIsViaUser = false;
        AppStateManager.getInstance().setOpenAutoName(newName);
        Toast.show("Renamed from \"" + oldAuto.getFileName() + "\" to \"" + newName + "\"");
    }

    public static void duplicate() {
        Optional<String> optName = PopUpHelper.chooseAutoName();
        if (optName.isEmpty()) {
            return;
        }
        String newName = optName.get();
        String fileName = newName + Constants.FILE_SUFFIX;

        Path oldAuto = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER, AppStateManager.getInstance().getOpenAutoName());
        Path newAuto = oldAuto.resolveSibling(fileName);

        if (Files.exists(newAuto)) {
            PopUpHelper.showAlert(Alert.AlertType.ERROR, "Name in Use!", "There is already a auto named \"" + newName + "\"!", ButtonType.OK);
            return;
        }

        try {
            Files.copy(oldAuto, newAuto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        openingIsViaUser = false;
        AppStateManager.getInstance().setOpenAutoName(newName);
        Toast.show("Duplicated \"" + oldAuto.getFileName() + "\" to \"" + newName + "\"");
    }

    private static String getAuto() {
        ObservableList<Event> events = AppStateManager.getInstance().getEvents();
        String[] stringEvents = events.stream().map(Event::toFileRow).toArray(String[]::new);
        return String.join(Constants.Events.NEW_LINE, stringEvents);
    }
}
