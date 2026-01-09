package se.lu.ics.vikingexpress.util;

import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.model.Workshop;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public final class CostCalculator {

    private CostCalculator() {

    }

    public static double calculateTotalCostForAllVehicles(List<ServiceEntry> serviceEntries) {
        return serviceEntries.stream()
                .mapToDouble(ServiceEntry::getCost)
                .sum();
    }

    public static double calculateAverageCost(List<Vehicle> vehicles, List<ServiceEntry> serviceEntries) {
        if (vehicles.isEmpty()) {
            return 0;
        }
        return calculateTotalCostForAllVehicles(serviceEntries) / vehicles.size();
    }

    public static ServiceEntry findMostExpensiveServiceEntry(List<ServiceEntry> serviceEntries) {
        if (serviceEntries == null || serviceEntries.isEmpty()) {
            return null;
        }
        
        return serviceEntries.stream()
                .max(Comparator.comparingDouble(ServiceEntry::getCost))
                .orElse(null);
    }

    public static Workshop findMostExpensiveWorkshop(List<ServiceEntry> serviceEntries) {
        if (serviceEntries == null || serviceEntries.isEmpty()) {
            return null;
        }

        Map<Workshop, Double> costPerWorkshop = new HashMap<>();
        for (ServiceEntry entry : serviceEntries) {
            costPerWorkshop.merge(entry.getWorkshop(), entry.getCost(), Double::sum);
        }

        return costPerWorkshop.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

}