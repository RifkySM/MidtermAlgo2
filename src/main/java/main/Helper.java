package main;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Helper {
    public static void showAlert(String title, String message, String type) {
        Alert.AlertType alertType = switch (type.toLowerCase()) {
            case "warn", "warning" -> Alert.AlertType.WARNING;
            case "error" -> Alert.AlertType.ERROR;
            case "confirm", "confirmation" -> Alert.AlertType.CONFIRMATION;
            default -> Alert.AlertType.INFORMATION;
        };

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static boolean showConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
