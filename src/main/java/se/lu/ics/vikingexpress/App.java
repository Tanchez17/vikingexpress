package se.lu.ics.vikingexpress;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        primaryStage.setTitle("VikingExpress Fleet Management");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);

        var bounds = javafx.stage.Screen.getPrimary().getVisualBounds();

        double targetW = Math.min(1400, bounds.getWidth() * 0.92);
        double targetH = Math.min(900, bounds.getHeight() * 0.90);

        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        primaryStage.setWidth(targetW);
        primaryStage.setHeight(targetH);

        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}