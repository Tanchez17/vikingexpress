package se.lu.ics.vikingexpress.model;

import se.lu.ics.vikingexpress.model.enums.VehicleType;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;

import java.time.LocalDate;

public class ServiceEntry {
    private static final int MAX_PARTS_REPLACED = 100;
    private Vehicle vehicle;
    private LocalDate date;
    private String problemDescription;
    private double cost;
    private int partsReplaced;
    private Workshop workshop;

    public ServiceEntry(Vehicle vehicle, LocalDate date, String problemDescription, double cost, int partsReplaced,
            Workshop workshop) {

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (workshop == null) {
            throw new IllegalArgumentException("Workshop cannot be null.");
        }
        if (vehicle.getType() == VehicleType.LARGE_TRUCK && workshop.getType() == WorkshopType.INTERNAL) {
            throw new IllegalArgumentException("Large trucks cannot be serviced at internal workshops.");
        }

        if (date == null) {
            throw new IllegalArgumentException("Service date cannot be null.");
        }
        if (cost < 0) {
            throw new IllegalArgumentException("Service cost cannot be negative.");
        }
        if (problemDescription == null || problemDescription.isBlank()) {
            throw new IllegalArgumentException("Problem description cannot be null or empty.");
        }

        if (partsReplaced < 0) {
            throw new IllegalArgumentException("Parts replaced cannot be negative.");
        }
        if (partsReplaced > MAX_PARTS_REPLACED) {
            throw new IllegalArgumentException(
                    "A single service entry cannot have more than " + MAX_PARTS_REPLACED + " parts replaced.");
        }

        if (vehicle.isDecommissioned()) {
            throw new IllegalArgumentException("Cannot add service entry to a decommissioned vehicle.");
        }

        int currentTotalParts = vehicle.getTotalPartsReplaced();
        int newTotalParts = currentTotalParts + partsReplaced;

        if (newTotalParts > Vehicle.MAX_TOTAL_PARTS_REPLACED) {
            vehicle.setDecommissioned(true);
            throw new IllegalArgumentException(
                    "Vehicle has reached the maximum limit of " + Vehicle.MAX_TOTAL_PARTS_REPLACED
                            + " parts replaced. The vehicle has been decommissioned.");
        }

        if (newTotalParts == Vehicle.MAX_TOTAL_PARTS_REPLACED) {
            vehicle.setDecommissioned(true);
        }

        this.vehicle = vehicle;
        this.date = date;
        this.problemDescription = problemDescription;
        this.cost = cost;
        this.partsReplaced = partsReplaced;
        this.workshop = workshop;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public double getCost() {
        return cost;
    }

    public int getPartsReplaced() {
        return partsReplaced;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Service date cannot be null.");
        }
        this.date = date;
    }

    public void setProblemDescription(String problemDescription) {
        if (problemDescription == null || problemDescription.isBlank()) {
            throw new IllegalArgumentException("Problem description cannot be null or empty.");
        }
        this.problemDescription = problemDescription;
    }

    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Service cost cannot be negative.");
        }
        this.cost = cost;
    }

    public void setPartsReplaced(int partsReplaced) {
        if (partsReplaced < 0) {
            throw new IllegalArgumentException("Parts replaced cannot be negative.");
        }
        if (partsReplaced > MAX_PARTS_REPLACED) {
            throw new IllegalArgumentException(
                    "A single service entry cannot have more than " + MAX_PARTS_REPLACED + " parts replaced.");
        }

        if (vehicle != null) {
            if (vehicle.isDecommissioned()) {
                throw new IllegalArgumentException("Cannot modify service entry for a decommissioned vehicle.");
            }

            int currentTotalParts = vehicle.getTotalPartsReplaced();
            int newTotalParts = currentTotalParts - this.partsReplaced + partsReplaced;

            if (newTotalParts > Vehicle.MAX_TOTAL_PARTS_REPLACED) {
                vehicle.setDecommissioned(true);
                throw new IllegalArgumentException(
                        "Vehicle would exceed the maximum limit of " + Vehicle.MAX_TOTAL_PARTS_REPLACED
                                + " parts replaced. The vehicle has been decommissioned.");
            }

            if (newTotalParts == Vehicle.MAX_TOTAL_PARTS_REPLACED) {
                vehicle.setDecommissioned(true);
            }
        }

        this.partsReplaced = partsReplaced;
    }

    public void setVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (workshop != null && vehicle.getType() == VehicleType.LARGE_TRUCK
                && workshop.getType() == WorkshopType.INTERNAL) {
            throw new IllegalArgumentException("Large trucks cannot be serviced at internal workshops.");
        }
        if (vehicle.isDecommissioned()) {
            throw new IllegalArgumentException("Cannot assign service entry to a decommissioned vehicle.");
        }
        this.vehicle = vehicle;
    }

    public void setWorkshop(Workshop workshop) {
        if (workshop == null) {
            throw new IllegalArgumentException("Workshop cannot be null.");
        }
        if (vehicle != null && vehicle.getType() == VehicleType.LARGE_TRUCK
                && workshop.getType() == WorkshopType.INTERNAL) {
            throw new IllegalArgumentException("Large trucks cannot be serviced at internal workshops.");
        }
        this.workshop = workshop;
    }

    @Override
    public String toString() {
        return "Service on " + date + " | Cost: " + cost + " | Parts replaced: " + partsReplaced + " | Workshop: "
                + workshop.getName();
    }
}
