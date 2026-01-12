package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;

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
            System.err.println("Failed to load view: content area not initialized");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentPane.getChildren().setAll(view);
        } catch (NullPointerException e) {
            System.err.println("Missing FXML resource: " + fxmlPath);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Failed to load view: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
