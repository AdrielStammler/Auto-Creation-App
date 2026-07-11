package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
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

    public static void highlightImage(ImageView image) {
        ColorAdjust highlight = new ColorAdjust();
        highlight.setBrightness(0.2);
        highlight.setContrast(0.15);
        highlight.setSaturation(0.2);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.DODGERBLUE);
        glow.setRadius(20.0);
        glow.setSpread(0.6);

        glow.setInput(highlight);
        image.setEffect(glow);
    }
}
