package se.lu.ics.vikingexpress.model;

import se.lu.ics.vikingexpress.model.enums.WorkshopType;

public class Workshop {
    private String name;
    private WorkshopType type;
    private String address;

    public Workshop(String name, WorkshopType type, String address) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Workshop name cannot be null or empty.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Workshop type cannot be null.");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Workshop address cannot be null or empty.");
        }

        this.name = name;
        this.type = type;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public WorkshopType getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Workshop name cannot be null or empty.");
        }
        this.name = name;
    }

    public void setType(WorkshopType type) {
        if (type == null) {
            throw new IllegalArgumentException("Workshop type cannot be null.");
        }
        this.type = type;
    }

    public void setAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Workshop address cannot be null or empty.");
        }
        this.address = address;
    }

    @Override
    public String toString() {
        String typeString = type == WorkshopType.INTERNAL ? "Internal" : "External";
        return name + " (" + typeString + ", Address " + address + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Workshop workshop = (Workshop) o;
        return name.equals(workshop.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
