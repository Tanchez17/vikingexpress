package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.enums.VehicleType;
import se.lu.ics.vikingexpress.service.DataService;

public class VehicleController {

    @FXML
    private TableView<Vehicle> vehicleTable;
    @FXML
    private TableColumn<Vehicle, String> vinColumn;
    @FXML
    private TableColumn<Vehicle, String> nameColumn;
    @FXML
    private TableColumn<Vehicle, String> typeColumn;
    @FXML
    private TableColumn<Vehicle, String> capacityColumn;
    @FXML
    private TableColumn<Vehicle, String> locationColumn;
    @FXML
    private TableColumn<Vehicle, String> statusColumn;
    @FXML
    private TableColumn<Vehicle, String> partsColumn;

    @FXML
    private TextField vinField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<VehicleType> typeCombo;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField costField;

    private final DataService dataService = DataService.getInstance();
    private ObservableList<Vehicle> vehicles;

    @FXML
    public void initialize() {
        setupTableColumns();
        vehicleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            populateFields(newSel);
        });
        loadVehicles();
        typeCombo.setItems(FXCollections.observableArrayList(VehicleType.values()));
    }

    private void setupTableColumns() {
        vinColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getVin())));
        nameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        typeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType().toString()));
        capacityColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getCapacity())));
        locationColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCurrentLocation()));
        statusColumn.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue();
            if (vehicle.isDecommissioned()) {
                return new javafx.beans.property.SimpleStringProperty("Decommissioned");
            }
            boolean hasIncompleteMaintenance = vehicle.getMaintenanceSchedules().stream()
                    .anyMatch(schedule -> !schedule.isCompleted());
            String status = hasIncompleteMaintenance ? "In service" : "Available";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        partsColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getTotalPartsReplaced())));
    }

    private void loadVehicles() {
        vehicles = FXCollections.observableArrayList(dataService.getAllVehicles());
        vehicleTable.setItems(vehicles);
    }

    @FXML
    void onAddVehicle() {
        try {
            String name = nameField.getText().trim();
            VehicleType type = typeCombo.getValue();
            String location = locationField.getText().trim();
            String capacityStr = capacityField.getText().trim();

            if (name.isEmpty() || type == null || location.isEmpty() || capacityStr.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled");
                return;
            }

            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                showAlert("Validation Error", "Capacity must be greater than zero");
                return;
            }

            Vehicle vehicle = new Vehicle(name, type, location, capacity);
            dataService.addVehicle(vehicle);

            clearFields();
            loadVehicles();
            showAlert("Success", "Vehicle added successfully");
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Capacity must be a valid number");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void onEditVehicle() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a vehicle to edit");
            return;
        }

        try {
            String name = nameField.getText().trim();
            VehicleType type = typeCombo.getValue();
            String location = locationField.getText().trim();
            String capacityStr = capacityField.getText().trim();

            if (name.isEmpty() || type == null || location.isEmpty() || capacityStr.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled");
                return;
            }

            int capacity = Integer.parseInt(capacityStr);
            selected.setName(name);
            selected.setType(type);
            selected.setCurrentLocation(location);
            selected.setCapacity(capacity);

            vehicleTable.refresh();
            clearFields();
            showAlert("Success", "Vehicle updated successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Capacity must be a valid number");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void onDeleteVehicle() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a vehicle to delete");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Vehicle");
        confirmDialog.setContentText(
                "Are you sure you want to delete " + selected.getName() + " (VIN: " + selected.getVin() + ")?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dataService.removeVehicle(selected);
            loadVehicles();
            clearFields();
            showAlert("Success", "Vehicle deleted successfully");
        }
    }

    @FXML
    void onSelectVehicle() {
        populateFields(vehicleTable.getSelectionModel().getSelectedItem());
    }

    private void populateFields(Vehicle selected) {
        if (selected == null) {
            return;
        }

        vinField.setText(String.valueOf(selected.getVin()));
        nameField.setText(selected.getName());
        typeCombo.setValue(selected.getType());
        capacityField.setText(String.valueOf(selected.getCapacity()));
        locationField.setText(selected.getCurrentLocation());
        costField.setText(String.format("%.2f", dataService.getTotalServiceCost(selected)));
    }

    private void clearFields() {
        vinField.clear();
        nameField.clear();
        typeCombo.setValue(null);
        capacityField.clear();
        locationField.clear();
        costField.clear();
    }

    private void showAlert(String title, String message) {
        Alert.AlertType alertType = title.equals("Error") || title.equals("Validation Error")
                ? Alert.AlertType.ERROR
                : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
