package se.lu.ics.vikingexpress.util;

public final class Validation {

    private Validation() {
    }

    public static void requireNonNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNonEmptyString(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void requireNonNegative(double value, String message) {
        if (value < 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
