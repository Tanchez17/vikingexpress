package se.lu.ics.vikingexpress.model.repository;

import se.lu.ics.vikingexpress.model.Workshop;
import se.lu.ics.vikingexpress.model.enums.WorkshopType;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class WorkshopRepository {
    private final List<Workshop> workshops = new ArrayList<>();

    public void addWorkshop(Workshop workshop) {
        if (workshop == null) {
            throw new IllegalArgumentException("Workshop cannot be null.");
        }
        workshops.add(workshop);
    }

    public void removeWorkshop(Workshop workshop) {
        workshops.remove(workshop);
    }

    public List<Workshop> getAllWorkshops() {
        return Collections.unmodifiableList(workshops);
    }

    public List<Workshop> getWorkshopsByType(WorkshopType type) {
        List<Workshop> result = new ArrayList<>();
        for (Workshop workshop : workshops) {
            if (workshop.getType() == type) {
                result.add(workshop);
            }
        }
        return result;
    }

    public int size() {
        return workshops.size();
    }
}