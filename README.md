# VikingExpress Fleet Management

A desktop application for a fictional logistics company, VikingExpress, to keep track of its vehicle fleet: vehicles, workshops, service history, planned maintenance and costs.

Built in Java and JavaFX as a group project (4 people) in the course SYSA21 at Lund University, autumn 2025. I took an informal lead role in planning and design.

## Features

- **Dashboard** – overview of the fleet, total vehicles and key cost figures
- **Vehicles** – add, edit and remove trucks with type, location and capacity
- **Workshops** – register internal and external workshops
- **Service history** – log each service with date, problem, cost (SEK) and parts replaced
- **Maintenance schedule** – plan upcoming maintenance and mark it as completed
- **Reports & statistics** – average cost, most expensive vehicle, job and workshop
- **Settings** – dark mode, warnings for high service costs and test data for demos

## How it is built

- **MVC structure:** FXML views, controllers for each screen, and a separate model layer
- **Repository pattern:** one repository per entity (vehicles, workshops, service entries, maintenance) behind a shared data service
- **Modelling first:** the system was designed with UML class and use case diagrams in Visual Paradigm before coding

## Run it

Requires Java 21 and Maven.

```
mvn clean javafx:run
```

Open **Settings → Load Test Data** to fill the app with example vehicles and services.

## Tech

Java 21 · JavaFX 21 · Maven
