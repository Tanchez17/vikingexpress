package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.model.enums.VehicleType;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;
import se.lu.ics.vikingexpress.service.DataService;
import se.lu.ics.vikingexpress.util.AlertUtils;
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

    private final DataService DATASERVICE = DataService.getInstance();
    private ServiceEntry editingEntry = null;

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
        List<Vehicle> availableVehicles = DATASERVICE.getAllVehicles().stream()
                .filter(vehicle -> !vehicle.isDecommissioned())
                .collect(Collectors.toList());
        vehicleCombo.setItems(FXCollections.observableArrayList(availableVehicles));
    }

    public void setServiceEntry(ServiceEntry entry) {
        this.editingEntry = entry;
        if (entry != null) {
            vehicleCombo.setValue(entry.getVehicle());
            workshopCombo.setValue(entry.getWorkshop());
            datePicker.setValue(entry.getDate());
            problemDescriptionArea.setText(entry.getProblemDescription());
            costField.setText(String.valueOf(entry.getCost()));
            partsReplacedField.setText(String.valueOf(entry.getPartsReplaced()));
            
            // Populate parts list
            partsListView.getItems().clear();
            for (int i = 1; i <= entry.getPartsReplaced(); i++) {
                partsListView.getItems().add("Part " + i);
            }
        }
    }

    private void loadWorkshops() {
        workshopCombo.setItems(FXCollections.observableArrayList(DATASERVICE.getAllWorkshops()));
    }

    @FXML
    void onVehicleSelected() {
        Vehicle selected = vehicleCombo.getValue();
        if (selected != null) {
            List<Workshop> availableWorkshops;
            if (selected.getType() == VehicleType.LARGE_TRUCK) {
                availableWorkshops = DATASERVICE.getAllWorkshops().stream()
                        .filter(w -> w.getType() == WorkshopType.EXTERNAL)
                        .collect(Collectors.toList());
            } else {
                availableWorkshops = DATASERVICE.getAllWorkshops();
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
            AlertUtils.showAlert("Information", "Please select a part to remove");
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
                AlertUtils.showAlert("Validation Error", "All fields must be filled");
                return;
            }

            double cost = Double.parseDouble(costStr);
            int parts = Integer.parseInt(partsStr);

            if (editingEntry != null) {
                // Update existing entry
                editingEntry.setVehicle(vehicle);
                editingEntry.setWorkshop(workshop);
                editingEntry.setDate(date);
                editingEntry.setProblemDescription(problemDesc);
                editingEntry.setCost(cost);
                editingEntry.setPartsReplaced(parts);
                AlertUtils.showAlert("Success", "Service entry updated successfully");
            } else {
                // Create new entry
                ServiceEntry entry = new ServiceEntry(vehicle, date, problemDesc, cost, parts, workshop);
                DATASERVICE.addServiceEntry(entry);
                AlertUtils.showAlert("Success", "Service entry saved successfully");
            }

            clearFields();
            closeStage();
        } catch (NumberFormatException e) {
            AlertUtils.showAlert("Error", "Cost and parts replaced must be valid numbers");
        } catch (IllegalArgumentException e) {
            AlertUtils.showAlert("Error", e.getMessage());
        }
    }

    @FXML
    void onCancel() {
        clearFields();
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) vehicleCombo.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
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
}
