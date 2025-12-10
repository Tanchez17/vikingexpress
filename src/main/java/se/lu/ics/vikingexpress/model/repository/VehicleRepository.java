package se.lu.ics.vikingexpress.model.repository;

import se.lu.ics.vikingexpress.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class VehicleRepository {
    private final List<Vehicle> vehicles = new ArrayList<>();

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (findByVin(vehicle.getVin()) != null) {
            throw new IllegalArgumentException("Vehicle with VIN " + vehicle.getVin() + " already exists.");
        }
        vehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    public Vehicle findByVin(long vin) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getVin() == vin) {
                return vehicle;
            }
        }
        return null;
    }

    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    public int size() {
        return vehicles.size();
    }
}