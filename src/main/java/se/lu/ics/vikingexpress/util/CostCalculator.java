package se.lu.ics.vikingexpress.util;

import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.ServiceEntry;
import se.lu.ics.vikingexpress.model.Workshop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

public final class CostCalculator {

    private CostCalculator() {

    }

    public static double calculateTotalCostForVehicle(Vehicle vehicle, List<ServiceEntry> serviceEntries) {
        double sum = 0;

        for (ServiceEntry entry : serviceEntries) {
            if (entry.getVehicle().equals(vehicle)) {
                sum += entry.getCost();
            }
        }
        return sum;
    }

    public static double calculateTotalCostForAllVehicles(List<ServiceEntry> serviceEntries) {
        double sum = 0;

        for (ServiceEntry entry : serviceEntries) {
            sum += entry.getCost();
        }
        return sum;
    }

    public static double calculateAverageCost(List<Vehicle> vehicles, List<ServiceEntry> serviceEntries) {
        if (vehicles.isEmpty()) {
            return 0;
        }
        return calculateTotalCostForAllVehicles(serviceEntries) / vehicles.size();
    }

    public static ServiceEntry findMostExpensiveServiceEntry(List<ServiceEntry> serviceEntries) {
        ServiceEntry mostExpensive = null;
        double maxCost = Double.NEGATIVE_INFINITY;

        for (ServiceEntry entry : serviceEntries) {
            if (entry.getCost() > maxCost) {
                maxCost = entry.getCost();
                mostExpensive = entry;
            }
        }
        return mostExpensive;
    }

    public static Workshop findMostExpensiveWorkshop(List<ServiceEntry> serviceEntries) {
        if (serviceEntries.isEmpty()) {
            return null;
        }

        Map<Workshop, Double> costPerWorkshop = new HashMap<>();

        for (ServiceEntry entry : serviceEntries) {
            Workshop workshop = entry.getWorkshop();
            double newTotal = costPerWorkshop.getOrDefault(workshop, 0.0) + entry.getCost();
            costPerWorkshop.put(workshop, newTotal);
        }

        Workshop mostExpensiveWorkshop = null;
        double maxTotal = Double.NEGATIVE_INFINITY;

        for (Map.Entry<Workshop, Double> workshopEntry : costPerWorkshop.entrySet()) {
            if (workshopEntry.getValue() > maxTotal) {
                maxTotal = workshopEntry.getValue();
                mostExpensiveWorkshop = workshopEntry.getKey();
            }
        }
        return mostExpensiveWorkshop;
    }

    public static Set<Workshop> getWorkshopsForVehicle(Vehicle vehicle, List<ServiceEntry> serviceEntries) {
        Set<Workshop> workshops = new HashSet<>();
        for (ServiceEntry entry : serviceEntries) {
            if (entry.getVehicle().equals(vehicle)) {
                workshops.add(entry.getWorkshop());
            }
        }
        return workshops;
    }

    public static boolean isTotalCostOverLimitForVehicle(Vehicle vehicle, List<ServiceEntry> serviceEntries,
            double limit) {
        double total = calculateTotalCostForVehicle(vehicle, serviceEntries);
        return total > limit;
    }
}