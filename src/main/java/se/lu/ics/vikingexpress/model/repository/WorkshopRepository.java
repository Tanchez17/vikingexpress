package se.lu.ics.vikingexpress.model.repository;

import se.lu.ics.vikingexpress.model.Workshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class WorkshopRepository {
    private final List<Workshop> WORKSHOPS = new ArrayList<>();

    public void addWorkshop(Workshop workshop) {
        if (workshop == null) {
            throw new IllegalArgumentException("Workshop cannot be null.");
        }
        WORKSHOPS.add(workshop);
    }

    public void removeWorkshop(Workshop workshop) {
        WORKSHOPS.remove(workshop);
    }

    public List<Workshop> getAllWorkshops() {
        return Collections.unmodifiableList(WORKSHOPS);
    }

    public void clearAll() {
        WORKSHOPS.clear();
    }
}