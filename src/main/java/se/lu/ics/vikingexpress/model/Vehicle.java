package se.lu.ics.vikingexpress.model;

import se.lu.ics.vikingexpress.model.enums.VehicleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vehicle {
    private static long vinCounter = 1;
    public static final int MAX_TOTAL_PARTS_REPLACED = 100;

    private final long VIN;
    private String name;
    private VehicleType type;
    private String currentLocation;
    private int capacity;
    private boolean decommissioned;

    private final List<ServiceEntry> SERVICE_ENTRIES = new ArrayList<>();
    private final List<MaintenanceSchedule> MAINTENANCE_SCHEDULES = new ArrayList<>();

    public Vehicle(String name, VehicleType type, String currentLocation, int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }

        this.VIN = generateVin();
        this.name = name;
        this.type = type;
        this.currentLocation = currentLocation;
        this.capacity = capacity;
        this.decommissioned = false;
    }

    private synchronized long generateVin() {
        return vinCounter++;
    }

    public long getVin() {
        return VIN;
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

    public boolean isDecommissioned() {
        return decommissioned;
    }

    public void setDecommissioned(boolean decommissioned) {
        this.decommissioned = decommissioned;
    }

    public List<ServiceEntry> getServiceEntries() {
        return Collections.unmodifiableList(SERVICE_ENTRIES);
    }

    public List<MaintenanceSchedule> getMaintenanceSchedules() {
        return Collections.unmodifiableList(MAINTENANCE_SCHEDULES);
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
        SERVICE_ENTRIES.add(entry);
    }

    public void removeServiceEntry(ServiceEntry entry) {
        SERVICE_ENTRIES.remove(entry);
    }

    public double getTotalServiceCost() {
        return SERVICE_ENTRIES.stream().mapToDouble(ServiceEntry::getCost).sum();
    }

    public int getTotalPartsReplaced() {
        return SERVICE_ENTRIES.stream().mapToInt(ServiceEntry::getPartsReplaced).sum();
    }

    public void addMaintenanceSchedule(MaintenanceSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Maintenance schedule cannot be null.");
        }
        MAINTENANCE_SCHEDULES.add(schedule);
    }

    public void removeMaintenanceSchedule(MaintenanceSchedule schedule) {
        MAINTENANCE_SCHEDULES.remove(schedule);
    }

    @Override
    public String toString() {
        return name + " (VIN: " + VIN + ", Type: " + type + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Vehicle vehicle = (Vehicle) o;
        return VIN == vehicle.VIN;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(VIN);
    }
}
