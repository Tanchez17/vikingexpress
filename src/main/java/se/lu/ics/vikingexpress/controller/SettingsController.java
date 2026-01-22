package se.lu.ics.vikingexpress.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.enums.VehicleType;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;
import se.lu.ics.vikingexpress.service.DataService;
import se.lu.ics.vikingexpress.util.AlertUtils;
import java.time.LocalDate;

public class SettingsController {

    private final DataService DATASERVICE = DataService.getInstance();

    @FXML
    private ToggleButton darkModeToggle;

    @FXML
    void onLoadTestData() {
        try {
            Vehicle largeTruck1 = new Vehicle("Electric Truck", VehicleType.LARGE_TRUCK, "Stockholm", 8000);
            Vehicle largeTruck2 = new Vehicle("Volvo FH16", VehicleType.LARGE_TRUCK, "Gothenburg", 10000);
            Vehicle mediumTruck1 = new Vehicle("Tesla Cybertruck", VehicleType.MEDIUM_TRUCK, "Malmö", 12000);
            Vehicle mediumTruck2 = new Vehicle("Batmobile", VehicleType.MEDIUM_TRUCK, "Uppsala", 11000);
            Vehicle van1 = new Vehicle("Breaking Bad RV", VehicleType.VAN, "Linköping", 2000);
            Vehicle van2 = new Vehicle("Mercedes Sprinter", VehicleType.VAN, "Örebro", 1800);

            DATASERVICE.addVehicle(largeTruck1);
            DATASERVICE.addVehicle(largeTruck2);
            DATASERVICE.addVehicle(mediumTruck1);
            DATASERVICE.addVehicle(mediumTruck2);
            DATASERVICE.addVehicle(van1);
            DATASERVICE.addVehicle(van2);

            Workshop internalWorkshop1 = new Workshop("Valhalla Heavy Vehicle Service", WorkshopType.INTERNAL,
                    "Valhalla Logistics Center, Sweden");
            Workshop internalWorkshop2 = new Workshop("VikingExpress Central Workshop", WorkshopType.INTERNAL,
                    "VikingExpress HQ, Lund");
            Workshop externalWorkshop1 = new Workshop("Tesla Service Center", WorkshopType.EXTERNAL,
                    "Stockholm EV District, Sweden");
            Workshop externalWorkshop2 = new Workshop("Volvo Truck Center", WorkshopType.EXTERNAL,
                    "Addressgatan -10, Lund");

            DATASERVICE.addWorkshop(internalWorkshop1);
            DATASERVICE.addWorkshop(internalWorkshop2);
            DATASERVICE.addWorkshop(externalWorkshop1);
            DATASERVICE.addWorkshop(externalWorkshop2);

            DATASERVICE.addServiceEntry(new se.lu.ics.vikingexpress.model.ServiceEntry(
                    largeTruck1,
                    LocalDate.now().minusDays(15),
                    "Driver attempted to refuel vehicle with gasoline",
                    650.0,
                    3,
                    externalWorkshop1));

            DATASERVICE.addServiceEntry(new se.lu.ics.vikingexpress.model.ServiceEntry(
                    largeTruck2,
                    LocalDate.now().minusDays(8),
                    "Brake system inspection and fluid replacement",
                    850.0,
                    5,
                    externalWorkshop2));

            DATASERVICE.addServiceEntry(new se.lu.ics.vikingexpress.model.ServiceEntry(
                    mediumTruck1,
                    LocalDate.now().minusDays(12),
                    "Software update required to convince vehicle it is, in fact, a truck",
                    580.0,
                    1,
                    internalWorkshop1));

            DATASERVICE.addServiceEntry(new se.lu.ics.vikingexpress.model.ServiceEntry(
                    mediumTruck2,
                    LocalDate.now().minusDays(6),
                    "Stealth mode malfunction caused vehicle to remain highly visible",
                    420.0,
                    15,
                    internalWorkshop2));

            DATASERVICE.addServiceEntry(new se.lu.ics.vikingexpress.model.ServiceEntry(
                    van1,
                    LocalDate.now().minusDays(10),
                    "Interior contamination cleanup required after chemical spill",
                    320.0,
                    7,
                    internalWorkshop1));

            DATASERVICE.addServiceEntry(new se.lu.ics.vikingexpress.model.ServiceEntry(
                    van2,
                    LocalDate.now().minusDays(4),
                    "Tire replacement due to wear and tear",
                    280.0,
                    4,
                    internalWorkshop2));

            DATASERVICE.addMaintenanceSchedule(new se.lu.ics.vikingexpress.model.MaintenanceSchedule(
                    largeTruck1,
                    externalWorkshop1,
                    LocalDate.now().plusDays(30),
                    "Scheduled major service - Engine and fuel system check"));

            DATASERVICE.addMaintenanceSchedule(new se.lu.ics.vikingexpress.model.MaintenanceSchedule(
                    largeTruck2,
                    externalWorkshop2,
                    LocalDate.now().plusDays(45),
                    "Tire alignment and suspension inspection"));

            DATASERVICE.addMaintenanceSchedule(new se.lu.ics.vikingexpress.model.MaintenanceSchedule(
                    mediumTruck1,
                    internalWorkshop1,
                    LocalDate.now().plusDays(20),
                    "Scheduled diagnostics + sensor calibration + software update"));

            DATASERVICE.addMaintenanceSchedule(new se.lu.ics.vikingexpress.model.MaintenanceSchedule(
                    mediumTruck2,
                    internalWorkshop2,
                    LocalDate.now().plusDays(25),
                    "Scheduled replacement of worn-out stealth components"));

            se.lu.ics.vikingexpress.model.MaintenanceSchedule van1Maintenance = new se.lu.ics.vikingexpress.model.MaintenanceSchedule(
                    van1,
                    internalWorkshop1,
                    LocalDate.now().plusDays(15),
                    "General interior deep cleaning");
            DATASERVICE.addMaintenanceSchedule(van1Maintenance);
            DATASERVICE.markMaintenanceAsCompleted(van1Maintenance);

            DATASERVICE.addMaintenanceSchedule(new se.lu.ics.vikingexpress.model.MaintenanceSchedule(
                    van2,
                    internalWorkshop2,
                    LocalDate.now().plusDays(18),
                    "Oil + filters + brake inspection"));

            AlertUtils.showAlert("Success", "Test data loaded successfully!");
        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Could not load test data: " + e.getMessage());
        }
    }

    @FXML
    void onResetData() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Reset");
        confirmDialog.setHeaderText("Reset All Data");
        confirmDialog.setContentText("Are you sure you want to reset all application data? This cannot be undone.");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                DATASERVICE.resetAllData();
                AlertUtils.showAlert("Success", "All application data has been reset successfully!");
            } catch (Exception e) {
                AlertUtils.showAlert("Error", "Failed to reset data: " + e.getMessage());
            }
        }
    }

    @FXML
    void onToggleDarkMode() {
        if (darkModeToggle == null) {
            return;
        }

        try {
            var scene = darkModeToggle.getScene();
            if (scene == null) {
                return;
            }

            var stylesheets = scene.getStylesheets();

            stylesheets.clear();

            boolean isDarkMode = darkModeToggle.isSelected();
            if (isDarkMode) {
                String darkTheme = getClass().getResource("/css/dark-theme.css").toExternalForm();
                stylesheets.add(darkTheme);
            } else {
                String lightTheme = getClass().getResource("/css/styles.css").toExternalForm();
                stylesheets.add(lightTheme);
            }

        } catch (Exception e) {
            AlertUtils.showAlert("Error", "Failed to toggle dark mode: " + e.getMessage());
            if (darkModeToggle != null) {
                darkModeToggle.setSelected(!darkModeToggle.isSelected());
            }
        }
    }
}
