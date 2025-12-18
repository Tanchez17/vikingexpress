package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.service.DataService;
import se.lu.ics.vikingexpress.util.CostCalculator;
import java.util.HashMap;
import java.util.Map;

public class ReportsController {

    @FXML
    private Label totalServiceCostLabel;
    @FXML
    private Label averageCostLabel;
    @FXML
    private Label mostExpensiveJobsLabel;
    @FXML
    private Label mostExpensiveWorkshopsLabel;
    @FXML
    private Label mostExpensiveVehiclesLabel;

    @FXML
    private TableView<Workshop> workshopCostTable;
    @FXML
    private TableColumn<Workshop, String> workshopNameColumn;
    @FXML
    private TableColumn<Workshop, String> workshopCostColumn;

    @FXML
    private TableView<Vehicle> vehicleCostTable;
    @FXML
    private TableColumn<Vehicle, String> vehicleNameColumn;
    @FXML
    private TableColumn<Vehicle, String> vehicleCostColumn;

    private final DataService dataService = DataService.getInstance();
    private final Map<Workshop, Double> workshopCostCache = new HashMap<>();
    private final Map<Vehicle, Double> vehicleCostCache = new HashMap<>();

    @FXML
    public void initialize() {
        setupTableColumns();
        refreshReports();
    }

    private void setupTableColumns() {
        workshopNameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        workshopCostColumn.setCellValueFactory(cellData -> {
            double cost = workshopCostCache.getOrDefault(cellData.getValue(), 0.0);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cost));
        });

        vehicleNameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        vehicleCostColumn.setCellValueFactory(cellData -> {
            double cost = vehicleCostCache.getOrDefault(cellData.getValue(), 0.0);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cost));
        });
    }

    @FXML
    void onRefresh() {
        refreshReports();
        showAlert("Success", "Reports refreshed");
    }

    @FXML
    void onExport() {
        StringBuilder report = new StringBuilder();
        report.append("=== VIKING EXPRESS FLEET MANAGEMENT REPORT ===\n\n");

        report.append("SUMMARY STATISTICS\n");
        report.append("==================\n");
        report.append("Total Service Cost: ").append(totalServiceCostLabel.getText()).append("\n");
        report.append("Average Maintenance Cost: ").append(averageCostLabel.getText()).append("\n");
        report.append("Most Expensive Jobs: ").append(mostExpensiveJobsLabel.getText()).append("\n");
        report.append("Most Expensive Workshops: ").append(mostExpensiveWorkshopsLabel.getText()).append("\n");
        report.append("Most Expensive Vehicles: ").append(mostExpensiveVehiclesLabel.getText()).append("\n\n");

        report.append("WORKSHOP COSTS\n");
        report.append("==============\n");
        for (Workshop ws : workshopCostTable.getItems()) {
            double cost = workshopCostCache.getOrDefault(ws, 0.0);
            report.append(String.format("%s: %.2f\n", ws.getName(), cost));
        }
        report.append("\n");

        report.append("VEHICLE COSTS\n");
        report.append("=============\n");
        for (Vehicle v : vehicleCostTable.getItems()) {
            double cost = vehicleCostCache.getOrDefault(v, 0.0);
            report.append(String.format("%s (VIN: %d): %.2f\n", v.getName(), v.getVin(), cost));
        }

        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        var content = new javafx.scene.input.ClipboardContent();
        content.putString(report.toString());
        clipboard.setContent(content);

        showAlert("Export", "Report copied to clipboard:\n\n" + report.toString());
    }

    private void refreshReports() {
        var vehicles = dataService.getAllVehicles();
        var serviceEntries = dataService.getAllServiceEntries();

        double totalCost = dataService.getTotalCostForAllVehicles();
        double avgCost = CostCalculator.calculateAverageCost(vehicles, serviceEntries);

        totalServiceCostLabel.setText(String.format("Total service cost: %.2f", totalCost));
        averageCostLabel.setText(String.format("Average maintenance cost: %.2f", avgCost));

        if (!serviceEntries.isEmpty()) {
            ServiceEntry mostExpensive = CostCalculator.findMostExpensiveServiceEntry(serviceEntries);
            if (mostExpensive != null) {
                mostExpensiveJobsLabel
                        .setText("Most expensive jobs: " + String.format("%.2f", mostExpensive.getCost()));
            } else {
                mostExpensiveJobsLabel.setText("Most expensive jobs: N/A");
            }
        } else {
            mostExpensiveJobsLabel.setText("Most expensive jobs: N/A");
        }

        workshopCostCache.clear();
        for (Workshop ws : dataService.getAllWorkshops()) {
            double cost = dataService.findServiceEntriesByWorkshop(ws)
                    .stream()
                    .mapToDouble(ServiceEntry::getCost)
                    .sum();
            workshopCostCache.put(ws, cost);
        }

        if (!workshopCostCache.isEmpty()) {
            Map.Entry<Workshop, Double> mostExpensiveWS = workshopCostCache.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            if (mostExpensiveWS != null) {
                mostExpensiveWorkshopsLabel.setText("Most expensive workshops: " +
                        String.format("%s (%.2f)", mostExpensiveWS.getKey().getName(), mostExpensiveWS.getValue()));
            } else {
                mostExpensiveWorkshopsLabel.setText("Most expensive workshops: N/A");
            }
        } else {
            mostExpensiveWorkshopsLabel.setText("Most expensive workshops: N/A");
        }

        vehicleCostCache.clear();
        for (Vehicle v : vehicles) {
            vehicleCostCache.put(v, dataService.getTotalServiceCost(v));
        }

        if (!vehicleCostCache.isEmpty()) {
            Map.Entry<Vehicle, Double> mostExpensiveVehicle = vehicleCostCache.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
            if (mostExpensiveVehicle != null) {
                mostExpensiveVehiclesLabel.setText("Most expensive vehicles: " +
                        String.format("%s (%.2f)", mostExpensiveVehicle.getKey().getName(),
                                mostExpensiveVehicle.getValue()));
            } else {
                mostExpensiveVehiclesLabel.setText("Most expensive vehicles: N/A");
            }
        } else {
            mostExpensiveVehiclesLabel.setText("Most expensive vehicles: N/A");
        }

        ObservableList<Workshop> workshopList = FXCollections.observableArrayList(
                dataService.getAllWorkshops());
        workshopCostTable.setItems(workshopList);
        workshopCostTable.refresh();

        ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList(vehicles);
        vehicleCostTable.setItems(vehicleList);
        vehicleCostTable.refresh();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
