package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.service.DataService;
import java.io.IOException;
import java.util.List;

public class ServiceHistoryController {

    @FXML
    private RadioButton byVehicleRadio;
    @FXML
    private RadioButton byWorkshopRadio;
    @FXML
    private ComboBox<Vehicle> vehicleCombo;
    @FXML
    private ComboBox<Workshop> workshopCombo;
    @FXML
    private TableView<ServiceEntry> historyTable;
    @FXML
    private TableColumn<ServiceEntry, String> dateColumn;
    @FXML
    private TableColumn<ServiceEntry, String> vehicleColumn;
    @FXML
    private TableColumn<ServiceEntry, String> workshopColumn;
    @FXML
    private TableColumn<ServiceEntry, String> descriptionColumn;
    @FXML
    private TableColumn<ServiceEntry, String> costColumn;
    @FXML
    private TableColumn<ServiceEntry, String> partsColumn;

    @FXML
    private TextArea descriptionArea;
    @FXML
    private ListView<String> partsListView;
    @FXML
    private TextField costTextField;

    private final DataService dataService = DataService.getInstance();
    private ObservableList<ServiceEntry> historyList;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadVehicles();
        loadWorkshops();
        setupRadioButtons();

        refreshHistory(dataService.getAllServiceEntries());
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate().toString()));
        vehicleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                "VIN: " + cellData.getValue().getVehicle().getVin()));
        workshopColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWorkshop().getName()));
        descriptionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProblemDescription()));
        costColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f", cellData.getValue().getCost())));
        partsColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getPartsReplaced())));
    }

    private void setupRadioButtons() {
        ToggleGroup group = new ToggleGroup();
        byVehicleRadio.setToggleGroup(group);
        byWorkshopRadio.setToggleGroup(group);
        byVehicleRadio.setSelected(true);

        byVehicleRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                vehicleCombo.setDisable(false);
                vehicleCombo.setVisible(true);
                vehicleCombo.setManaged(true);
                workshopCombo.setDisable(true);
                workshopCombo.setVisible(false);
                workshopCombo.setManaged(false);
                onVehicleSelected();
            }
        });

        byWorkshopRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                workshopCombo.setDisable(false);
                workshopCombo.setVisible(true);
                workshopCombo.setManaged(true);
                vehicleCombo.setDisable(true);
                vehicleCombo.setVisible(false);
                vehicleCombo.setManaged(false);
                onWorkshopSelected();
            }
        });
    }

    private void loadVehicles() {
        ObservableList<Vehicle> vehicleItems = FXCollections.observableArrayList(dataService.getAllVehicles());
        vehicleCombo.setItems(vehicleItems);
        vehicleCombo.setValue(null);
    }

    private void loadWorkshops() {
        ObservableList<Workshop> workshopItems = FXCollections.observableArrayList(dataService.getAllWorkshops());
        workshopCombo.setItems(workshopItems);
        workshopCombo.setValue(null);
    }

    @FXML
    void onVehicleSelected() {
        Vehicle selected = vehicleCombo.getValue();
        if (selected == null) {
            refreshHistory(dataService.getAllServiceEntries());
            return;
        }
        refreshHistory(dataService.findServiceEntriesByVehicle(selected));
    }

    @FXML
    void onWorkshopSelected() {
        Workshop selected = workshopCombo.getValue();
        if (selected == null) {
            refreshHistory(dataService.getAllServiceEntries());
            return;
        }
        refreshHistory(dataService.findServiceEntriesByWorkshop(selected));
    }

    private void refreshHistory(List<ServiceEntry> entries) {
        historyList = FXCollections.observableArrayList(entries);
        historyTable.setItems(historyList);
    }

    @FXML
    void onSelectEntry() {
        ServiceEntry selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            descriptionArea.setText(selected.getProblemDescription());
            costTextField.setText(String.format("%.2f", selected.getCost()));

            ObservableList<String> parts = FXCollections.observableArrayList();
            int partsCount = selected.getPartsReplaced();
            if (partsCount > 0) {
                for (int i = 1; i <= partsCount; i++) {
                    parts.add("Part " + i);
                }
            }
            partsListView.setItems(parts);
        }
    }

    @FXML
    void onNewServiceEntry() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/service-entry-form.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Service Entry");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(historyTable.getScene().getWindow());

            Scene dialogScene = new Scene(root);
            var parentScene = historyTable.getScene();
            if (parentScene != null && !parentScene.getStylesheets().isEmpty()) {
                dialogScene.getStylesheets().addAll(parentScene.getStylesheets());
            } else {
                dialogScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }

            dialogStage.setScene(dialogScene);

            dialogStage.setOnCloseRequest(e -> {
                if (byVehicleRadio.isSelected()) {
                    onVehicleSelected();
                } else {
                    onWorkshopSelected();
                }
            });

            dialogStage.showAndWait();

            if (byVehicleRadio.isSelected()) {
                onVehicleSelected();
            } else {
                onWorkshopSelected();
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to open service entry form: " + e.getMessage());
        }
    }

    @FXML
    void onEditServiceEntry() {
        ServiceEntry selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a service entry to edit");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/service-entry-form.fxml"));
            Parent root = loader.load();
            ServiceEntryFormController controller = loader.getController();
            controller.setServiceEntry(selected);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Service Entry");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(historyTable.getScene().getWindow());

            Scene dialogScene = new Scene(root);
            var parentScene = historyTable.getScene();
            if (parentScene != null && !parentScene.getStylesheets().isEmpty()) {
                dialogScene.getStylesheets().addAll(parentScene.getStylesheets());
            } else {
                dialogScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            }

            dialogStage.setScene(dialogScene);

            dialogStage.setOnCloseRequest(e -> {
                historyTable.refresh();
            });

            dialogStage.showAndWait();
            historyTable.refresh();
        } catch (IOException e) {
            showAlert("Error", "Failed to open service entry form: " + e.getMessage());
        }
    }

    @FXML
    void onRemoveServiceEntry() {
        ServiceEntry selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select a service entry to remove");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Service Entry");
        confirmDialog.setContentText(
                "Are you sure you want to delete this service entry from " + selected.getDate() + "?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            dataService.removeServiceEntry(selected);
            if (byVehicleRadio.isSelected()) {
                onVehicleSelected();
            } else {
                onWorkshopSelected();
            }
            descriptionArea.clear();
            costTextField.clear();
            partsListView.setItems(FXCollections.observableArrayList());
            showAlert("Success", "Service entry removed successfully");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
