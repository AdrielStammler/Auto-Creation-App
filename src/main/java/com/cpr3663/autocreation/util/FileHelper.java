package com.cpr3663.autocreation.util;

import com.cpr3663.autocreation.AppStateManager;

public class FileHelper {
    public static void create() {

    }

    public static void open() {

    }

    public static void save() {
        AppStateManager.getInstance().setIsSaved(true);
    }
}
