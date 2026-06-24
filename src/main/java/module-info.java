module com.cpr3663.autocreation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens com.cpr3663.autocreation to javafx.fxml;
    exports com.cpr3663.autocreation;
    exports com.cpr3663.autocreation.controllers;
    opens com.cpr3663.autocreation.controllers to javafx.fxml;
    exports com.cpr3663.autocreation.util;
    opens com.cpr3663.autocreation.util to javafx.fxml;
}