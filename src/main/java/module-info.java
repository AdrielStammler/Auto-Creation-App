module com.cpr3663.autocreation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.desktop;
    requires java.logging;

    opens com.cpr3663.autocreation to javafx.fxml;
    exports com.cpr3663.autocreation;
    exports com.cpr3663.autocreation.controllers;
    exports com.cpr3663.autocreation.objects;
    opens com.cpr3663.autocreation.controllers to javafx.fxml;
    exports com.cpr3663.autocreation.util;
    opens com.cpr3663.autocreation.util to javafx.fxml;
}