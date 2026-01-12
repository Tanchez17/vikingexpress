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

    private final VehicleRepository vehicleRepository;
    private final WorkshopRepository workshopRepository;
    private final ServiceEntryRepository serviceEntryRepository;
    private final MaintenanceRepository maintenanceRepository;

    private DataService() {
        this.vehicleRepository = new VehicleRepository();
        this.workshopRepository = new WorkshopRepository();
        this.serviceEntryRepository = new ServiceEntryRepository();
        this.maintenanceRepository = new MaintenanceRepository();
    }

    public static synchronized DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.addVehicle(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        serviceEntryRepository.findByVehicle(vehicle).forEach(serviceEntryRepository::removeServiceEntry);
        maintenanceRepository.getSchedulesByVehicle(vehicle).forEach(maintenanceRepository::removeMaintenanceSchedule);
        vehicleRepository.removeVehicle(vehicle);
    }

    public Vehicle findVehicleByVin(long vin) {
        return vehicleRepository.findByVin(vin);
    }

    public java.util.List<Vehicle> getAllVehicles() {
        return vehicleRepository.getAllVehicles();
    }

    public void addWorkshop(Workshop workshop) {
        workshopRepository.addWorkshop(workshop);
    }

    public void removeWorkshop(Workshop workshop) {
        serviceEntryRepository.findByWorkshop(workshop).forEach(serviceEntryRepository::removeServiceEntry);
        maintenanceRepository.getSchedulesByWorkshop(workshop)
                .forEach(maintenanceRepository::removeMaintenanceSchedule);
        workshopRepository.removeWorkshop(workshop);
    }

    public java.util.List<Workshop> getAllWorkshops() {
        return workshopRepository.getAllWorkshops();
    }

    public void addServiceEntry(ServiceEntry entry) {
        serviceEntryRepository.addServiceEntry(entry);
    }

    public void removeServiceEntry(ServiceEntry entry) {
        serviceEntryRepository.removeServiceEntry(entry);
    }

    public java.util.List<ServiceEntry> getAllServiceEntries() {
        return serviceEntryRepository.getAllServiceEntries();
    }

    public java.util.List<ServiceEntry> findServiceEntriesByVehicle(Vehicle vehicle) {
        return serviceEntryRepository.findByVehicle(vehicle);
    }

    public java.util.List<ServiceEntry> findServiceEntriesByWorkshop(Workshop workshop) {
        return serviceEntryRepository.findByWorkshop(workshop);
    }

    public double getTotalServiceCost(Vehicle vehicle) {
        return serviceEntryRepository.getTotalServiceCost(vehicle);
    }

    public double getTotalCostForAllVehicles() {
        return serviceEntryRepository.getTotalCostForAllVehicles();
    }

    public void addMaintenanceSchedule(MaintenanceSchedule schedule) {
        maintenanceRepository.addMaintenanceSchedule(schedule);
    }

    public void removeMaintenanceSchedule(MaintenanceSchedule schedule) {
        maintenanceRepository.removeMaintenanceSchedule(schedule);
    }

    public java.util.List<MaintenanceSchedule> getAllMaintenanceSchedules() {
        return maintenanceRepository.getAllMaintenanceSchedules();
    }

    public java.util.List<MaintenanceSchedule> findMaintenanceByVehicle(Vehicle vehicle) {
        return maintenanceRepository.getSchedulesByVehicle(vehicle);
    }

    public void markMaintenanceAsCompleted(MaintenanceSchedule schedule) {
        maintenanceRepository.markAsCompleted(schedule);
    }

    public void resetAllData() {
        serviceEntryRepository.clearAll();
        maintenanceRepository.clearAll();
        vehicleRepository.clearAll();
        workshopRepository.clearAll();
    }
}
