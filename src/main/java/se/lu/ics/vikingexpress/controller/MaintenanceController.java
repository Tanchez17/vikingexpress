package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.MaintenanceSchedule;
import se.lu.ics.vikingexpress.service.DataService;
import java.time.LocalDate;

public class MaintenanceController {

    @FXML
    private ComboBox<Vehicle> vehicleCombo;
    @FXML
    private TableView<MaintenanceSchedule> maintenanceTable;
    @FXML
    private TableColumn<MaintenanceSchedule, String> dateColumn;
    @FXML
    private TableColumn<MaintenanceSchedule, String> typeColumn;
    @FXML
    private TableColumn<MaintenanceSchedule, String> workshopColumn;
    @FXML
    private TableColumn<MaintenanceSchedule, String> notesColumn;

    private final DataService dataService = DataService.getInstance();
    private ObservableList<MaintenanceSchedule> maintenanceList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadVehicles();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(cellData -> {
            MaintenanceSchedule schedule = cellData.getValue();
            String dateStr = schedule.isCompleted() && schedule.getCompletedDate() != null
                    ? schedule.getCompletedDate().toString()
                    : schedule.getScheduledDate().toString();
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isCompleted() ? "Completed" : "Pending"));
        workshopColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWorkshop().getName()));
        notesColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDescription() != null ? cellData.getValue().getDescription() : "N/A"));
    }

    private void loadVehicles() {
        vehicleCombo.setItems(FXCollections.observableArrayList(dataService.getAllVehicles()));
        vehicleCombo.setValue(null);
        refreshTable();
    }

    @FXML
    void onVehicleSelected() {
        refreshTable();
    }

    @FXML
    void onAddMaintenance() {
        createMaintenanceDialog(null);
    }

    @FXML
    void onEditMaintenance() {
        MaintenanceSchedule selected = maintenanceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a maintenance record to edit");
            return;
        }
        createMaintenanceDialog(selected);
    }

    @FXML
    void onCompleteMaintenance() {
        MaintenanceSchedule selected = maintenanceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a maintenance record to complete");
            return;
        }

        if (selected.isCompleted()) {
            showAlert("Information", "This maintenance is already completed");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Complete");
        confirmDialog.setHeaderText("Complete Maintenance");
        confirmDialog.setContentText("Mark this maintenance as completed?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dataService.markMaintenanceAsCompleted(selected);
            refreshTable();
            showAlert("Success", "Maintenance marked as completed");
        }
    }

    private void refreshTable() {
        Vehicle selected = vehicleCombo.getValue();
        if (selected == null) {
            maintenanceList = FXCollections.observableArrayList(dataService.getAllMaintenanceSchedules());
        } else {
            maintenanceList = FXCollections.observableArrayList(dataService.findMaintenanceByVehicle(selected));
        }
        maintenanceTable.setItems(maintenanceList);
        maintenanceTable.refresh();
    }

    @FXML
    void onRemoveMaintenance() {
        MaintenanceSchedule selected = maintenanceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a maintenance record to remove");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Remove");
        confirmDialog.setHeaderText("Remove Maintenance Schedule");
        confirmDialog.setContentText("Are you sure you want to remove this maintenance schedule?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dataService.removeMaintenanceSchedule(selected);
            refreshTable();
            showAlert("Success", "Maintenance record removed");
        }
    }

    private void createMaintenanceDialog(MaintenanceSchedule existingSchedule) {
        Dialog<MaintenanceSchedule> dialog = new Dialog<>();
        dialog.setTitle(existingSchedule == null ? "Add Maintenance" : "Edit Maintenance");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<Vehicle> vehicleSelect = new ComboBox<>(
                FXCollections.observableArrayList(dataService.getAllVehicles()));
        ComboBox<Workshop> workshopSelect = new ComboBox<>(
                FXCollections.observableArrayList(dataService.getAllWorkshops()));
        DatePicker dateSelect = new DatePicker(LocalDate.now());
        TextArea descArea = new TextArea();
        descArea.setWrapText(true);
        descArea.setPrefHeight(100);

        if (existingSchedule != null) {
            vehicleSelect.setValue(existingSchedule.getVehicle());
            workshopSelect.setValue(existingSchedule.getWorkshop());
            dateSelect.setValue(existingSchedule.getScheduledDate());
            descArea.setText(existingSchedule.getDescription() != null ? existingSchedule.getDescription() : "");
        }

        grid.add(new Label("Vehicle:"), 0, 0);
        grid.add(vehicleSelect, 1, 0);
        grid.add(new Label("Workshop:"), 0, 1);
        grid.add(workshopSelect, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(dateSelect, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descArea, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    Vehicle vehicle = vehicleSelect.getValue();
                    Workshop workshop = workshopSelect.getValue();
                    LocalDate date = dateSelect.getValue();
                    String description = descArea.getText().trim();

                    if (vehicle == null || workshop == null || date == null) {
                        showAlert("Error", "Vehicle, Workshop, and Date are required");
                        return null;
                    }

                    if (existingSchedule == null) {
                        MaintenanceSchedule schedule = new MaintenanceSchedule(
                                vehicle, workshop, date, description.isEmpty() ? null : description);
                        dataService.addMaintenanceSchedule(schedule);
                        showAlert("Success", "Maintenance scheduled successfully");
                    } else {
                        existingSchedule.setVehicle(vehicle);
                        existingSchedule.setWorkshop(workshop);
                        existingSchedule.setScheduledDate(date);
                        existingSchedule.setDescription(description.isEmpty() ? null : description);
                        showAlert("Success", "Maintenance updated successfully");
                    }

                    refreshTable();
                } catch (IllegalArgumentException e) {
                    showAlert("Error", e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
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
