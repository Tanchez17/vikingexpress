package se.lu.ics.vikingexpress.model.repository;

import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class ServiceEntryRepository {
    private final List<ServiceEntry> SERVICE_ENTRIES = new ArrayList<>();

    public void addServiceEntry(ServiceEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Service entry cannot be null.");
        }
        SERVICE_ENTRIES.add(entry);

        entry.getVehicle().addServiceEntry(entry);
    }

    public void removeServiceEntry(ServiceEntry entry) {
        SERVICE_ENTRIES.remove(entry);
        entry.getVehicle().removeServiceEntry(entry);
    }

    public List<ServiceEntry> getAllServiceEntries() {
        return Collections.unmodifiableList(SERVICE_ENTRIES);
    }

    public List<ServiceEntry> findByVehicle(Vehicle vehicle) {
        return SERVICE_ENTRIES.stream()
                .filter(e -> e.getVehicle().equals(vehicle))
                .toList();
    }

    public List<ServiceEntry> findByWorkshop(Workshop workshop) {
        return SERVICE_ENTRIES.stream()
                .filter(e -> e.getWorkshop().equals(workshop))
                .toList();
    }

    public double getTotalServiceCost(Vehicle vehicle) {
        return SERVICE_ENTRIES.stream()
                .filter(e -> e.getVehicle().equals(vehicle))
                .mapToDouble(ServiceEntry::getCost)
                .sum();
    }

    public double getTotalCostForAllVehicles() {
        return SERVICE_ENTRIES.stream().mapToDouble(ServiceEntry::getCost).sum();
    }

    public void clearAll() {
        for (ServiceEntry entry : new ArrayList<>(SERVICE_ENTRIES)) {
            entry.getVehicle().removeServiceEntry(entry);
        }
        SERVICE_ENTRIES.clear();
    }
}
