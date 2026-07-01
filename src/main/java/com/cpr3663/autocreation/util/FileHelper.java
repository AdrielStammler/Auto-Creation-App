package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;
import com.cpr3663.autocreation.Constants;

import java.io.File;

public class FileHelper {
    public static void create() {

    }

    public static void open(String autoName) {
        File deployFolder = new File(AppStateManager.getInstance().getRobotRepoPath(), Constants.Paths.ROBOT_REPO_DEPLOY);
        File auto = new File(deployFolder, autoName);
        // TODO make work

        AppStateManager.getInstance().setOpenAutoName(autoName);
    }

    public static void save() {
        AppStateManager.getInstance().setIsSaved(true);
    }
}
