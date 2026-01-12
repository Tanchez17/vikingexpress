package se.lu.ics.vikingexpress.model.repository;

import se.lu.ics.vikingexpress.model.MaintenanceSchedule;
import se.lu.ics.vikingexpress.model.Vehicle;
import se.lu.ics.vikingexpress.model.Workshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class MaintenanceRepository {

    private final List<MaintenanceSchedule> maintenanceSchedules = new ArrayList<>();

    public void addMaintenanceSchedule(MaintenanceSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("Maintenance schedule cannot be null.");
        }
        maintenanceSchedules.add(schedule);
        schedule.getVehicle().addMaintenanceSchedule(schedule);
    }

    public void removeMaintenanceSchedule(MaintenanceSchedule schedule) {
        maintenanceSchedules.remove(schedule);
        schedule.getVehicle().removeMaintenanceSchedule(schedule);
    }

    public List<MaintenanceSchedule> getAllMaintenanceSchedules() {
        return Collections.unmodifiableList(maintenanceSchedules);
    }

    public List<MaintenanceSchedule> getSchedulesByVehicle(Vehicle vehicle) {
        return maintenanceSchedules.stream()
                .filter(s -> s.getVehicle().equals(vehicle))
                .toList();
    }

    public List<MaintenanceSchedule> getSchedulesByWorkshop(Workshop workshop) {
        return maintenanceSchedules.stream()
                .filter(s -> s.getWorkshop().equals(workshop))
                .toList();
    }

    public void markAsCompleted(MaintenanceSchedule schedule) {
        schedule.markCompleted(java.time.LocalDate.now());
    }

    public void clearAll() {
        for (MaintenanceSchedule schedule : new ArrayList<>(maintenanceSchedules)) {
            schedule.getVehicle().removeMaintenanceSchedule(schedule);
        }
        maintenanceSchedules.clear();
    }
}
