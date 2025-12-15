package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.fxml.FXML;
import javafx.scene.layout.*;

public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        showPage("/fxml/vehicles-view.fxml");
    }

    @FXML
    void onVehicles() {
        showPage("/fxml/vehicles-view.fxml");
    }

    @FXML
    void onWorkshops() {
        showPage("/fxml/workshops-view.fxml");
    }

    @FXML
    void onMaintenance() {
        showPage("/fxml/maintenance-view.fxml");
    }

    @FXML
    void onReports() {
        showPage("/fxml/reports-view.fxml");
    }

    @FXML
    void onServiceHistory() {
        showPage("/fxml/service-history-view.fxml");
    }

    @FXML
    void onSettings() {
        showPage("/fxml/settings-view.fxml");
    }

    private void showPage(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
