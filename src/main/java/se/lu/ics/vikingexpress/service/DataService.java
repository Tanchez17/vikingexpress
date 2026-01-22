package se.lu.ics.vikingexpress.service;

import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.model.MaintenanceSchedule;
import se.lu.ics.vikingexpress.model.repository.VehicleRepository;
import se.lu.ics.vikingexpress.model.repository.WorkshopRepository;
import se.lu.ics.vikingexpress.model.repository.ServiceEntryRepository;
import se.lu.ics.vikingexpress.model.repository.MaintenanceRepository;

public class DataService {
    private static DataService instance;

    private final VehicleRepository VEHICLE_REPOSITORY;
    private final WorkshopRepository WORKSHOP_REPOSITORY;
    private final ServiceEntryRepository SERVICE_ENTRY_REPOSITORY;
    private final MaintenanceRepository MAINTENANCE_REPOSITORY;

    private DataService() {
        this.VEHICLE_REPOSITORY = new VehicleRepository();
        this.WORKSHOP_REPOSITORY = new WorkshopRepository();
        this.SERVICE_ENTRY_REPOSITORY = new ServiceEntryRepository();
        this.MAINTENANCE_REPOSITORY = new MaintenanceRepository();
    }

    public static synchronized DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    public void addVehicle(Vehicle vehicle) {
        VEHICLE_REPOSITORY.addVehicle(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        SERVICE_ENTRY_REPOSITORY.findByVehicle(vehicle).forEach(SERVICE_ENTRY_REPOSITORY::removeServiceEntry);
        MAINTENANCE_REPOSITORY.getSchedulesByVehicle(vehicle).forEach(MAINTENANCE_REPOSITORY::removeMaintenanceSchedule);
        VEHICLE_REPOSITORY.removeVehicle(vehicle);
    }

    public Vehicle findVehicleByVin(long vin) {
        return VEHICLE_REPOSITORY.findByVin(vin);
    }

    public java.util.List<Vehicle> getAllVehicles() {
        return VEHICLE_REPOSITORY.getAllVehicles();
    }

    public void addWorkshop(Workshop workshop) {
        WORKSHOP_REPOSITORY.addWorkshop(workshop);
    }

    public void removeWorkshop(Workshop workshop) {
        SERVICE_ENTRY_REPOSITORY.findByWorkshop(workshop).forEach(SERVICE_ENTRY_REPOSITORY::removeServiceEntry);
        MAINTENANCE_REPOSITORY.getSchedulesByWorkshop(workshop)
                .forEach(MAINTENANCE_REPOSITORY::removeMaintenanceSchedule);
        WORKSHOP_REPOSITORY.removeWorkshop(workshop);
    }

    public java.util.List<Workshop> getAllWorkshops() {
        return WORKSHOP_REPOSITORY.getAllWorkshops();
    }

    public void addServiceEntry(ServiceEntry entry) {
        SERVICE_ENTRY_REPOSITORY.addServiceEntry(entry);
    }

    public void removeServiceEntry(ServiceEntry entry) {
        SERVICE_ENTRY_REPOSITORY.removeServiceEntry(entry);
    }

    public java.util.List<ServiceEntry> getAllServiceEntries() {
        return SERVICE_ENTRY_REPOSITORY.getAllServiceEntries();
    }

    public java.util.List<ServiceEntry> findServiceEntriesByVehicle(Vehicle vehicle) {
        return SERVICE_ENTRY_REPOSITORY.findByVehicle(vehicle);
    }

    public java.util.List<ServiceEntry> findServiceEntriesByWorkshop(Workshop workshop) {
        return SERVICE_ENTRY_REPOSITORY.findByWorkshop(workshop);
    }

    public double getTotalServiceCost(Vehicle vehicle) {
        return SERVICE_ENTRY_REPOSITORY.getTotalServiceCost(vehicle);
    }

    public double getTotalCostForAllVehicles() {
        return SERVICE_ENTRY_REPOSITORY.getTotalCostForAllVehicles();
    }

    public void addMaintenanceSchedule(MaintenanceSchedule schedule) {
        MAINTENANCE_REPOSITORY.addMaintenanceSchedule(schedule);
    }

    public void removeMaintenanceSchedule(MaintenanceSchedule schedule) {
        MAINTENANCE_REPOSITORY.removeMaintenanceSchedule(schedule);
    }

    public java.util.List<MaintenanceSchedule> getAllMaintenanceSchedules() {
        return MAINTENANCE_REPOSITORY.getAllMaintenanceSchedules();
    }

    public java.util.List<MaintenanceSchedule> findMaintenanceByVehicle(Vehicle vehicle) {
        return MAINTENANCE_REPOSITORY.getSchedulesByVehicle(vehicle);
    }

    public void markMaintenanceAsCompleted(MaintenanceSchedule schedule) {
        MAINTENANCE_REPOSITORY.markAsCompleted(schedule);
    }

    public void resetAllData() {
        SERVICE_ENTRY_REPOSITORY.clearAll();
        MAINTENANCE_REPOSITORY.clearAll();
        VEHICLE_REPOSITORY.clearAll();
        WORKSHOP_REPOSITORY.clearAll();
    }
}
