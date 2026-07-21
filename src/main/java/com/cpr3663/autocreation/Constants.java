package com.cpr3663.autocreation;

public final class Constants {
    public static final int ROBOT_IMAGE_Y_EXTRA_PIXELS = 11;

    public static final class Events {
        public static final String DELIMITER = ":";
        public static final String PARAM_DELIMITER = ",";
        public static final String NEW_LINE = "\n";
        public static final String DRIVE_NAME = "drive";
    }

    public static final class Links {
        public static final String CREATE_GITHUB_ISSUE = "https://github.com/AdrielStammler/Auto-Creation-App/issues/new";
        public static final String OPEN_GITHUB = "https://github.com/AdrielStammler/Auto-Creation-App";
    }

    public static final class Paths {
        public static final String ROBOT_ICON = "/icons/RobotIcon.png";
        public static final String ALERT_ICON = "/icons/AlertIcon.png";
        public static final String FIELD_IMAGE = System.getProperty("user.home") + "/.autoCreation/images/field.png";
        public static final String DARK_THEME = "/themes/dark-theme.css";

        private static final String ROBOT_REPO_DEPLOY = "/src/main/deploy/auto-creation";
        public static final String ROBOT_REPO_AUTOS_FOLDER = ROBOT_REPO_DEPLOY + "/autos";
        public static final String ROBOT_REPO_POINTS_FOLDER = ROBOT_REPO_DEPLOY + "/points";
    }

    public static final class Stage {
        public static final String TITLE = "Hello!";

        public static final int WIDTH = 1600;
        public static final int HEIGHT = 800;
    }
}
