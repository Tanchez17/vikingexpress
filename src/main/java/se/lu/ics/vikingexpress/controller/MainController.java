package se.lu.ics.vikingexpress.controller;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    private static final PseudoClass PC_OK = PseudoClass.getPseudoClass("ok");
    private static final PseudoClass PC_WARN = PseudoClass.getPseudoClass("warn");
    private static final PseudoClass PC_ERROR = PseudoClass.getPseudoClass("error");

    @FXML
    private StackPane contentPane;

    @FXML
    private Label statusLabel;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button vehiclesBtn;

    @FXML
    private Button workshopsBtn;

    @FXML
    private Button maintenanceBtn;

    @FXML
    private Button serviceHistoryBtn;

    @FXML
    private Button reportsBtn;

    @FXML
    private Button settingsBtn;

    @FXML
    public void initialize() {
        setDefaultButtonFalse(dashboardBtn, vehiclesBtn, workshopsBtn, maintenanceBtn,
                serviceHistoryBtn, reportsBtn, settingsBtn);

        showPage("/fxml/dashboard-view.fxml");
        setActiveButton(dashboardBtn);
        setStatus("Ready");
    }

    private void setDefaultButtonFalse(Button... buttons) {
        for (Button btn : buttons) {
            if (btn != null) {
                btn.setDefaultButton(false);
            }
        }
    }

    private void setActiveButton(Button activeButton) {
        Button[] navButtons = { dashboardBtn, vehiclesBtn, workshopsBtn, maintenanceBtn,
                serviceHistoryBtn, reportsBtn, settingsBtn };
        for (Button btn : navButtons) {
            if (btn != null) {
                btn.getStyleClass().remove("nav-button-active");
            }
        }

        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    public void setStatus(String message) {
        setStatusInternal(message, StatusKind.OK, false);
    }

    public void setWarning(String message) {
        setStatusInternal(message, StatusKind.WARN, true);
    }

    public void setError(String message) {
        setStatusInternal(message, StatusKind.ERROR, true);
    }

    public void setSuccess(String message) {
        setStatusInternal(message, StatusKind.OK, true);
    }

    @FXML
    void onDashboard() {
        showPage("/fxml/dashboard-view.fxml");
        setActiveButton(dashboardBtn);
    }

    @FXML
    void onVehicles() {
        showPage("/fxml/vehicles-view.fxml");
        setActiveButton(vehiclesBtn);
    }

    @FXML
    void onWorkshops() {
        showPage("/fxml/workshops-view.fxml");
        setActiveButton(workshopsBtn);
    }

    @FXML
    void onMaintenance() {
        showPage("/fxml/maintenance-view.fxml");
        setActiveButton(maintenanceBtn);
    }

    @FXML
    void onReports() {
        showPage("/fxml/reports-view.fxml");
        setActiveButton(reportsBtn);
    }

    @FXML
    void onServiceHistory() {
        showPage("/fxml/service-history-view.fxml");
        setActiveButton(serviceHistoryBtn);
    }

    @FXML
    void onSettings() {
        showPage("/fxml/settings-view.fxml");
        setActiveButton(settingsBtn);
    }

    private void showPage(String fxmlPath) {
        if (contentPane == null) {
            setError("Failed to load view: content area not initialized");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentPane.getChildren().setAll(view);

            String viewName = readableNameFromPath(fxmlPath);
            setStatus("Ready · " + viewName);
        } catch (NullPointerException e) {
            setError("Missing FXML resource: " + fxmlPath);
        } catch (IOException e) {
            setError("Failed to load view: " + e.getMessage());
        } catch (Exception e) {
            setError("Unexpected error: " + e.getMessage());
        }
    }

    private void setStatusInternal(String message, StatusKind kind, boolean decorate) {
        if (statusLabel == null) {
            return;
        }

        statusLabel.pseudoClassStateChanged(PC_OK, false);
        statusLabel.pseudoClassStateChanged(PC_WARN, false);
        statusLabel.pseudoClassStateChanged(PC_ERROR, false);

        switch (kind) {
            case OK -> statusLabel.pseudoClassStateChanged(PC_OK, true);
            case WARN -> statusLabel.pseudoClassStateChanged(PC_WARN, true);
            case ERROR -> statusLabel.pseudoClassStateChanged(PC_ERROR, true);
        }

        String prefix = "";
        if (decorate) {
            prefix = switch (kind) {
                case OK -> "✓ ";
                case WARN -> "⚠ ";
                case ERROR -> "❌ ";
            };
        }

        statusLabel.setText(prefix + message);
    }

    private String readableNameFromPath(String fxmlPath) {
        String file = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
        String base = file.replace(".fxml", "");
        base = base.replace("-view", "");
        base = base.replace("-", " ");
        return capitalizeWords(base);
    }

    private String capitalizeWords(String str) {
        if (str == null || str.isBlank())
            return "";
        return java.util.Arrays.stream(str.trim().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(java.util.stream.Collectors.joining(" "));
    }

    private enum StatusKind {
        OK, WARN, ERROR
    }
}
