package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import com.cpr3663.autocreation.util.GitHubUpdateChecker;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AboutController {
    @FXML private Label appNameLabel;
    @FXML private Label versionLabel;

    @FXML private HBox updateStatusBox;
    @FXML private ProgressIndicator updateSpinner;
    @FXML private Label updateStatusLabel;

    @FXML private Button updateActionButton;

    private Stage stage;
    private String pendingDownloadUrl;

    public static final class UpdateResult {
        private final boolean updateAvailable;
        private final String message;

        private UpdateResult(boolean updateAvailable, String message) {
            this.updateAvailable = updateAvailable;
            this.message = message;
        }

        public static UpdateResult upToDate() {
            return new UpdateResult(false, "You have the latest version.");
        }

        public static UpdateResult available(String versionLabel) {
            return new UpdateResult(true, "An update is available: " + versionLabel);
        }

        public boolean isUpdateAvailable() {
            return updateAvailable;
        }

        public String getMessage() {
            return message;
        }
    }

    @FXML
    private void initialize() {
        updateStatusBox.setVisible(false);
        updateStatusBox.setManaged(false);
        appNameLabel.setText("About " + Constants.App.APP_NAME);
        versionLabel.setText("Version " + Constants.App.APP_VERSION);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onUpdateActionClicked() {
        if (pendingDownloadUrl != null) {
            AppStateManager.getInstance().getHostServices().showDocument(pendingDownloadUrl);
            return;
        }

        GitHubUpdateChecker updateChecker = new GitHubUpdateChecker();

        updateActionButton.setDisable(true);
        showUpdateStatus("Checking for updates…", true);

        Task<UpdateResult> task = new Task<>() {
            @Override
            protected UpdateResult call() throws Exception {
                return updateChecker.checkForUpdates();
            }
        };

        task.setOnSucceeded(e -> {
            UpdateResult result = task.getValue();
            showUpdateStatus(result.getMessage(), false);
            if (result.isUpdateAvailable()) {
                updateActionButton.setText("Download Update");
                pendingDownloadUrl = updateChecker.getLatestReleaseUrl();
            } else {
                updateActionButton.setText("Check for Updates");
            }
            updateActionButton.setDisable(false);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            showUpdateStatus("Couldn't check for updates" + (ex != null ? ": " + ex.getMessage() : "."), false);
            updateActionButton.setDisable(false);
        });

        Thread thread = new Thread(task, "about-update-check");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void onReleaseNotesClicked() {
        AppStateManager.getInstance().getHostServices().showDocument(Constants.Links.RELEASE_NOTES);
    }

    @FXML
    private void onCloseClicked() {
        stage.close();
    }

    private void showUpdateStatus(String message, boolean showSpinner) {
        updateStatusLabel.setText(message);
        updateSpinner.setVisible(showSpinner);
        updateSpinner.setManaged(showSpinner);
        updateStatusBox.setVisible(true);
        updateStatusBox.setManaged(true);
    }
}
