package se.lu.ics.vikingexpress.model.repository;

import se.lu.ics.vikingexpress.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class VehicleRepository {
    private final List<Vehicle> VEHICLES = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (findByVin(vehicle.getVin()) != null) {
            throw new IllegalArgumentException("Vehicle with VIN " + vehicle.getVin() + " already exists.");
        }
        VEHICLES.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        VEHICLES.remove(vehicle);
    }

    public Vehicle findByVin(long vin) {
        return VEHICLES.stream()
                .filter(v -> v.getVin() == vin)
                .findFirst()
                .orElse(null);
    }

    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(VEHICLES);
    }

    public void clearAll() {
        VEHICLES.clear();
    }
}