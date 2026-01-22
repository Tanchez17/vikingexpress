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
import se.lu.ics.vikingexpress.util.AlertUtils;
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

    private final DataService DATASERVICE = DataService.getInstance();
    private final Map<Workshop, Double> WORKSHOP_COST_CACHE = new HashMap<>();
    private final Map<Vehicle, Double> VEHICLE_COST_CACHE = new HashMap<>();

    @FXML
    public void initialize() {
        setupTableColumns();
        refreshReports();
    }

    private void setupTableColumns() {
        workshopNameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        workshopCostColumn.setCellValueFactory(cellData -> {
            double cost = WORKSHOP_COST_CACHE.getOrDefault(cellData.getValue(), 0.0);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cost));
        });

        vehicleNameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        vehicleCostColumn.setCellValueFactory(cellData -> {
            double cost = VEHICLE_COST_CACHE.getOrDefault(cellData.getValue(), 0.0);
            return new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cost));
        });
    }

    @FXML
    void onRefresh() {
        refreshReports();
        AlertUtils.showAlert("Success", "Reports refreshed");
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
            double cost = WORKSHOP_COST_CACHE.getOrDefault(ws, 0.0);
            report.append(String.format("%s: %.2f\n", ws.getName(), cost));
        }
        report.append("\n");

        report.append("VEHICLE COSTS\n");
        report.append("=============\n");
        for (Vehicle v : vehicleCostTable.getItems()) {
            double cost = VEHICLE_COST_CACHE.getOrDefault(v, 0.0);
            report.append(String.format("%s (VIN: %d): %.2f\n", v.getName(), v.getVin(), cost));
        }

        var clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        var content = new javafx.scene.input.ClipboardContent();
        content.putString(report.toString());
        clipboard.setContent(content);

        AlertUtils.showAlert("Export", "Report copied to clipboard:\n\n" + report.toString());
    }

    private void refreshReports() {
        var vehicles = DATASERVICE.getAllVehicles();
        var serviceEntries = DATASERVICE.getAllServiceEntries();

        double totalCost = DATASERVICE.getTotalCostForAllVehicles();
        double avgCost = CostCalculator.calculateAverageCost(vehicles, serviceEntries);

        totalServiceCostLabel.setText(String.format("Total service cost: %.2f", totalCost));
        averageCostLabel.setText(String.format("Average maintenance cost: %.2f", avgCost));

        if (!serviceEntries.isEmpty()) {
            ServiceEntry mostExpensive = CostCalculator.findMostExpensiveServiceEntry(serviceEntries);
            if (mostExpensive != null) {
                mostExpensiveJobsLabel
                        .setText(String.format("Most expensive jobs: %.2f | Vehicle: %s (VIN: %d) | Workshop: %s",
                                mostExpensive.getCost(),
                                mostExpensive.getVehicle().getName(),
                                mostExpensive.getVehicle().getVin(),
                                mostExpensive.getWorkshop().getName()));
            } else {
                mostExpensiveJobsLabel.setText("Most expensive jobs: N/A");
            }
        } else {
            mostExpensiveJobsLabel.setText("Most expensive jobs: N/A");
        }

        WORKSHOP_COST_CACHE.clear();
        for (Workshop ws : DATASERVICE.getAllWorkshops()) {
            double cost = DATASERVICE.findServiceEntriesByWorkshop(ws)
                    .stream()
                    .mapToDouble(ServiceEntry::getCost)
                    .sum();
            WORKSHOP_COST_CACHE.put(ws, cost);
        }

        if (!WORKSHOP_COST_CACHE.isEmpty()) {
            Map.Entry<Workshop, Double> mostExpensiveWS = WORKSHOP_COST_CACHE.entrySet().stream()
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

        VEHICLE_COST_CACHE.clear();
        for (Vehicle v : vehicles) {
            VEHICLE_COST_CACHE.put(v, DATASERVICE.getTotalServiceCost(v));
        }

        if (!VEHICLE_COST_CACHE.isEmpty()) {
            Map.Entry<Vehicle, Double> mostExpensiveVehicle = VEHICLE_COST_CACHE.entrySet().stream()
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
                DATASERVICE.getAllWorkshops());
        workshopCostTable.setItems(workshopList);
        workshopCostTable.refresh();

        ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList(vehicles);
        vehicleCostTable.setItems(vehicleList);
        vehicleCostTable.refresh();
    }
}
