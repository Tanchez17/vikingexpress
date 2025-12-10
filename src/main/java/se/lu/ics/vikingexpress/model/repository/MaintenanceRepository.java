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
        List<MaintenanceSchedule> result = new ArrayList<>();
        for (MaintenanceSchedule s : maintenanceSchedules) {
            if (s.getVehicle().equals(vehicle)) {
                result.add(s);
            }
        }
        return result;
    }

    public List<MaintenanceSchedule> getSchedulesByWorkshop(Workshop workshop) {
        List<MaintenanceSchedule> result = new ArrayList<>();
        for (MaintenanceSchedule s : maintenanceSchedules) {
            if (s.getWorkshop().equals(workshop)) {
                result.add(s);
            }
        }
        return result;
    }

    public int size() {
        return maintenanceSchedules.size();
    }
}
