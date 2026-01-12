package se.lu.ics.vikingexpress.util;

import javafx.scene.control.Alert;

public final class AlertUtils {

    private AlertUtils() {
    }

    public static void showAlert(String title, String message) {
        Alert.AlertType alertType = title.equals("Error") || title.equals("Validation Error")
                ? Alert.AlertType.ERROR
                : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showWarningAlert(String title, String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
