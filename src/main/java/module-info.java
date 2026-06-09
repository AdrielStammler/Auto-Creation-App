module com.cpr3663.autocreation {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.cpr3663.autocreation to javafx.fxml;
    exports com.cpr3663.autocreation;
}