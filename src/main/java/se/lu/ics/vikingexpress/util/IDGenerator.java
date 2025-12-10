package se.lu.ics.vikingexpress.util;

public final class IDGenerator {

    private static long vehicleCounter = 1;
    private static long workshopCounter = 1;
    private static long serviceEntryCounter = 1;
    private static long maintenanceScheduleCounter = 1;

    private IDGenerator() {

    }

    public static synchronized long nextVehicleId() {
        return vehicleCounter++;
    }

    public static synchronized long nextWorkshopId() {
        return workshopCounter++;
    }

    public static synchronized long nextServiceEntryId() {
        return serviceEntryCounter++;
    }

    public static synchronized long nextMaintenanceScheduleId() {
        return maintenanceScheduleCounter++;
    }
}
