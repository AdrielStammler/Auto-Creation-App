package com.cpr3663.autocreation.controllers;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.objects.DriveEvent;
import com.cpr3663.autocreation.objects.Event;
import com.cpr3663.autocreation.util.MiscHelper;
import edu.wpi.first.apriltag.AprilTag;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class EditorController {
    @FXML public HBox root;

    @FXML private CheckBox afterPrevCheck;
    @FXML private CheckBox delayCheck;
    @FXML private ChoiceBox<Event.DelayTypes> delayTypeChoice;
    @FXML private TextField delayAmountText;

    @FXML private GridPane paramGridPane;

    private Event event;

    private Event.DelayTypes prevDelayType;

    @FXML
    private void initialize() {
        event = AppStateManager.getInstance().getSelectedEvent();
        if (event == null) {
            afterPrevCheck.setDisable(true);
            delayCheck.setDisable(true);
            delayTypeChoice.setDisable(true);
            delayAmountText.setDisable(true);
            return;
        }

        root.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> AppStateManager.getInstance().setEditorEditing());

        afterPrevCheck.selectedProperty().bindBidirectional(event.afterPrevProperty());
        delayCheck.setSelected(event.getDelayType().equals(Event.DelayTypes.NONE));
        delayCheck.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                delayTypeChoice.setValue(Objects.requireNonNullElse(prevDelayType, Event.DelayTypes.TIME));
            } else {
                prevDelayType = delayTypeChoice.getValue();
                delayTypeChoice.setValue(Event.DelayTypes.NONE);
            }
        });

        delayTypeChoice.getItems().addAll(Arrays.copyOfRange(Event.DelayTypes.values(), 1, Event.DelayTypes.values().length));
        delayTypeChoice.valueProperty().bindBidirectional(event.delayTypeProperty());
        delayTypeChoice.selectionModelProperty().addListener((obs, old, newV) -> {
            Event.DelayTypes type = newV.getSelectedItem();
            if (type.equals(Event.DelayTypes.TIME))
                delayAmountText.setTextFormatter(MiscHelper.countFormater());
            else
                delayAmountText.setTextFormatter(MiscHelper.posDoubleFormater());
        });
        delayAmountText.textProperty().bindBidirectional(event.delayProperty(), new NumberStringConverter());
        delayAmountText.setOnAction(e -> delayAmountText.getParent().requestFocus());

        delayTypeChoice.disableProperty().bind(delayCheck.selectedProperty().not());
        delayAmountText.disableProperty().bind(delayCheck.selectedProperty().not());

        if (event instanceof DriveEvent)
            setupDriveParams();
        else
            setupCustomParams();
    }

    private void setupCustomParams() {
        Event.Type type = event.getType();
        if (type == null) throw new RuntimeException("Error: Event type is null for a non-DriveEvent");

        String[] params = type.parameters();

        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            Label label = new Label(param);
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional(event.parameterProperty(i));
            textField.setOnAction(e -> textField.getParent().requestFocus());

            paramGridPane.add(label, 0, i);
            paramGridPane.add(textField, 1, i);
        }
    }

    private void setupDriveParams() {
        DriveEvent driveEvent = (DriveEvent) event;

        label("April Tag:", 0);
        aprilTagBox(driveEvent.aprilTagProperty(), 0);

        label("X Pos:", 1);
        doubleBox(driveEvent.xProperty(), 1);

        label("Y Pos:", 2);
        doubleBox(driveEvent.yProperty(), 2);

        label("Rotation:", 3);
        doubleBox(driveEvent.thetaProperty(), 3);

        label("Threshold:", 4);
        doubleBox(driveEvent.thresholdProperty(), 4);

        label("Max Velocity:", 5);
        disableableDoubleBox(driveEvent.maxVelocityProperty(), 5);

        label("Max Acceleration:", 6);
        disableableDoubleBox(driveEvent.maxAccelerationProperty(), 6);
    }

    private void label(String text, int row) {
        Label label = new Label(text);
        paramGridPane.add(label, 0, row);
    }

    private void textBox(StringProperty property, int row) {
        TextField field = new TextField();
        field.textProperty().bindBidirectional(property);
        field.setOnAction(e -> field.getParent().requestFocus());
        paramGridPane.add(field, 1, row);
    }

    private void aprilTagBox(Property<AprilTag> property, int row) {
        TextField field = new TextField();
        field.textProperty().bindBidirectional(property, new AprilTagStringConverter());
        field.setOnAction(e -> field.getParent().requestFocus());
        field.setTextFormatter(MiscHelper.intFormater());
        paramGridPane.add(field, 1, row);
    }

    private void doubleBox(DoubleProperty property, int row) {
        TextField field = new TextField();
        TextFormatter<String> formatter = MiscHelper.posDoubleFormater();
        field.setTextFormatter(formatter);
        field.setOnAction(e -> field.getParent().requestFocus());
        field.textProperty().bindBidirectional(property, new NumberStringConverter());
        paramGridPane.add(field, 1, row);
    }

    private void disableableDoubleBox(DoubleProperty property, int row) {
        TextField field = new TextField();
        CheckBox checkBox = new CheckBox();
        field.textProperty().bindBidirectional(property, new NumberStringConverter());
        field.setOnAction(e -> field.getParent().requestFocus());
        field.setTextFormatter(MiscHelper.posDoubleFormater());

        field.disableProperty().bind(checkBox.selectedProperty().not());
        AtomicReference<String> prev = new AtomicReference<>();
        checkBox.selectedProperty().addListener((obs, old, newV) -> {
            if (newV) {
                field.setTextFormatter(MiscHelper.posDoubleFormater());
                String str = prev.get();
                field.setText((str == null || str.isBlank() || str.equals("-1")) ? "0" : str);
            } else {
                field.setTextFormatter(null);
                prev.set(field.getText());
                field.setText("-1");
            }
        });

        checkBox.setSelected(!field.getText().equals("-1"));

        HBox hBox = new HBox(checkBox, field);
        paramGridPane.add(hBox, 1, row);
    }

    private static class AprilTagStringConverter extends StringConverter<AprilTag> {
        private static AprilTagFieldLayout fieldLayout;

        public AprilTagStringConverter() {
            fieldLayout = AprilTagFieldLayout.loadField(AppStateManager.getInstance().getAprilTagField());
        }

        @Override
        public String toString(AprilTag aprilTag) {
            return Integer.toString(aprilTag.ID);
        }
        @Override
        public AprilTag fromString(String string) {
            int id = Integer.parseInt(string);
            Optional<Pose3d> optPose = fieldLayout.getTagPose(id);
            return optPose.map(pose -> new AprilTag(id, pose)).orElse(new AprilTag(-1, new Pose3d()));
        }
    }

}
