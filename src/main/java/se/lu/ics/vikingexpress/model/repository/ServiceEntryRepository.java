package se.lu.ics.vikingexpress.model.repository;

import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class ServiceEntryRepository {
    private final List<ServiceEntry> serviceEntries = new ArrayList<>();

    public void addServiceEntry(ServiceEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Service entry cannot be null.");
        }
        serviceEntries.add(entry);

        entry.getVehicle().addServiceEntry(entry);
    }

    public void removeServiceEntry(ServiceEntry entry) {
        serviceEntries.remove(entry);
        entry.getVehicle().removeServiceEntry(entry);
    }

    public List<ServiceEntry> getAllServiceEntries() {
        return Collections.unmodifiableList(serviceEntries);
    }

    public List<ServiceEntry> findByVehicle(Vehicle vehicle) {
        List<ServiceEntry> result = new ArrayList<>();
        for (ServiceEntry e : serviceEntries) {
            if (e.getVehicle().equals(vehicle)) {
                result.add(e);
            }
        }
        return result;
    }

    public List<ServiceEntry> findByWorkshop(Workshop workshop) {
        List<ServiceEntry> result = new ArrayList<>();
        for (ServiceEntry e : serviceEntries) {
            if (e.getWorkshop().equals(workshop)) {
                result.add(e);
            }
        }
        return result;
    }

    public double getTotalServiceCost(Vehicle vehicle) {
        double total = 0;
        for (ServiceEntry e : serviceEntries) {
            if (e.getVehicle().equals(vehicle)) {
                total += e.getCost();
            }
        }
        return total;
    }

    public double getTotalCostForAllVehicles() {
        return serviceEntries.stream().mapToDouble(ServiceEntry::getCost).sum();
    }
}
