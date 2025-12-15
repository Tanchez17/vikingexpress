package se.lu.ics.vikingexpress;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Använd kommando: mvn clean javafx:run
// För att köra programmet från terminalen med Maven

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main-view.fxml"));

        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 700);

        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        
        primaryStage.setTitle("VikingExpress Fleet Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}