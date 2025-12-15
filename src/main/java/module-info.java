module vikingexpress.fleet {
    exports se.lu.ics.vikingexpress.util;
    exports se.lu.ics.vikingexpress.model;
    exports se.lu.ics.vikingexpress.model.repository;
    exports se.lu.ics.vikingexpress.controller;
    exports se.lu.ics.vikingexpress.model.enums;
    exports se.lu.ics.vikingexpress;

    requires javafx.base;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.controls;

    opens se.lu.ics.vikingexpress.controller to javafx.fxml;
}
