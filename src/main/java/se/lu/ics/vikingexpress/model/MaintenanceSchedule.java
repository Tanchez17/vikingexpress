package se.lu.ics.vikingexpress.model;

import se.lu.ics.vikingexpress.model.enums.VehicleType;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;

import java.time.LocalDate;

public class MaintenanceSchedule {

    private Vehicle vehicle;
    private Workshop workshop;
    private LocalDate scheduledDate;
    private String description;

    private boolean completed;
    private LocalDate completedDate;

    public MaintenanceSchedule(Vehicle vehicle, Workshop workshop, LocalDate scheduledDate, String description) {

        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (workshop == null) {
            throw new IllegalArgumentException("Workshop cannot be null.");
        }
        if (scheduledDate == null) {
            throw new IllegalArgumentException("Scheduled date cannot be null.");
        }
        if (description != null && description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank.");
        }
        if (vehicle.getType() == VehicleType.LARGE_TRUCK && workshop.getType() == WorkshopType.INTERNAL) {
            throw new IllegalArgumentException("Large trucks cannot be serviced at internal workshops.");
        }

        this.vehicle = vehicle;
        this.workshop = workshop;
        this.scheduledDate = scheduledDate;
        this.description = description;
        this.completed = false;
        this.completedDate = null;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle cannot be null.");
        }
        if (workshop != null && vehicle.getType() == VehicleType.LARGE_TRUCK
                && workshop.getType() == WorkshopType.INTERNAL) {
            throw new IllegalArgumentException("Large trucks cannot be serviced at internal workshops.");
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

    public void setScheduledDate(LocalDate scheduledDate) {
        if (scheduledDate == null) {
            throw new IllegalArgumentException("Scheduled date cannot be null.");
        }
        this.scheduledDate = scheduledDate;
    }

    public void setDescription(String description) {
        if (description != null && description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank.");
        }
        this.description = description;
    }

    public void markCompleted(LocalDate completedDate) {
        if (completedDate == null) {
            throw new IllegalArgumentException("Completed date cannot be null.");
        }
        this.completed = true;
        this.completedDate = completedDate;
    }

    public void markNotCompleted() {
        this.completed = false;
        this.completedDate = null;
    }

    @Override
    public String toString() {
        String status = completed ? "Completed on " + completedDate : "Scheduled for " + scheduledDate;
        String descPart = (description != null && !description.isBlank()) ? " | Description: " + description : "";

        return "Maintenance for vehicle VIN " + vehicle.getVin() + " at workshop " + workshop.getName() +
                " - " + status + descPart;
    }
}
