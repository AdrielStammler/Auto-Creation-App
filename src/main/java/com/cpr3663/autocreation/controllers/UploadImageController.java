package com.cpr3663.autocreation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UploadImageController {
    @FXML private Button browseButton;
    public Image image = null;
    public Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Image getImage() {
        return image;
    }

    @FXML
    public void initialize() {
        Background background = new Background(new BackgroundFill(Color.BLUE, new CornerRadii(5), Insets.EMPTY));
        browseButton.setBackground(background);
    }

    @FXML
    private void browse(ActionEvent e) {
        e.consume();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        fileChooser.setInitialDirectory(getDefaultFolder());
        fileChooser.getExtensionFilters().addAll(new  FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (file == null) return;
        try {
            image = new Image(file.toURI().toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        stage.close();
    }

    private File getDefaultFolder() {
        Path home = Path.of(System.getProperty("user.home"));
        Path downloads = home.resolve("Downloads");

        if (Files.exists(downloads) && Files.isDirectory(downloads)) return downloads.toFile();
        return home.toFile();
    }

    @FXML
    public void dragged(DragEvent e) {
        Dragboard db = e.getDragboard();
        if (db.hasImage() || db.hasFiles()) {
            e.acceptTransferModes(TransferMode.COPY);
        }
        e.consume();
    }

    @FXML
    public void dropped(DragEvent e) {
        Dragboard db = e.getDragboard();
        if (db.hasImage() || db.hasFiles()) {
            try {
                image = new Image(new FileInputStream(db.getFiles().get(0)));
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            stage.close();
        }
        e.consume();
    }
}
