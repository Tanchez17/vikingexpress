package se.lu.ics.vikingexpress.model;

import se.lu.ics.vikingexpress.model.enums.VehicleType;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;

import java.time.LocalDate;

public class ModelIntegrationTest {

    public static void main(String[] args) {

        System.out.println("\nSTARTING MODEL INTEGRATION TESTS");

        testWorkshopValidCreation();
        testWorkshopValidationRules();
        testWorkshopEquality();

        testVehicleBasic();
        testVehicleCapacityValidation();
        testVehicleServiceAndMaintenanceLists();
        testVehicleTotals();

        testServiceEntryValidCreation();
        testServiceEntryValidationRules();

        testMaintenanceScheduleValidCreation();
        testMaintenanceScheduleValidationRules();

        testLargeTruckInternalWorkshopRule();

        System.out.println("\nALL TESTS COMPLETED (Check output for expected exceptions/messages)");
    }

    private static void testWorkshopValidCreation() {
        System.out.println("\nTEST: Workshop Valid Creation");
        Workshop internal = new Workshop("Main Workshop", WorkshopType.INTERNAL, "Lund, Sweden");
        Workshop external = new Workshop("Another Workshop", WorkshopType.EXTERNAL, "Malmö, Sweden");

        System.out.println("Internal: " + internal);
        System.out.println("External: " + external);

        System.out.println("Name: " + internal.getName());
        System.out.println("Type: " + internal.getType());
        System.out.println("Address: " + internal.getAddress());
    }

    private static void testWorkshopValidationRules() {
        System.out.println("\nTEST: Workshop Validation Rules");

        try {
            new Workshop(null, WorkshopType.EXTERNAL, "Address");
            System.out.println("ERROR: Null name accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null name rejected: " + e.getMessage());
        }

        try {
            new Workshop(" ", WorkshopType.EXTERNAL, "Address");
            System.out.println("ERROR: Blank name accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Blank name rejected: " + e.getMessage());
        }

        try {
            new Workshop("Name", null, "Address");
            System.out.println("ERROR: Null type accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null type rejected: " + e.getMessage());
        }

        try {
            new Workshop("Name", WorkshopType.EXTERNAL, null);
            System.out.println("ERROR: Null address accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null address rejected: " + e.getMessage());
        }

        Workshop ws = new Workshop("Valid Name", WorkshopType.INTERNAL, "Valid Address");
        try {
            ws.setName(null);
            System.out.println("ERROR: setName(null) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: setName(null) rejected: " + e.getMessage());
        }

        try {
            ws.setType(null);
            System.out.println("ERROR: setType(null) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: setType(null) rejected: " + e.getMessage());
        }

        try {
            ws.setAddress(null);
            System.out.println("ERROR: setAddress(null) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: setAddress(null) rejected: " + e.getMessage());
        }

        ws.setName("New Name");
        ws.setType(WorkshopType.INTERNAL);
        ws.setAddress("New Address");
        System.out.println("Updated Workshop: " + ws);
    }

    private static void testWorkshopEquality() {
        System.out.println("\nTEST: Workshop Equality & HashCode");

        Workshop ws1 = new Workshop("Workshop A", WorkshopType.INTERNAL, "Address 1");
        Workshop ws2 = new Workshop("Workshop A", WorkshopType.INTERNAL, "Address 1");
        Workshop ws3 = new Workshop("Workshop B", WorkshopType.EXTERNAL, "Address 2");

        System.out.println("ws1: " + ws1);
        System.out.println("ws2: " + ws2);
        System.out.println("ws3: " + ws3);

        System.out.println("ws1.equals(ws2):? " + ws1.equals(ws2));
        System.out.println("ws1.equals(ws3):? " + ws1.equals(ws3));

        System.out.println("ws1.hashCode() ==  ws2.hashCode():? " + (ws1.hashCode() == ws2.hashCode()));
    }

    private static void testVehicleBasic() {
        System.out.println("\nTEST: Vehicle Basic Construction");

        Vehicle v1 = new Vehicle("Volvo ", VehicleType.LARGE_TRUCK, "Lund", 20000);
        Vehicle v2 = new Vehicle("Lamborghini", VehicleType.VAN, "Malmö", 15000);

        System.out.println("Vehicle 1: " + v1);
        System.out.println("Vehicle 2: " + v2);
        System.out.println("VIN 1: " + v1.getVin());
        System.out.println("VIN 2: " + v2.getVin());
        System.out.println("VINs unique:? " + (v1.getVin() != v2.getVin()));
    }

    private static void testVehicleCapacityValidation() {
        System.out.println("\nTEST: Vehicle Capacity Validation");

        try {
            new Vehicle("Invalid Truck", VehicleType.VAN, "Nowhere", 0);
            System.out.println("ERROR: Zero capacity accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Zero capacity rejected: " + e.getMessage());
        }

        try {
            new Vehicle("Invalid Vehicle", VehicleType.VAN, "City", -500);
            System.out.println("ERROR: Negative capacity accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Negative capacity rejected: " + e.getMessage());
        }

        Vehicle v = new Vehicle("Valid Vehicle", VehicleType.MEDIUM_TRUCK, "City", 1000);
        try {
            v.setCapacity(0);
            System.out.println("ERROR: setCapacity(0) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: setCapacity(0) rejected: " + e.getMessage());
        }

        try {
            v.setCapacity(-100);
            System.out.println("ERROR: setCapacity(-100) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: setCapacity(-100) rejected: " + e.getMessage());
        }

        v.setCapacity(2000);
        System.out.println("OK: Updated Vehicle Capacity: " + v.getCapacity());
    }

    private static void testVehicleServiceAndMaintenanceLists() {
        System.out.println("\nTEST: Vehicle Service & maintenance lists");

        Vehicle v8 = new Vehicle("Test Vehicle", VehicleType.MEDIUM_TRUCK, "Lund", 12000);
        Workshop externalws = new Workshop("External Workshop", WorkshopType.EXTERNAL, "Malmö");
        Workshop internalws = new Workshop("Internal Workshop", WorkshopType.INTERNAL, "Lund");

        ServiceEntry se1 = new ServiceEntry(v8, LocalDate.of(2025, 12, 10), "Break check", 5000, 2, externalws);
        MaintenanceSchedule ms1 = new MaintenanceSchedule(v8, internalws, LocalDate.of(2025, 11, 15),
                "Annual maintenance");

        v8.addServiceEntry(se1);
        v8.addMaintenanceSchedule(ms1);

        System.out.println("Service entries count: " + v8.getServiceEntries().size());
        System.out.println("Maintenance schedules count: " + v8.getMaintenanceSchedules().size());

        System.out.println("First Service Entry: " + v8.getServiceEntries().get(0));
        System.out.println("First Maintenance Schedule: " + v8.getMaintenanceSchedules().get(0));

        try {
            v8.getServiceEntries().add(se1);
            System.out.println("ERROR: Modified unmodifiable service entries list");
        } catch (UnsupportedOperationException e) {
            System.out.println("OK: Unmodifiable service entries list enforced: " + e.getMessage());
        }

        try {
            v8.getMaintenanceSchedules().remove(0);
            System.out.println("ERROR: Was able to modify maintenanceSchedules list from outside");
        } catch (UnsupportedOperationException e) {
            System.out.println("OK: Unmodifiable maintenance schedules list enforced: " + e.getMessage());
        }
    }

    private static void testVehicleTotals() {
        System.out.println("\nTEST: Vehicle Total Cost & Parts Replaced");

        Vehicle v12 = new Vehicle("Total Test Vehicle", VehicleType.VAN, "Lomma", 5000);
        Workshop ws = new Workshop("Cost Workshop", WorkshopType.EXTERNAL, "Lomma");

        ServiceEntry se1 = new ServiceEntry(v12, LocalDate.of(2025, 10, 5), "Oil change", 1500, 1, ws);
        ServiceEntry se2 = new ServiceEntry(v12, LocalDate.of(2025, 12, 20), "Tire replacement", 20000, 5, ws);

        v12.addServiceEntry(se1);
        v12.addServiceEntry(se2);

        System.out.println("Total Service Cost: " + v12.getTotalServiceCost());
        System.out.println("Total Parts Replaced: " + v12.getTotalPartsReplaced());
    }

    private static void testServiceEntryValidCreation() {
        System.out.println("\nTEST: ServiceEntry Valid Creation");

        Vehicle ferrari = new Vehicle("Service Test Vehicle", VehicleType.MEDIUM_TRUCK, "Stockholm", 8000);
        Workshop ws = new Workshop("Service Workshop", WorkshopType.EXTERNAL, "Stockholm");

        ServiceEntry se = new ServiceEntry(ferrari, LocalDate.of(2025, 9, 15), "Engine tuning", 8000, 3, ws);

        System.out.println("Created service entry: " + se);
        System.out.println("Vehicle VIN: " + se.getVehicle().getVin());
        System.out.println("Workshop: " + se.getWorkshop().getName());
    }

    private static void testServiceEntryValidationRules() {
        System.out.println("\nTEST: ServiceEntry Validation Rules");

        Vehicle testVehicle = new Vehicle("Validation Vehicle", VehicleType.VAN, "Gothenburg", 3000);
        Workshop testWorkshop = new Workshop("Validation Workshop", WorkshopType.EXTERNAL, "Gothenburg");

        try {
            new ServiceEntry(null, LocalDate.now(), "Problem", 1000, 1, testWorkshop);
            System.out.println("ERROR: Null vehicle accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null vehicle rejected: " + e.getMessage());
        }

        try {
            new ServiceEntry(testVehicle, LocalDate.now(), "Problem", 1000, 1, null);
            System.out.println("ERROR: Null workshop accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null workshop rejected: " + e.getMessage());
        }

        try {
            new ServiceEntry(testVehicle, LocalDate.now(), null, 1000, 1, testWorkshop);
            System.out.println("ERROR: Null problem description accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null problem description rejected: " + e.getMessage());
        }

        try {
            new ServiceEntry(testVehicle, LocalDate.now(), " ", 1000, 1, testWorkshop);
            System.out.println("ERROR: Blank problem description accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Blank problem description rejected: " + e.getMessage());
        }

        try {
            new ServiceEntry(testVehicle, LocalDate.now(), "Problem", -500, 1, testWorkshop);
            System.out.println("ERROR: Negative cost accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Negative cost rejected: " + e.getMessage());
        }

        try {
            new ServiceEntry(testVehicle, LocalDate.now(), "Problem", 1000, -2, testWorkshop);
            System.out.println("ERROR: Negative parts replaced accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Negative parts replaced rejected: " + e.getMessage());
        }

        try {
            new ServiceEntry(testVehicle, LocalDate.now(), "Problem", 1000, 101, testWorkshop);
            System.out.println("ERROR: ServiceEntry with > 100 parts replaced accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: > 100 parts replaced rejected: " + e.getMessage());
        }

        ServiceEntry validEntry = new ServiceEntry(testVehicle, LocalDate.now(), "Valid Problem", 2000, 10,
                testWorkshop);

        try {
            validEntry.setCost(-5);
            System.out.println("ERROR: setCost(-5) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: setCost(-5) rejected: " + e.getMessage());
        }

        try {
            validEntry.setPartsReplaced(200);
            System.out.println("ERROR: setPartsReplaced(200) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: setPartsReplaced(200) rejected: " + e.getMessage());
        }

        try {
            validEntry.setProblemDescription(" ");
            System.out.println("ERROR: setProblemDescription(blank) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: setProblemDescription(blank) rejected: " + e.getMessage());
        }
    }

    private static void testMaintenanceScheduleValidCreation() {
        System.out.println("\nTEST: MaintenanceSchedule Valid Creation");

        Vehicle testVehicle = new Vehicle("Maintenance Test Vehicle", VehicleType.MEDIUM_TRUCK, "Uppsala", 7000);
        Workshop testWorkshop = new Workshop("Maintenance Workshop", WorkshopType.EXTERNAL, "Uppsala");

        MaintenanceSchedule ms = new MaintenanceSchedule(testVehicle, testWorkshop, LocalDate.of(2025, 8, 20),
                "Routine check");

        System.out.println("Created maintenance schedule: " + ms);
        System.out.println("Completed? " + ms.isCompleted());

        ms.markCompleted(LocalDate.of(2025, 8, 22));
        System.out.println("After marking completed:" + ms);
        System.out.println("Completed? " + ms.isCompleted());
        System.out.println("Completed Date: " + ms.getCompletedDate());

        ms.markNotCompleted();
        System.out.println("After marking not completed:" + ms);
        System.out.println("Completed? " + ms.isCompleted());
    }

    private static void testMaintenanceScheduleValidationRules() {
        System.out.println("\nTEST: MaintenanceSchedule Validation Rules");

        Vehicle testVehicle = new Vehicle("Maintenance Test Vehicle", VehicleType.VAN, "Uppsala", 4000);
        Workshop testWorkshop = new Workshop("Maintenance Workshop", WorkshopType.EXTERNAL, "Uppsala");

        try {
            new MaintenanceSchedule(null, testWorkshop, LocalDate.of(2025, 8, 20), "Routine check");
            System.out.println("ERROR: Null vehicle accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null vehicle rejected: " + e.getMessage());
        }

        try {
            new MaintenanceSchedule(testVehicle, null, LocalDate.of(2025, 8, 20), "Routine check");
            System.out.println("ERROR: Null workshop accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null workshop rejected: " + e.getMessage());
        }

        try {
            new MaintenanceSchedule(testVehicle, testWorkshop, null, "Routine check");
            System.out.println("ERROR: Null date accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Null date rejected: " + e.getMessage());
        }

        try {
            new MaintenanceSchedule(testVehicle, testWorkshop, LocalDate.of(2025, 8, 20), " ");
            System.out.println("ERROR: Blank description accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Blank description rejected: " + e.getMessage());
        }

        MaintenanceSchedule validMS = new MaintenanceSchedule(testVehicle, testWorkshop, LocalDate.of(2025, 8, 20),
                "Valid description");

        try {
            validMS.markCompleted(null);
            System.out.println("ERROR: markCompleted(null) accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: markCompleted(null) rejected: " + e.getMessage());
        }
    }

    private static void testLargeTruckInternalWorkshopRule() {
        System.out.println("\nTEST: Large Truck & Internal Workshop Rule");

        Vehicle largeTruck = new Vehicle("Big Truck", VehicleType.LARGE_TRUCK, "Lund", 30000);
        Workshop internalWS = new Workshop("Internal WS", WorkshopType.INTERNAL, "Lund");

        try {
            new ServiceEntry(largeTruck, LocalDate.now(), "Engine repair", 10000, 5, internalWS);
            System.out.println("ERROR: Large truck serviced at internal workshop accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Large truck at internal workshop rejected: " + e.getMessage());
        }

        try {
            new MaintenanceSchedule(largeTruck, internalWS, LocalDate.now().plusDays(30), "Monthly check");
            System.out.println("ERROR: Large truck maintenance at internal workshop accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: Large truck maintenance at internal workshop rejected: " + e.getMessage());
        }
    }
}