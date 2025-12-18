package se.lu.ics.vikingexpress.model;

import se.lu.ics.vikingexpress.model.enums.VehicleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vehicle {
    private static long vinCounter = 1;

    private final long vin;
    private String name;
    private VehicleType type;
    private String currentLocation;
    private int capacity;

    private final List<ServiceEntry> serviceEntries = new ArrayList<>();
    private final List<MaintenanceSchedule> maintenanceSchedules = new ArrayList<>();

    public Vehicle(String name, VehicleType type, String currentLocation, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }

        this.vin = generateVin();
        this.name = name;
        this.type = type;
        this.currentLocation = currentLocation;
        this.capacity = capacity;
    }

    private synchronized long generateVin() {
        return vinCounter++;
    }

    public long getVin() {
        return vin;
    }

    public String getName() {
        return name;
    }

    public VehicleType getType() {
        return type;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<ServiceEntry> getServiceEntries() {
        return Collections.unmodifiableList(serviceEntries);
    }

    public List<MaintenanceSchedule> getMaintenanceSchedules() {
        return Collections.unmodifiableList(maintenanceSchedules);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        this.capacity = capacity;
    }

    public void addServiceEntry(ServiceEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Service entry cannot be null.");
        }
        serviceEntries.add(entry);
    }

    public void removeServiceEntry(ServiceEntry entry) {
        serviceEntries.remove(entry);
    }

    public double getTotalServiceCost() {
        return serviceEntries.stream().mapToDouble(ServiceEntry::getCost).sum();
    }

    public int getTotalPartsReplaced() {
        return serviceEntries.stream().mapToInt(ServiceEntry::getPartsReplaced).sum();
    }

    public void addMaintenanceSchedule(MaintenanceSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Maintenance schedule cannot be null.");
        }
        maintenanceSchedules.add(schedule);
    }

    public void removeMaintenanceSchedule(MaintenanceSchedule schedule) {
        maintenanceSchedules.remove(schedule);
    }

    @Override
    public String toString() {
        return name + " (VIN: " + vin + ", Type: " + type + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Vehicle vehicle = (Vehicle) o;
        return vin == vehicle.vin;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(vin);
    }
}
