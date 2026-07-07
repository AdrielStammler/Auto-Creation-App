package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.Main;
import com.cpr3663.autocreation.controllers.NewAutoController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PopUpHelper {
    public static boolean confirmOverride() {
        if (AppStateManager.getInstance().isSaved())
            return true;

        Alert ask = new Alert(Alert.AlertType.CONFIRMATION);

        ButtonType buttonSave = new ButtonType("Continue & Save");
        ButtonType buttonDiscard = new ButtonType("Continue & Discard");
        ButtonType buttonCancel = new ButtonType("Cancel");

        Image icon = new Image(Objects.requireNonNull(Menus.class.getResource(Constants.Paths.ALERT_ICON)).toExternalForm());
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
                return true;
            }
            return result.get().equals(buttonDiscard);
        }
        return false;
    }

    public static void selectAutoToOpen(Window targetWindow) {
        File deployFolder = new File(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_DEPLOY);
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(deployFolder);
        chooser.setTitle("Select an Auto");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Auto Files", "*.dsv"));
        File auto = chooser.showOpenDialog(targetWindow);
        if (auto == null) return;
        String path = auto.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".dsv")) {
            auto = new File(path + ".dsv");
        }
        AppStateManager.getInstance().setOpenAutoName(auto.getName());
    }

    public static Optional<String> chooseNewAutoName(Window ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader(PopUpHelper.class.getResource("/com/cpr3663/autocreation/newAuto-view.fxml"));
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initStyle(StageStyle.UTILITY);
            popupStage.initOwner(ownerStage);
            popupStage.setTitle("New Auto Name");

            Scene scene = new Scene(loader.load());
            popupStage.setScene(scene);
            Main.setDarkMode(popupStage, AppStateManager.getInstance().isDarkMode());

            NewAutoController controller = loader.getController();
            controller.setStage(popupStage);

            // This blocks execution until the popup window is closed
            popupStage.showAndWait();

            return Optional.ofNullable(controller.getUserInput());

        } catch (IOException e) {
            Logger.getLogger(PopUpHelper.class.getName()).log(Level.SEVERE, null, e);
            return Optional.empty();
        }
    }
}
