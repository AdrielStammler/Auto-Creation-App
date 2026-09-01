package com.cpr3663.autocreation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Constants {
    public static final double ROBOT_IMAGE_Y_EXTRA_PIXELS = 11.0/2.0;
    public static final String FILE_SUFFIX = ".dsv";

    public static final class Events {
        public static final String DELIMITER = ":";
        public static final String PARAM_DELIMITER = ",";
        public static final String NEW_LINE = "\n";
        public static final String DRIVE_NAME = "drive";
        public static final String DRIVE_PARAMS = "driveParams";
    }

    public static final class Links {
        public static final String CREATE_ISSUE = "https://github.com/AdrielStammler/Auto-Creation-App/issues/new";
        public static final String GITHUB = "https://github.com/AdrielStammler/Auto-Creation-App";
        public static final String RELEASE_NOTES = "https://github.com/AdrielStammler/Auto-Creation-App/releases#release-v" + App.APP_VERSION;
    }

    public static final class Paths {
        public static final String ROBOT_ICON = "/icons/RobotIcon.png";
        public static final String TAG_ICON = "/icons/TagIcon.png";
        public static final String ALERT_ICON = "/icons/AlertIcon.png";
        public static final String APP_ICON = "/icons/AppIcon.png";
        public static final String FIELD_IMAGE = System.getProperty("user.home") + "/.autoCreation/images/field.png";
        public static final String DEFAULT_FIELD_IMAGE = "/other/default-field.png";
        public static final String DARK_THEME = "/themes/dark-theme.css";

        private static final String ROBOT_REPO_DEPLOY = "/src/main/deploy/auto-creation";
        public static final String ROBOT_REPO_AUTOS_FOLDER = ROBOT_REPO_DEPLOY + "/autos";
        public static final String ROBOT_REPO_POINTS_FOLDER = ROBOT_REPO_DEPLOY + "/points";
    }

    public static final class App {
        public static final String APP_NAME = "Auto Creation";
        public static final String APP_VERSION = getVersion();

        public static final int DEFAULT_WIDTH = 1650;
        public static final int DEFAULT_HEIGHT = 800;
    }

    private static String getVersion() {
        try (InputStream in = Constants.class.getResourceAsStream("/version.properties")) {
            if (in == null) {
                return "unknown";
            }
            Properties props = new Properties();
            props.load(in);
            return props.getProperty("version", "unknown");
        } catch (IOException e) {
            return "unknown";
        }
    }
}
