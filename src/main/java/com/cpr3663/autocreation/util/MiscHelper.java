package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Window;

public class MiscHelper {

    public static void setDarkMode(Window stage) {
        Scene scene = stage.getScene();
        scene.getStylesheets().clear();
        if (AppStateManager.getInstance().isDarkMode()) {
            scene.getStylesheets().add(Constants.Paths.DARK_THEME);
        }
//        else {
//                scene.getStylesheets().add(Constants.Paths.LIGHT_THEME);
//        }
    }

    public static void closeRequest() {
        boolean cancel = PopUpHelper.checkForSaving();
        if (!cancel) Platform.exit();
    }
}
