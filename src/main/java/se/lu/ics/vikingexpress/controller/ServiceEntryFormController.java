package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.model.enums.VehicleType;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;
import se.lu.ics.vikingexpress.service.DataService;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceEntryFormController {

    @FXML
    private ComboBox<Vehicle> vehicleCombo;
    @FXML
    private ComboBox<Workshop> workshopCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextArea problemDescriptionArea;
    @FXML
    private TextField costField;
    @FXML
    private TextField partsReplacedField;
    @FXML
    private ListView<String> partsListView;

    private final DataService dataService = DataService.getInstance();

    @FXML
    public void initialize() {
        loadVehicles();
        loadWorkshops();
        datePicker.setValue(LocalDate.now());

        partsListView.getItems().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                partsReplacedField.setText(String.valueOf(partsListView.getItems().size()));
            }
        });
    }

    private void loadVehicles() {
        List<Vehicle> availableVehicles = dataService.getAllVehicles().stream()
                .filter(vehicle -> !vehicle.isDecommissioned())
                .collect(Collectors.toList());
        vehicleCombo.setItems(FXCollections.observableArrayList(availableVehicles));
    }

    private void loadWorkshops() {
        workshopCombo.setItems(FXCollections.observableArrayList(dataService.getAllWorkshops()));
    }

    @FXML
    void onVehicleSelected() {
        Vehicle selected = vehicleCombo.getValue();
        if (selected != null) {
            List<Workshop> availableWorkshops;
            if (selected.getType() == VehicleType.LARGE_TRUCK) {
                availableWorkshops = dataService.getAllWorkshops().stream()
                        .filter(w -> w.getType() == WorkshopType.EXTERNAL)
                        .collect(Collectors.toList());
            } else {
                availableWorkshops = dataService.getAllWorkshops();
            }
            workshopCombo.setItems(FXCollections.observableArrayList(availableWorkshops));
        } else {
            loadWorkshops();
        }
    }

    @FXML
    void onAddPart() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Add Part");
        dialog.setHeaderText("Enter part name:");
        var result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            partsListView.getItems().add(result.get().trim());
        }
    }

    @FXML
    void onRemovePart() {
        String selected = partsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            partsListView.getItems().remove(selected);
        } else {
            showAlert("Information", "Please select a part to remove");
        }
    }

    @FXML
    void onSave() {
        try {
            Vehicle vehicle = vehicleCombo.getValue();
            Workshop workshop = workshopCombo.getValue();
            LocalDate date = datePicker.getValue();
            String problemDesc = problemDescriptionArea.getText().trim();
            String costStr = costField.getText().trim();
            String partsStr = partsReplacedField.getText().trim();

            if (vehicle == null || workshop == null || date == null || problemDesc.isEmpty() ||
                    costStr.isEmpty() || partsStr.isEmpty()) {
                showAlert("Validation Error", "All fields must be filled");
                return;
            }

            double cost = Double.parseDouble(costStr);
            int parts = Integer.parseInt(partsStr);

            ServiceEntry entry = new ServiceEntry(vehicle, date, problemDesc, cost, parts, workshop);
            dataService.addServiceEntry(entry);

            clearFields();
            showAlert("Success", "Service entry saved successfully");
        } catch (NumberFormatException e) {
            showAlert("Error", "Cost and parts replaced must be valid numbers");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void onCancel() {
        clearFields();
    }

    private void clearFields() {
        vehicleCombo.setValue(null);
        workshopCombo.setValue(null);
        datePicker.setValue(LocalDate.now());
        problemDescriptionArea.clear();
        costField.clear();
        partsReplacedField.clear();
        partsListView.getItems().clear();
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
