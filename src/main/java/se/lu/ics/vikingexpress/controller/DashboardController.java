package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.model.MaintenanceSchedule;
import se.lu.ics.vikingexpress.model.enums.VehicleType;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;
import se.lu.ics.vikingexpress.service.DataService;
import se.lu.ics.vikingexpress.util.CostCalculator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    private static final double COST_LIMIT = 10000.0;

    @FXML
    private Label totalVehiclesLabel;
    @FXML
    private Label availableVehiclesLabel;
    @FXML
    private Label inServiceVehiclesLabel;
    @FXML
    private Label totalCostLabel;
    @FXML
    private Label averageCostLabel;
    @FXML
    private Label mostExpensiveJobLabel;
    @FXML
    private Label mostCostlyWorkshopLabel;
    @FXML
    private Label warningCountLabel;
    @FXML
    private ListView<String> warningsListView;
    @FXML
    private Label upcomingCountLabel;
    @FXML
    private ListView<String> upcomingMaintenanceListView;

    private final DataService dataService = DataService.getInstance();

    @FXML
    public void initialize() {
        try {
            refreshDashboard();
        } catch (Exception e) {
            System.err.println("Error initializing dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshDashboard() {
        try {
            List<Vehicle> vehicles = dataService.getAllVehicles();
            List<ServiceEntry> serviceEntries = dataService.getAllServiceEntries();
            List<MaintenanceSchedule> maintenanceSchedules = dataService.getAllMaintenanceSchedules();

            updateStatistics(vehicles);
            updateCostIndicators(vehicles, serviceEntries);
            updateWarnings(vehicles, serviceEntries);
            updateUpcomingMaintenance(maintenanceSchedules);
        } catch (Exception e) {
            System.err.println("Error refreshing dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatistics(List<Vehicle> vehicles) {
        int totalVehicles = vehicles.size();
        int vehiclesWithMaintenanceScheduled = 0;

        for (Vehicle vehicle : vehicles) {
            boolean hasIncompleteMaintenance = vehicle.getMaintenanceSchedules().stream()
                    .anyMatch(schedule -> !schedule.isCompleted());
            if (hasIncompleteMaintenance) {
                vehiclesWithMaintenanceScheduled++;
            }
        }

        totalVehiclesLabel.setText(String.valueOf(totalVehicles));
        availableVehiclesLabel.setText(String.valueOf(totalVehicles - vehiclesWithMaintenanceScheduled));
        inServiceVehiclesLabel.setText(String.valueOf(vehiclesWithMaintenanceScheduled));
    }

    private void updateCostIndicators(List<Vehicle> vehicles, List<ServiceEntry> serviceEntries) {
        double totalCost = dataService.getTotalCostForAllVehicles();
        double averageCost = CostCalculator.calculateAverageCost(vehicles, serviceEntries);

        totalCostLabel.setText(String.format("%.2f", totalCost));
        averageCostLabel.setText(String.format("%.2f", averageCost));

        if (!serviceEntries.isEmpty()) {
            ServiceEntry mostExpensive = CostCalculator.findMostExpensiveServiceEntry(serviceEntries);
            if (mostExpensive != null) {
                mostExpensiveJobLabel.setText(String.format("%.2f", mostExpensive.getCost()));
            } else {
                mostExpensiveJobLabel.setText("N/A");
            }

            Workshop mostExpensiveWS = CostCalculator.findMostExpensiveWorkshop(serviceEntries);
            mostCostlyWorkshopLabel.setText(mostExpensiveWS != null ? mostExpensiveWS.getName() : "N/A");
        } else {
            mostExpensiveJobLabel.setText("N/A");
            mostCostlyWorkshopLabel.setText("N/A");
        }
    }

    private void updateWarnings(List<Vehicle> vehicles, List<ServiceEntry> serviceEntries) {
        List<String> warnings = new java.util.ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            double totalCost = dataService.getTotalServiceCost(vehicle);
            if (totalCost > COST_LIMIT) {
                warnings.add(String.format("⚠ %s (VIN: %d) exceeds cost limit: %.2f (Limit: %.2f)",
                        vehicle.getName(), vehicle.getVin(), totalCost, COST_LIMIT));
            }

            int totalParts = vehicle.getTotalPartsReplaced();
            if (totalParts > 100) {
                warnings.add(String.format("⚠ %s (VIN: %d) has %d parts replaced (Max: 100)",
                        vehicle.getName(), vehicle.getVin(), totalParts));
            }

            for (ServiceEntry entry : vehicle.getServiceEntries()) {
                if (vehicle.getType() == VehicleType.LARGE_TRUCK
                        && entry.getWorkshop().getType() == WorkshopType.INTERNAL) {
                    warnings.add(String.format("⚠ %s (VIN: %d) - Large truck serviced at internal workshop: %s",
                            vehicle.getName(), vehicle.getVin(), entry.getWorkshop().getName()));
                }
            }
        }

        ObservableList<String> warningsList = FXCollections.observableArrayList(warnings);
        warningsListView.setItems(warningsList);
        warningCountLabel.setText(warnings.isEmpty() ? ""
                : String.format("(%d warning%s)",
                        warnings.size(), warnings.size() == 1 ? "" : "s"));

        if (warnings.isEmpty()) {
            warningsList.add("✓ No warnings or alerts");
        }
    }

    private void updateUpcomingMaintenance(List<MaintenanceSchedule> maintenanceSchedules) {
        LocalDate today = LocalDate.now();

        List<MaintenanceSchedule> upcoming = maintenanceSchedules.stream()
                .filter(schedule -> !schedule.isCompleted())
                .filter(schedule -> !schedule.getScheduledDate().isBefore(today))
                .sorted(Comparator.comparing(MaintenanceSchedule::getScheduledDate))
                .limit(10)
                .collect(Collectors.toList());

        List<String> maintenanceItems = new java.util.ArrayList<>();
        for (MaintenanceSchedule schedule : upcoming) {
            String description = schedule.getDescription() != null && !schedule.getDescription().isBlank()
                    ? schedule.getDescription()
                    : "Maintenance";
            String item = String.format("%s | %s (VIN: %d) at %s | %s",
                    schedule.getScheduledDate(),
                    schedule.getVehicle().getName(),
                    schedule.getVehicle().getVin(),
                    schedule.getWorkshop().getName(),
                    description);
            maintenanceItems.add(item);
        }

        ObservableList<String> maintenanceList = FXCollections.observableArrayList(maintenanceItems);
        upcomingMaintenanceListView.setItems(maintenanceList);
        upcomingCountLabel.setText(upcoming.isEmpty() ? "" : String.format("(%d scheduled)", upcoming.size()));

        if (maintenanceItems.isEmpty()) {
            maintenanceList.add("No upcoming maintenance scheduled");
        }
    }
}
