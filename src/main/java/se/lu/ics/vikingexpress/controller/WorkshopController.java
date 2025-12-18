package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;
import se.lu.ics.vikingexpress.service.DataService;

public class WorkshopController {

    @FXML
    private TableView<Workshop> workshopTable;
    @FXML
    private TableColumn<Workshop, String> nameColumn;
    @FXML
    private TableColumn<Workshop, String> typeColumn;
    @FXML
    private TableColumn<Workshop, String> addressColumn;

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<WorkshopType> typeCombo;
    @FXML
    private TextField addressField;

    private final DataService dataService = DataService.getInstance();
    private ObservableList<Workshop> workshops;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadWorkshops();
        typeCombo.setItems(FXCollections.observableArrayList(WorkshopType.values()));
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        typeColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType().toString()));
        addressColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAddress()));
    }

    private void loadWorkshops() {
        workshops = FXCollections.observableArrayList(dataService.getAllWorkshops());
        workshopTable.setItems(workshops);
    }

    @FXML
    void onAddWorkshop() {
        try {
            String name = nameField.getText().trim();
            WorkshopType type = typeCombo.getValue();
            String address = addressField.getText().trim();

            if (name.isEmpty() || type == null || address.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled");
                return;
            }

            Workshop workshop = new Workshop(name, type, address);
            dataService.addWorkshop(workshop);

            clearFields();
            loadWorkshops();
            showAlert("Success", "Workshop added successfully");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void onEditWorkshop() {
        Workshop selected = workshopTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a workshop to edit");
            return;
        }

        try {
            String name = nameField.getText().trim();
            WorkshopType type = typeCombo.getValue();
            String address = addressField.getText().trim();

            if (name.isEmpty() || type == null || address.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled");
                return;
            }

            selected.setName(name);
            selected.setType(type);
            selected.setAddress(address);

            workshopTable.refresh();
            clearFields();
            showAlert("Success", "Workshop updated successfully");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void onDeleteWorkshop() {
        Workshop selected = workshopTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a workshop to delete");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Workshop");
        confirmDialog.setContentText("Are you sure you want to delete " + selected.getName() + "?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dataService.removeWorkshop(selected);
            loadWorkshops();
            clearFields();
            showAlert("Success", "Workshop deleted successfully");
        }
    }

    @FXML
    void onSelectWorkshop() {
        Workshop selected = workshopTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            typeCombo.setValue(selected.getType());
            addressField.setText(selected.getAddress());
        }
    }

    @FXML
    void onShowServiceHistory() {
        Workshop selected = workshopTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a workshop");
            return;
        }

        var entries = dataService.findServiceEntriesByWorkshop(selected);
        if (entries.isEmpty()) {
            showAlert("Info", "No service history for this workshop");
        } else {
            StringBuilder sb = new StringBuilder("Service History:\n");
            for (var entry : entries) {
                sb.append(String.format("Vehicle VIN %d - Date: %s - Cost: %.2f\n",
                        entry.getVehicle().getVin(), entry.getDate(), entry.getCost()));
            }
            showAlert("Service History", sb.toString());
        }
    }

    private void clearFields() {
        nameField.clear();
        typeCombo.setValue(null);
        addressField.clear();
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
