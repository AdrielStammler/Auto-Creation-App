package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.controllers.UploadImageController;
import com.cpr3663.autocreation.controllers.AutoNameController;
import com.cpr3663.autocreation.controllers.SettingsController;
import javafx.animation.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PopUpHelper {
    /**
     * @return a {@link Boolean} representing if the caller may continue with overriding (if overriding would occur).
     *  {@link Boolean true}: Do not override. {@link Boolean false}: Override or continue
     */
    public static boolean checkForOverride() {
        if (AppStateManager.getInstance().isSaved())
            return false;

        Alert ask = new Alert(Alert.AlertType.CONFIRMATION);

        ButtonType buttonSave = new ButtonType("Continue & Save");
        ButtonType buttonDiscard = new ButtonType("Continue & Discard");
        ButtonType buttonCancel = new ButtonType("Cancel");

        Image icon = new Image(Objects.requireNonNull(PopUpHelper.class.getResource(Constants.Paths.ALERT_ICON)).toExternalForm());
        Stage stage = (Stage) ask.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
        ImageView iconView = new ImageView(icon);
        iconView.setPreserveRatio(true);
        iconView.setFitHeight(65);
        ask.setGraphic(iconView);

        ask.getButtonTypes().setAll(buttonSave, buttonDiscard, buttonCancel);
        ask.setTitle("Unsaved Changes");
        ask.setHeaderText("You have unsaved changes, are you sure you want to continue?");
        ask.getDialogPane().setContent(new Region());

        Optional<ButtonType> result = ask.showAndWait();

        if (result.isPresent() && !result.get().equals(buttonCancel)) {
            if (result.get().equals(buttonSave)) {
                FileHelper.save();
                return false;
            }
            return !result.get().equals(buttonDiscard);
        }
        return true;
    }

    public static void showSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(PopUpHelper.class.getResource("/com/cpr3663/autocreation/settings-view.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initOwner(AppStateManager.getInstance().getWindow());
            popupStage.setTitle("Settings");

            Scene scene = new Scene(loader.load());
            popupStage.setScene(scene);
            MiscHelper.setDarkMode(popupStage);
            SettingsController controller = loader.getController();
            controller.setStage(popupStage);

            showPopUp(popupStage);

        } catch (IOException e) {
            Logger.getLogger(PopUpHelper.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void selectAutoToOpen() {
        Path autosFolder = Path.of(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_AUTOS_FOLDER).toAbsolutePath();
        if (Files.notExists(autosFolder)) {
            try {
                Files.createDirectories(autosFolder);
            } catch (IOException e) {
                Logger.getLogger(PopUpHelper.class.getName()).log(Level.SEVERE, "Failed to create directory at " + autosFolder, e);
            }
        }
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(autosFolder.toFile());
        chooser.setTitle("Select an Auto");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Auto Files", "*.dsv"));
        File auto = chooser.showOpenDialog(AppStateManager.getInstance().getWindow());
        if (auto == null) return;
        String path = auto.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".dsv")) {
            auto = new File(path + ".dsv");
        }
        AppStateManager.getInstance().setOpenAutoName(auto.getName());
    }

    public static Optional<String> chooseAutoName() {
        try {
            FXMLLoader loader = new FXMLLoader(PopUpHelper.class.getResource("/com/cpr3663/autocreation/autoName-view.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UNDECORATED);
            popupStage.initOwner(AppStateManager.getInstance().getWindow());
            popupStage.setTitle("New Auto Name");

            Scene scene = new Scene(loader.load());
            popupStage.setScene(scene);
            MiscHelper.setDarkMode(popupStage);
            AutoNameController controller = loader.getController();
            controller.setStage(popupStage);

            showPopUp(popupStage);

            return Optional.ofNullable(controller.getUserInput());

        } catch (IOException e) {
            Logger.getLogger(PopUpHelper.class.getName()).log(Level.SEVERE, null, e);
            return Optional.empty();
        }
    }

    public static Image getImage(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(PopUpHelper.class.getResource("/com/cpr3663/autocreation/uploadImage-view.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UTILITY);
            popupStage.initOwner(stage);
            popupStage.setTitle("Upload Image");
            popupStage.setResizable(false);

            Scene scene = new Scene(loader.load());
            popupStage.setScene(scene);
            MiscHelper.setDarkMode(popupStage);
            UploadImageController controller = loader.getController();
            controller.setStage(popupStage);

            popupStage.showAndWait();

            return controller.getImage();

        } catch (IOException e) {
            Logger.getLogger(PopUpHelper.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public static Optional<ButtonType> showAlert(Alert.AlertType type, String title, String message, ButtonType... buttons) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.getButtonTypes().setAll(buttons);
        return alert.showAndWait();
    }

    private static void showPopUp(Stage popupStage) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1));
        fadeTransition.setNode(AppStateManager.getInstance().getRoot().getChildren().get(1));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(0.5);
        fadeTransition.playFromStart();

        popupStage.showAndWait();

        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(0.0);
        fadeTransition.playFromStart();
    }
}
